package id.ac.ui.cs.advprog.hiringgo.log.repository;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LogRepositoryTest {

    @Autowired
    private LogRepository logRepository;

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
}