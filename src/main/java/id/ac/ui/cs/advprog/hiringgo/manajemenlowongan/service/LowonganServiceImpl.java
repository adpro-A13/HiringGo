package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;


import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.LowonganFilterStrategy;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
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
        Optional<Lowongan> existing = lowonganRepository.findByIdMataKuliahAndSemesterAndTahunAjaran(
                lowongan.getIdMataKuliah(),
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
}