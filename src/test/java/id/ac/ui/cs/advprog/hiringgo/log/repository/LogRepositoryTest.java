package id.ac.ui.cs.advprog.hiringgo.log.repository;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LogRepositoryTest {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testSaveAndFindById() {
        Log log = new Log.Builder()
                .judul("Asistensi")
                .kategori(LogKategori.ASISTENSI)
                .waktuMulai(LocalTime.of(9, 0))
                .waktuSelesai(LocalTime.of(11, 0))
                .tanggalLog(LocalDate.now())
                .build();

        Log saved = logRepository.save(log);
        assertNotNull(saved.getId());

        Log found = logRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Asistensi", found.getJudul());
    }

    @Test
    public void testFindByStatus() {
        Log log = new Log.Builder()
                .judul("Koreksi")
                .kategori(LogKategori.MENGOREKSI)
                .tanggalLog(LocalDate.now())
                .status(LogStatus.DITERIMA)
                .build();

        logRepository.save(log);

        List<Log> logs = logRepository.findByStatus(LogStatus.DITERIMA);
        assertFalse(logs.isEmpty());
        assertEquals(LogStatus.DITERIMA, logs.get(0).getStatus());
    }

    @Test
    void testFindLogsByDosenMataKuliah() {

        // Simpan entitas yang relevan (dosen, mata kuliah, lowongan, pendaftaran, log)
        Dosen dosen = new Dosen("professor@example.com", "password", "Prof. Name", "12345");
        entityManager.persist(dosen);
        entityManager.flush();
        UUID dosenId = dosen.getId();

        MataKuliah mk = new MataKuliah(
                "CSCM602023-01.00.12.01-2020",
                "Pemrograman Lanjut",
                "Membahas Java & Spring Boot"
        );
        mk.addDosenPengampu(dosen);
        entityManager.persist(mk);
        entityManager.flush();


        Lowongan lo = new Lowongan(
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                mk,
                "2024/2025",
                "GANJIL",
                3
        );
        Lowongan mergedLowongan = entityManager.merge(lo);
        entityManager.flush();

        UUID pendaftaranId = UUID.randomUUID();
        Mahasiswa kandidat = new Mahasiswa("test@example.com", "password", "Test User", "12345678");
        entityManager.persist(kandidat);
        BigDecimal ipk = new BigDecimal("3.75");
        int sks = 120;
        LocalDateTime waktuDaftar = LocalDateTime.now();

        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(pendaftaranId);
        pendaftaran.setLowongan(mergedLowongan);
        pendaftaran.setKandidat(kandidat);
        pendaftaran.setIpk(ipk);
        pendaftaran.setSks(sks);
        pendaftaran.setWaktuDaftar(waktuDaftar);
        Pendaftaran mergedPendaftaran = entityManager.merge(pendaftaran);
        entityManager.flush();

        Log log = new Log();
        log.setPendaftaran(mergedPendaftaran);
        entityManager.persist(log);

        entityManager.flush();

        List<Log> result = logRepository.findLogsByDosenMataKuliah(dosenId);
        assertEquals(1, result.size());
    }
}