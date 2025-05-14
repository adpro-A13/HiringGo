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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LowonganServiceImpl implements LowonganService {

    @Autowired
    private LowonganRepository lowonganRepository;
    private PendaftaranRepository pendaftaranRepository;
    private final LowonganFilterService filterService = new LowonganFilterService();

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
    public List<Lowongan> filterLowongan(LowonganFilterStrategy strategy) {
        filterService.setStrategy(strategy);
        List<Lowongan> allLowongan = findAll(); // atau bisa langsung dari repository
        return filterService.filter(allLowongan);
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
        if (!lowonganRepository.existsById(lowonganId)) {
            throw new RuntimeException("Lowongan tidak ditemukan");
        }
        lowonganRepository.deleteById(lowonganId);
    }

    @Override
    @Transactional
    public void terimaPendaftar(UUID lowonganId, UUID pendaftaranId) {
        Pendaftaran pendaftaran = pendaftaranRepository.findById(pendaftaranId)
                .orElseThrow(() -> new IllegalArgumentException("Pendaftaran tidak ditemukan"));

        Lowongan lowongan = lowonganRepository.findById(lowonganId)
                .orElseThrow(() -> new IllegalArgumentException("Lowongan tidak ditemukan"));

        if (!pendaftaran.getLowongan().getLowonganId().equals(lowonganId)) {
            throw new IllegalArgumentException("Pendaftaran tidak sesuai dengan lowongan");
        }

        if (lowongan.getJumlahAsdosDiterima() >= lowongan.getJumlahAsdosDibutuhkan()) {
            throw new IllegalStateException("Lowongan sudah penuh");
        }

        // Tandai diterima
        pendaftaran.setStatus(StatusPendaftaran.DITERIMA);
        pendaftaranRepository.save(pendaftaran);

        // Update jumlah diterima
        lowongan.setJumlahAsdosDiterima(lowongan.getJumlahAsdosDiterima() + 1);

        // Tutup lowongan jika sudah penuh
        if (lowongan.getJumlahAsdosDiterima() >= lowongan.getJumlahAsdosDibutuhkan()) {
            lowongan.setStatusLowongan(String.valueOf(StatusLowongan.DITUTUP));
        }

        lowonganRepository.save(lowongan);
    }


    @Override
    public void tolakPendaftar(UUID lowonganId, UUID pendaftaranId) {
        Pendaftaran pendaftaran = pendaftaranRepository.findById(pendaftaranId)
                .orElseThrow(() -> new IllegalArgumentException("Pendaftaran tidak ditemukan"));

        Lowongan lowongan = lowonganRepository.findById(lowonganId)
                .orElseThrow(() -> new IllegalArgumentException("Lowongan tidak ditemukan"));

        if (!pendaftaran.getLowongan().getLowonganId().equals(lowonganId)) {
            throw new IllegalArgumentException("Pendaftaran tidak sesuai dengan lowongan");
        }

        pendaftaran.setStatus(StatusPendaftaran.DITOLAK);
        pendaftaranRepository.save(pendaftaran);
    }

    @Override
    public Lowongan updateLowongan(UUID id, Lowongan updatedLowongan) {
        Optional<Lowongan> existingOpt = lowonganRepository.findById(id);

        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Lowongan dengan ID " + id + " tidak ditemukan.");
        }

        Lowongan existing = existingOpt.get();
        existing.setMataKuliah(updatedLowongan.getMataKuliah());
        existing.setTahunAjaran(updatedLowongan.getTahunAjaran());
        existing.setSemester(String.valueOf(updatedLowongan.getSemester()));
        existing.setStatusLowongan(String.valueOf(updatedLowongan.getStatusLowongan()));
        existing.setJumlahAsdosDibutuhkan(updatedLowongan.getJumlahAsdosDibutuhkan());
        existing.setJumlahAsdosDiterima(updatedLowongan.getJumlahAsdosDiterima());
        existing.setJumlahAsdosPendaftar(updatedLowongan.getJumlahAsdosPendaftar());
        existing.setDaftarPendaftaran(updatedLowongan.getDaftarPendaftaran());

        return lowonganRepository.save(existing);
    }
}