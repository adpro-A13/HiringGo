package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository;


import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PendaftaranRepository extends JpaRepository<Pendaftaran, UUID> {
    // Mengambil semua pendaftaran untuk lowongan tertentu (berdasarkan ID lowongan)
    List<Pendaftaran> findByLowonganLowonganId(UUID lowonganId);
}
