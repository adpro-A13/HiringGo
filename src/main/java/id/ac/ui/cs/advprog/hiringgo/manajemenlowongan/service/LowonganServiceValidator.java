package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

@Component
public class LowonganServiceValidator {
    private static final String LOWONGAN_NOT_FOUND_MSG = "Lowongan tidak ditemukan";

    private final LowonganRepository lowonganRepository;
    private final PendaftaranRepository pendaftaranRepository;

    @Autowired
    public LowonganServiceValidator(LowonganRepository lowonganRepository,
                                    PendaftaranRepository pendaftaranRepository) {
        this.lowonganRepository = lowonganRepository;
        this.pendaftaranRepository = pendaftaranRepository;
    }

    public void ensureQuotaAvailable(Lowongan lowongan) {
        if (lowongan.getJumlahAsdosPendaftar() >= lowongan.getJumlahAsdosDibutuhkan()) {
            throw new IllegalStateException("Kuota lowongan sudah penuh!");
        }
    }

    public Pair<Pendaftaran, Lowongan> validatePendaftaranAndLowongan(UUID lowonganId,
                                                                      UUID pendaftaranId,
                                                                      String username) {
        Pendaftaran pendaftaran = pendaftaranRepository.findById(pendaftaranId)
                .orElseThrow(() -> new IllegalArgumentException("Pendaftaran tidak ditemukan"));

        Lowongan lowongan = lowonganRepository.findById(lowonganId)
                .orElseThrow(() -> new IllegalArgumentException(LOWONGAN_NOT_FOUND_MSG));

        if (!pendaftaran.getLowongan().getLowonganId().equals(lowonganId)) {
            throw new IllegalArgumentException("Pendaftaran tidak sesuai dengan lowongan");
        }

        if (lowongan.getMataKuliah().getDosenPengampu().stream()
                .noneMatch(d -> d.getUsername().equals(username))) {
            throw new AccessDeniedException("Anda bukan dosen pengampu mata kuliah ini.");
        }

        return Pair.of(pendaftaran, lowongan);
    }

    public void validateStatusAndCapacity(Pendaftaran pendaftaran, Lowongan lowongan) {
        if (pendaftaran.getStatus() == StatusPendaftaran.DITERIMA ||
                pendaftaran.getStatus() == StatusPendaftaran.DITOLAK) {
            throw new IllegalStateException("Pendaftar ini sudah " +
                    pendaftaran.getStatus().name().toLowerCase().replace("_", " "));
        }

        if (lowongan.getJumlahAsdosDiterima() >= lowongan.getJumlahAsdosDibutuhkan()) {
            throw new IllegalStateException("Lowongan sudah penuh");
        }
    }

    public Lowongan getAuthorizedLowongan(UUID lowonganId) {
        Lowongan existing = lowonganRepository.findById(lowonganId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, LOWONGAN_NOT_FOUND_MSG));

        String currentUsername = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        if (isNotAuthorizedDosenPengampu(existing, currentUsername)) {
            throw new AccessDeniedException("Anda bukan pengampu mata kuliah ini.");
        }

        return existing;
    }

    public boolean isNotAuthorizedDosenPengampu(Lowongan lowongan, String username) {
        return lowongan.getMataKuliah().getDosenPengampu().stream()
                .noneMatch(d -> d.getUsername().equals(username));
    }

    public void validateLowonganCombinationIsUnique(Lowongan lowongan) {
        Optional<Lowongan> existing = lowonganRepository
                .findByMataKuliahAndSemesterAndTahunAjaranAndJumlahAsdosDibutuhkan(
                        lowongan.getMataKuliah(),
                        lowongan.getSemester(),
                        lowongan.getTahunAjaran(),
                        lowongan.getJumlahAsdosDiterima()
                );

        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lowongan dengan kombinasi yang sama sudah ada.");
        }
    }

}