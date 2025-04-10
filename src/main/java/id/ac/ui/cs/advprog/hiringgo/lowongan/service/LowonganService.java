package id.ac.ui.cs.advprog.hiringgo.lowongan.service;

import id.ac.ui.cs.advprog.hiringgo.lowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.lowongan.model.Pendaftar;
import id.ac.ui.cs.advprog.hiringgo.lowongan.repository.LowonganRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LowonganService {

    @Autowired
    private LowonganRepository lowonganRepository;

    // Di-inject implementasi Template (DefaultLowonganRegistration)
    @Autowired
    private DefaultLowonganRegistration defaultLowonganRegistration;

    public void registerLowongan(Long lowonganId, Pendaftar pendaftar) {
        Lowongan lowongan = lowonganRepository.findById(lowonganId)
                .orElseThrow(() -> new EntityNotFoundException("Lowongan not found"));

        // Gunakan Template Method
        defaultLowonganRegistration.register(lowongan, pendaftar);

        // Simpan hasil update (misal, jumlah pendaftar)
        lowonganRepository.save(lowongan);
    }
}
