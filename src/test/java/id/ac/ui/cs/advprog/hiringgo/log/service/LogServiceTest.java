package id.ac.ui.cs.advprog.hiringgo.log.service;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Log log = new Log.Builder()
                .judul("Asistensi")
                .kategori(LogKategori.ASISTENSI)
                .tanggalLog(LocalDate.now())
                .waktuMulai(LocalTime.of(9, 0))
                .waktuSelesai(LocalTime.of(10, 0))
                .build();

        when(logRepository.save(any())).thenReturn(log);

        Log result = logService.createLog(log);

        assertEquals(LogStatus.MENUNGGU, result.getStatus());
        verify(logRepository).save(log);
    }

    @Test
    void testCreateLogInvalidTime() {
        Log log = new Log.Builder()
                .waktuMulai(LocalTime.of(14, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .build();

        Exception e = assertThrows(IllegalArgumentException.class, () -> logService.createLog(log));

        assertTrue(e.getMessage().contains("Waktu mulai harus sebelum"));
    }

    @Test
    void testUpdateStatus() {
        Log log = new Log.Builder()
                .status(LogStatus.MENUNGGU)
                .build();

        when(logRepository.findById(1L)).thenReturn(Optional.of(log));
        when(logRepository.save(any())).thenReturn(log);

        Log updated = logService.updateStatus(1L, LogStatus.DITERIMA);

        assertEquals(LogStatus.DITERIMA, updated.getStatus());
    }
}