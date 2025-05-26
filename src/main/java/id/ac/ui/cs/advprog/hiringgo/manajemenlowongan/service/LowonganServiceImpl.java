package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.event.NotifikasiEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LowonganServiceImpl implements LowonganService {
    private static final String CONFLICT_MSG = "Lowongan dengan kombinasi tersebut sudah ada!";

    private final LowonganRepository lowonganRepository;
    private final PendaftaranRepository pendaftaranRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final LowonganServiceValidator validator;

    @Override
    public Lowongan findById(UUID id) {
        return lowonganRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lowongan tidak ditemukan"));
    }

    @Override
    public List<Lowongan> findAll() {
        return lowonganRepository.findAll();
    }

    @Override
    public List<Lowongan> findAllByDosenUsername(String username) {
        return lowonganRepository.findAll().stream()
                .filter(low -> low.getMataKuliah().getDosenPengampu().stream()
                        .anyMatch(d -> d.getUsername().equals(username)))
                .toList();
    }

    @Override
    public void registerLowongan(UUID lowonganId, String candidateId) {
        Lowongan lowongan = findById(lowonganId);
        validator.ensureQuotaAvailable(lowongan);
        lowongan.setJumlahAsdosPendaftar(lowongan.getJumlahAsdosPendaftar() + 1);
        lowonganRepository.save(lowongan);
    }

    @Override
    public void deleteLowonganById(UUID lowonganId) {
        Lowongan lowongan = findById(lowonganId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (validator.isNotAuthorizedDosenPengampu(lowongan, username)) {
            throw new AccessDeniedException("Anda bukan pengampu mata kuliah ini.");
        }
        boolean adaPendaftaran = !pendaftaranRepository.findByLowonganLowonganId(lowonganId).isEmpty();
        if (adaPendaftaran) {
            throw new IllegalStateException("Lowongan ini tidak dapat dihapus karena masih memiliki pendaftaran.");
        }
        lowonganRepository.deleteById(lowonganId);
    }

    @Override
    @Transactional
    public void terimaPendaftar(UUID lowonganId, UUID pendaftaranId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Pair<Pendaftaran, Lowongan> result = validator.validatePendaftaranAndLowongan(lowonganId, pendaftaranId, username);
        Pendaftaran pendaftaran = result.getFirst();
        Lowongan lowongan = result.getSecond();

        validator.validateStatusAndCapacity(pendaftaran, lowongan);

        prosesPenerimaan(pendaftaran, lowongan);
        kirimNotifikasi(pendaftaran, lowongan);
    }

    @Override
    public Lowongan createLowongan(Lowongan lowongan) {
        Optional<Lowongan> existing = lowonganRepository
                .findByMataKuliahAndSemesterAndTahunAjaran(
                        lowongan.getMataKuliah(),
                        lowongan.getSemester(),
                        lowongan.getTahunAjaran()
                );
        if (existing.isPresent()) {
            throw new IllegalStateException(CONFLICT_MSG);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (validator.isNotAuthorizedDosenPengampu(lowongan, username)) {
            throw new AccessDeniedException("Anda bukan pengampu mata kuliah ini.");
        }
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);
        return lowonganRepository.save(lowongan);
    }

    @Override
    @Transactional
    public void tolakPendaftar(UUID lowonganId, UUID pendaftaranId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Pair<Pendaftaran, Lowongan> result = validator.validatePendaftaranAndLowongan(lowonganId, pendaftaranId, username);
        Pendaftaran pendaftaran = result.getFirst();
        Lowongan lowongan = result.getSecond();

        validator.validateStatusAndCapacity(pendaftaran, lowongan);

        pendaftaran.setStatus(StatusPendaftaran.DITOLAK);
        pendaftaranRepository.save(pendaftaran);
        kirimNotifikasi(pendaftaran, lowongan);
    }

    @Override
    public Lowongan updateLowongan(UUID id, Lowongan updated) {
        Lowongan existing = validator.getAuthorizedLowongan(id);
        validator.validateLowonganCombinationIsUnique(updated);

        existing.setTahunAjaran(updated.getTahunAjaran());
        existing.setSemester(String.valueOf(updated.getSemester()));
        existing.setStatusLowongan(String.valueOf(updated.getStatusLowongan()));
        existing.setJumlahAsdosDibutuhkan(updated.getJumlahAsdosDibutuhkan());

        return lowonganRepository.save(existing);
    }

    private void prosesPenerimaan(Pendaftaran pendaftaran, Lowongan lowongan) {
        pendaftaran.setStatus(StatusPendaftaran.DITERIMA);
        pendaftaranRepository.save(pendaftaran);

        lowongan.setJumlahAsdosDiterima(lowongan.getJumlahAsdosDiterima() + 1);
        if (lowongan.getJumlahAsdosDiterima() >= lowongan.getJumlahAsdosDibutuhkan()) {
            lowongan.setStatusLowongan(StatusLowongan.DITUTUP.toString());
        }
        lowonganRepository.save(lowongan);
    }

    private void kirimNotifikasi(Pendaftaran pendaftaran, Lowongan lowongan) {
        NotifikasiEvent event = new NotifikasiEvent(
                pendaftaran.getKandidat(),
                lowongan.getMataKuliah(),
                lowongan.getTahunAjaran(),
                lowongan.getSemester(),
                pendaftaran.getStatus().getValue()
        );
        eventPublisher.publishEvent(event);
    }
}