package id.ac.ui.cs.advprog.hiringgo.matakuliah.repository;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MataKuliahRepository extends JpaRepository<MataKuliah, String> {
    // Secara default, sudah mendapatkan method:
    // - save(MataKuliah entity)
    // - findById(String id)
    // - findAll()
    // - deleteById(String id)
    // dsb. tanpa menulis implementasi manual dari JPA
}