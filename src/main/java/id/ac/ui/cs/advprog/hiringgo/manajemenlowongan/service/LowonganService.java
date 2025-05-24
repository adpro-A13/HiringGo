package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.LowonganFilterStrategy;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;

import java.util.List;
import java.util.UUID;

public interface LowonganService {
    Lowongan findById(UUID id);
    List<Lowongan> findAll();
    Lowongan createLowongan(Lowongan lowongan);
    Lowongan updateLowongan(UUID id, Lowongan updatedLowongan);
    void deleteLowonganById(UUID id);
    void registerLowongan(UUID lowonganId, String candidateId);
    void terimaPendaftar(UUID lowonganId, UUID pendaftaranId);
    void tolakPendaftar(UUID lowonganId, UUID pendaftaranId);
    List<Lowongan> findAllByDosenUsername(String username);

}
