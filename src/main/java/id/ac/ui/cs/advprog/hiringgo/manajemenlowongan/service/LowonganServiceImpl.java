package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;


import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.LowonganFilterStrategy;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return lowonganRepository.save(lowongan);
    }

    @Override
    public void registerLowongan(UUID lowonganId, String candidateId) {
        Lowongan lowongan = findById(lowonganId);
        // Check if the quota has been reached
        if (lowongan.getJumlahAsdosPendaftar() >= lowongan.getJumlahAsdosDibutuhkan()) {
            throw new IllegalStateException("Kuota lowongan sudah penuh!");
        }
        lowongan.setJumlahAsdosPendaftar(lowongan.getJumlahAsdosPendaftar() + 1);
        lowonganRepository.save(lowongan);
    }
}
