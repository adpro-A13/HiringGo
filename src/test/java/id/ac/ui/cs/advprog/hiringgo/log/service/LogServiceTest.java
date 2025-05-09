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
import java.util.List;

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

    @Test
    void testGetLogById() {
        Log log = new Log.Builder()
                .judul("Review")
                .build();

        when(logRepository.findById(1L)).thenReturn(Optional.of(log));

        Optional<Log> found = logService.getLogById(1L);

        assertTrue(found.isPresent());
        assertEquals("Review", found.get().getJudul());
    }

    @Test
    void testGetLogByIdNotFound() {
        when(logRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Log> found = logService.getLogById(99L);

        assertFalse(found.isPresent());
    }

    @Test
    void testGetAllLogs() {
        Log log1 = new Log.Builder().judul("Asistensi").build();
        Log log2 = new Log.Builder().judul("Sidang").build();

        when(logRepository.findAll()).thenReturn(List.of(log1, log2));

        List<Log> logs = logService.getAllLogs();

        assertEquals(2, logs.size());
        verify(logRepository).findAll();
    }

    @Test
    void testUpdateLog() {
        Log existing = new Log.Builder()
                .judul("Asistensi")
                .waktuMulai(LocalTime.of(8, 0))
                .waktuSelesai(LocalTime.of(9, 0))
                .build();

        Log updated = new Log.Builder()
                .judul("Sidang")
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(11, 0))
                .build();

        when(logRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(logRepository.save(any())).thenReturn(updated);

        Log result = logService.updateLog(1L, updated);

        assertEquals("Sidang", result.getJudul());
        assertEquals(LocalTime.of(10, 0), result.getWaktuMulai());
    }

    @Test
    void testDeleteLog() {
        doNothing().when(logRepository).deleteById(1L);

        assertDoesNotThrow(() -> logService.deleteLog(1L));
        verify(logRepository).deleteById(1L);
    }
}