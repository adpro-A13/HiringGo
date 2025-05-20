package id.ac.ui.cs.advprog.hiringgo.log.service;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
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

    @Test
    void testGetLogsByStatus() {
        // Arrange
        LogStatus status = LogStatus.DITERIMA;
        Log log1 = new Log.Builder()
                .id(1L)
                .judul("Log 1")
                .status(status)
                .build();
        Log log2 = new Log.Builder()
                .id(2L)
                .judul("Log 2")
                .status(status)
                .build();

        List<Log> expectedLogs = Arrays.asList(log1, log2);

        when(logRepository.findByStatus(status)).thenReturn(expectedLogs);

        // Act
        List<Log> actualLogs = logService.getLogsByStatus(status);

        // Assert
        assertEquals(2, actualLogs.size());
        assertEquals("Log 1", actualLogs.get(0).getJudul());
        assertEquals("Log 2", actualLogs.get(1).getJudul());
        verify(logRepository).findByStatus(status);
    }

    @Test
    void testGetLogsByMonth() {
        // Arrange
        int month = 5;
        int year = 2023;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        Log log1 = new Log.Builder()
                .id(1L)
                .judul("Monthly Log 1")
                .tanggalLog(LocalDate.of(year, month, 15))
                .build();
        Log log2 = new Log.Builder()
                .id(2L)
                .judul("Monthly Log 2")
                .tanggalLog(LocalDate.of(year, month, 20))
                .build();

        List<Log> expectedLogs = Arrays.asList(log1, log2);

        when(logRepository.findByTanggalLogBetween(startDate, endDate))
                .thenReturn(expectedLogs);

        // Act
        List<Log> actualLogs = logService.getLogsByMonth(month, year);

        // Assert
        assertEquals(2, actualLogs.size());
        assertEquals("Monthly Log 1", actualLogs.get(0).getJudul());
        assertEquals("Monthly Log 2", actualLogs.get(1).getJudul());
        verify(logRepository).findByTanggalLogBetween(startDate, endDate);
    }

    @Test
    void testUpdateLogNotFound() {
        // Arrange
        Long id = 999L;
        Log updatedLog = new Log.Builder()
                .judul("Updated Log")
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .build();

        when(logRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> logService.updateLog(id, updatedLog));

        assertEquals("Log tidak ditemukan", exception.getMessage());
        verify(logRepository).findById(id);
        verify(logRepository, never()).save(any());
    }

    @Test
    void testUpdateStatusNotFound() {
        // Arrange
        Long id = 999L;
        LogStatus newStatus = LogStatus.DITERIMA;

        when(logRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> logService.updateStatus(id, newStatus));

        assertEquals("Log tidak ditemukan", exception.getMessage());
        verify(logRepository).findById(id);
        verify(logRepository, never()).save(any());
    }

    @Test
    void testDeleteLogWithNonExistentId() {
        // Arrange
        Long id = 999L;
        doThrow(new RuntimeException("Log not found")).when(logRepository).deleteById(id);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> logService.deleteLog(id));
        verify(logRepository).deleteById(id);
    }
}