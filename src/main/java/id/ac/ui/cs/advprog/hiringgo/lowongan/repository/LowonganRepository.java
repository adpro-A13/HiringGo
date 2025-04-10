package id.ac.ui.cs.advprog.hiringgo.lowongan.repository;

import id.ac.ui.cs.advprog.hiringgo.lowongan.model.Lowongan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LowonganRepository extends JpaRepository<Lowongan, Long> {
    // JpaRepository sudah menyediakan CRUD dasar
}
