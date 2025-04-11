package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LowonganRepository extends JpaRepository<Lowongan, UUID> {
    // Sudah ada method berikut ini dari JpaRepository
    // - findById(UUID id)
    // - findAll()
    // - save(Lowongan entity)
    // - deleteById(UUID id)
}
