package id.ac.ui.cs.advprog.hiringgo.repository;

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
        Log log = new Log();
        log.setJudul("Asistensi");
        log.setKategori(LogKategori.ASISTENSI);
        log.setWaktuMulai(LocalTime.of(9, 0));
        log.setWaktuSelesai(LocalTime.of(11, 0));
        log.setTanggalLog(LocalDate.now());

        Log saved = logRepository.save(log);
        assertNotNull(saved.getId());

        Log found = logRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Asistensi", found.getJudul());
    }

    @Test
    public void testFindByStatus() {
        Log log = new Log();
        log.setJudul("Koreksi");
        log.setKategori(LogKategori.MENGOREKSI);
        log.setTanggalLog(LocalDate.now());
        log.setStatus(LogStatus.DITERIMA);

        logRepository.save(log);

        List<Log> logs = logRepository.findByStatus(LogStatus.DITERIMA);
        assertFalse(logs.isEmpty());
        assertEquals(LogStatus.DITERIMA, logs.get(0).getStatus());
    }
}
