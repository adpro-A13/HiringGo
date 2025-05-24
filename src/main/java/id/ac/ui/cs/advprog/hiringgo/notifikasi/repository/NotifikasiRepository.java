package id.ac.ui.cs.advprog.hiringgo.notifikasi.repository;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.model.Notifikasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotifikasiRepository extends JpaRepository<Notifikasi, UUID> {
    List<Notifikasi> findByMahasiswa(Mahasiswa mahasiswa);

    List<Notifikasi> findByMahasiswaAndReadFalse(Mahasiswa mahasiswa);
}
