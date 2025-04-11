package id.ac.ui.cs.advprog.hiringgo.log.service;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.repository.LogRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LogServiceTest {

    private LogRepository logRepository;
    private LogService logService;

    @BeforeEach
    void setUp() {
        logRepository = mock(LogRepository.class);
        logService = new LogServiceImpl(logRepository);
    }

    @Test
    void testCreateLogValid() {
        Log log = new Log();
        log.setJudul("Asistensi");
        log.setKategori(LogKategori.ASISTENSI);
        log.setTanggalLog(LocalDate.now());
        log.setWaktuMulai(LocalTime.of(9, 0));
        log.setWaktuSelesai(LocalTime.of(10, 0));

        when(logRepository.save(any())).thenReturn(log);

        Log result = logService.createLog(log);

        assertEquals(LogStatus.MENUNGGU, result.getStatus());
        verify(logRepository).save(log);
    }

    @Test
    void testCreateLogInvalidTime() {
        Log log = new Log();
        log.setWaktuMulai(LocalTime.of(14, 0));
        log.setWaktuSelesai(LocalTime.of(12, 0));

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            logService.createLog(log);
        });

        assertTrue(e.getMessage().contains("Waktu mulai harus sebelum"));
    }

    @Test
    void testUpdateStatus() {
        Log log = new Log();
        log.setId(1L);
        log.setStatus(LogStatus.MENUNGGU);

        when(logRepository.findById(1L)).thenReturn(Optional.of(log));
        when(logRepository.save(any())).thenReturn(log);

        Log updated = logService.updateStatus(1L, LogStatus.DITERIMA);

        assertEquals(LogStatus.DITERIMA, updated.getStatus());
    }
}

