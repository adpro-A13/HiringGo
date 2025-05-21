package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;


import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.LowonganFilterStrategy;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LowonganServiceImpl implements LowonganService {

    private final LowonganRepository lowonganRepository;
    private final PendaftaranRepository pendaftaranRepository;
    private final LowonganFilterService filterService;

    @Autowired
    public LowonganServiceImpl(LowonganRepository lowonganRepository, PendaftaranRepository pendaftaranRepository) {
        this.lowonganRepository = lowonganRepository;
        this.pendaftaranRepository = pendaftaranRepository;
        this.filterService = new LowonganFilterService(); // atau juga inject kalau bisa
    }

    @Override
    public Lowongan findById(UUID id) {
        return lowonganRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lowongan tidak ditemukan"));
    }

    @Override
    public List<Lowongan> findAll() {
        return lowonganRepository.findAll();
    }

    @Override
    public List<Lowongan> findAllByDosenUsername(String username) {
        List<Lowongan> allLowongan = lowonganRepository.findAll();

        return allLowongan.stream()
                .filter(low -> low.getMataKuliah().getDosenPengampu()
                        .stream()
                        .anyMatch(d -> d.getUsername().equals(username)))
                .collect(Collectors.toList());
    }

    // Overloaded method
    public List<Lowongan> filterLowongan(LowonganFilterStrategy strategy, List<Lowongan> lowonganList) {
        filterService.setStrategy(strategy);
        return filterService.filter(lowonganList);
    }


    @Override
    public Lowongan createLowongan(Lowongan lowongan) {
        Optional<Lowongan> existing = lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                lowongan.getMataKuliah(),
                lowongan.getSemester(),
                lowongan.getTahunAjaran()
        );

        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lowongan dengan kombinasi tersebut sudah ada!");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Logged in username: " + username + "tes");

        if (isNotAuthorizedDosenPengampu(lowongan, username)) {
            throw new AccessDeniedException("Anda bukan pengampu mata kuliah ini.");
        }
        // Set default values
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);

        return lowonganRepository.save(lowongan);
    }



    private void ensureQuotaAvailable(Lowongan lowongan) {
        if (lowongan.getJumlahAsdosPendaftar() >= lowongan.getJumlahAsdosDibutuhkan()) {
            throw new IllegalStateException("Kuota lowongan sudah penuh!");
        }
    }

    @Override
    public void registerLowongan(UUID lowonganId, String candidateId) {
        Lowongan lowongan = findById(lowonganId);
        ensureQuotaAvailable(lowongan);
        lowongan.setJumlahAsdosPendaftar(lowongan.getJumlahAsdosPendaftar() + 1);
        lowonganRepository.save(lowongan);
    }

    @Override
    public void deleteLowonganById(UUID lowonganId) {
        Lowongan lowongan = lowonganRepository.findById(lowonganId)
                .orElseThrow(() -> new RuntimeException("Lowongan tidak ditemukan"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (isNotAuthorizedDosenPengampu(lowongan, username)) {
            throw new AccessDeniedException("Anda bukan pengampu mata kuliah ini.");
        }

        lowonganRepository.deleteById(lowonganId);
    }

    @Override
    @Transactional
    public void terimaPendaftar(UUID lowonganId, UUID pendaftaranId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var result = validasiPendaftaranDanLowongan(lowonganId, pendaftaranId, username);

        Pendaftaran pendaftaran = result.getFirst();
        Lowongan lowongan = result.getSecond();

        getAuthorizedLowongan(lowonganId);

        if (lowongan.getJumlahAsdosDiterima() >= lowongan.getJumlahAsdosDibutuhkan()) {
            throw new IllegalStateException("Lowongan sudah penuh");
        }

        pendaftaran.setStatus(StatusPendaftaran.DITERIMA);
        pendaftaranRepository.save(pendaftaran);

        lowongan.setJumlahAsdosDiterima(lowongan.getJumlahAsdosDiterima() + 1);
        if (lowongan.getJumlahAsdosDiterima() >= lowongan.getJumlahAsdosDibutuhkan()) {
            lowongan.setStatusLowongan(String.valueOf(StatusLowongan.DITUTUP));
        }
        lowonganRepository.save(lowongan);
    }



    @Override
    public void tolakPendaftar(UUID lowonganId, UUID pendaftaranId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var result = validasiPendaftaranDanLowongan(lowonganId, pendaftaranId, username);

        getAuthorizedLowongan(lowonganId);

        Pendaftaran pendaftaran = result.getFirst();
        pendaftaran.setStatus(StatusPendaftaran.DITOLAK);
        pendaftaranRepository.save(pendaftaran);
    }

    @Override
    public Lowongan updateLowongan(UUID id, Lowongan updatedLowongan) {
        Lowongan existing = getAuthorizedLowongan(id);

        Optional<Lowongan> kombinasiAda = lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                updatedLowongan.getMataKuliah(),
                updatedLowongan.getSemester(),
                updatedLowongan.getTahunAjaran()
        );

        if (kombinasiAda.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lowongan dengan kombinasi tersebut sudah ada!");
        }

        existing.setTahunAjaran(updatedLowongan.getTahunAjaran());
        existing.setSemester(String.valueOf(updatedLowongan.getSemester()));
        existing.setStatusLowongan(String.valueOf(updatedLowongan.getStatusLowongan()));
        existing.setJumlahAsdosDibutuhkan(updatedLowongan.getJumlahAsdosDibutuhkan());

        return lowonganRepository.save(existing);
    }


    private Pair<Pendaftaran, Lowongan> validasiPendaftaranDanLowongan(UUID lowonganId, UUID pendaftaranId, String username) {
        Pendaftaran pendaftaran = pendaftaranRepository.findById(pendaftaranId)
                .orElseThrow(() -> new IllegalArgumentException("Pendaftaran tidak ditemukan"));

        Lowongan lowongan = lowonganRepository.findById(lowonganId)
                .orElseThrow(() -> new IllegalArgumentException("Lowongan tidak ditemukan"));

        if (!pendaftaran.getLowongan().getLowonganId().equals(lowonganId)) {
            throw new IllegalArgumentException("Pendaftaran tidak sesuai dengan lowongan");
        }

        if (!lowongan.getMataKuliah().getDosenPengampu().stream()
                .anyMatch(d -> d.getUsername().equals(username))) {
            throw new AccessDeniedException("Anda bukan dosen pengampu mata kuliah ini.");
        }

        return Pair.of(pendaftaran, lowongan);
    }

    private boolean isNotAuthorizedDosenPengampu(Lowongan lowongan, String username) {
        return lowongan.getMataKuliah().getDosenPengampu()
                .stream()
                .noneMatch(d -> d.getUsername().equals(username));
    }

    private Lowongan getAuthorizedLowongan(UUID lowonganId) {
        Lowongan existing = lowonganRepository.findById(lowonganId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lowongan not found"));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (isNotAuthorizedDosenPengampu(existing, currentUsername)) {
            throw new AccessDeniedException("Anda bukan pengampu mata kuliah ini.");
        }

        return existing;
    }
}