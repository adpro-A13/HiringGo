package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface LowonganRepository extends JpaRepository<Lowongan, UUID> {
    Optional<Lowongan> findByMataKuliahAndSemesterAndTahunAjaran(MataKuliah mataKuliah, Semester semester, String tahunAjaran);
    Optional<Lowongan> findByMataKuliahAndSemesterAndTahunAjaranAndJumlahAsdosDibutuhkan(MataKuliah mataKuliah, Semester semester, String tahunAjaran, int jumlahAsdosDibutuhkan);
    List<Lowongan> findByStatusLowongan(StatusLowongan statusLowongan);
    List<Lowongan> findByMataKuliah(MataKuliah mataKuliah);
}
