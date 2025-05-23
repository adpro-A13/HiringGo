package id.ac.ui.cs.advprog.hiringgo.matakuliah.repository;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MataKuliahRepository extends JpaRepository<MataKuliah, String> {
    Optional<MataKuliah> findByKode(String kode);
    List<MataKuliah> findByDosenPengampu(Dosen dosen);
}