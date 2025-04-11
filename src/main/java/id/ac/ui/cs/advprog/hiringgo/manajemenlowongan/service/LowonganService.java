package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.LowonganFilterStrategy;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;

import java.util.List;
import java.util.UUID;

public interface LowonganService {
    Lowongan findById(UUID id);
    List<Lowongan> findAll();
    List<Lowongan> filterLowongan(LowonganFilterStrategy strategy);
    Lowongan createLowongan(Lowongan lowongan);

    // The method that doesn't exist yet
    void registerLowongan(UUID lowonganId, String candidateId);
}
