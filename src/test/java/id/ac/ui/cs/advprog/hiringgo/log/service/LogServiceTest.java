package id.ac.ui.cs.advprog.hiringgo.log.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.log.dto.request.CreateLogRequest;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.repository.LogRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LogServiceTest {

    private LogRepository logRepository;
    private PendaftaranRepository pendaftaranRepository;
    private Lowongan lowongan;
    private UserRepository userRepository;
    private LogService logService;

    @BeforeEach
    void setUp() {
        logRepository = mock(LogRepository.class);
        pendaftaranRepository = mock(PendaftaranRepository.class);
        lowongan = mock(Lowongan.class);
        userRepository = mock(UserRepository.class);
        logService = new LogServiceImpl(logRepository, pendaftaranRepository, userRepository);
    }

    @Test
    void testCreateLogValid() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();
        BigDecimal ipk = new BigDecimal("3.14");

        User user = new Mahasiswa();
        user.setId(userId);

        Pendaftaran pendaftaran = new Pendaftaran(lowongan, (Mahasiswa) user, ipk, 3, LocalDateTime.now());
        pendaftaran.setPendaftaranId(pendaftaranId);

        CreateLogRequest request = new CreateLogRequest();
        request.setJudul("Asistensi");
        request.setKategori(LogKategori.ASISTENSI);
        request.setTanggalLog(LocalDate.now());
        request.setWaktuMulai(LocalTime.of(9, 0));
        request.setWaktuSelesai(LocalTime.of(10, 0));
        request.setPendaftaran(String.valueOf(pendaftaranId));
        request.setUser(userId);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(logRepository.save(any(Log.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Log result = logService.createLog(request);

        // Assert
        assertEquals(LogStatus.MENUNGGU, result.getStatus());
        assertEquals("Asistensi", result.getJudul());
        assertEquals(user, result.getUser());
        assertEquals(pendaftaran, result.getPendaftaran());
        verify(logRepository).save(any(Log.class));
    }

    @Test
    void testCreateLogPendaftaranNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();
        BigDecimal ipk = new BigDecimal("3.14");

        User user = new Mahasiswa();
        user.setId(userId);

        Pendaftaran pendaftaran = new Pendaftaran(lowongan, (Mahasiswa) user, ipk, 3, LocalDateTime.now());
        pendaftaran.setPendaftaranId(pendaftaranId);

        CreateLogRequest request = new CreateLogRequest();
        request.setPendaftaran(String.valueOf(pendaftaranId));
        request.setUser(userId);
        request.setWaktuMulai(LocalTime.of(9, 0));
        request.setWaktuSelesai(LocalTime.of(10, 0));

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> logService.createLog(request));
        assertEquals("Pendaftaran Not Found", exception.getMessage());
        verify(logRepository, never()).save(any());
    }

    @Test
    void testCreateLogUserNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();
        BigDecimal ipk = new BigDecimal("3.14");

        User user = new Mahasiswa();
        user.setId(userId);

        Pendaftaran pendaftaran = new Pendaftaran(lowongan, (Mahasiswa) user, ipk, 3, LocalDateTime.now());
        pendaftaran.setPendaftaranId(pendaftaranId);

        CreateLogRequest request = new CreateLogRequest();
        request.setPendaftaran(String.valueOf(pendaftaranId));
        request.setUser(userId);
        request.setWaktuMulai(LocalTime.of(9, 0));
        request.setWaktuSelesai(LocalTime.of(10, 0));

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> logService.createLog(request));
        assertEquals("User Not Found", exception.getMessage());
        verify(logRepository, never()).save(any());
    }

    @Test
    void testCreateLogInvalidTime() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();
        BigDecimal ipk = new BigDecimal("3.14");

        User user = new Mahasiswa();
        user.setId(userId);

        Pendaftaran pendaftaran = new Pendaftaran(lowongan, (Mahasiswa) user, ipk, 3, LocalDateTime.now());
        pendaftaran.setPendaftaranId(pendaftaranId);

        CreateLogRequest request = new CreateLogRequest();
        request.setJudul("Asistensi");
        request.setKategori(LogKategori.ASISTENSI);
        request.setTanggalLog(LocalDate.now());
        request.setWaktuMulai(LocalTime.of(14, 0));
        request.setWaktuSelesai(LocalTime.of(12, 0));
        request.setPendaftaran(String.valueOf(pendaftaranId));
        request.setUser(userId);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> logService.createLog(request));
        assertTrue(exception.getMessage().contains("Waktu mulai harus sebelum waktu selesai"));
        verify(logRepository, never()).save(any());
    }

    @Test
    void testUpdateStatus() {
        // Arrange
        Log log = new Log.Builder()
                .status(LogStatus.MENUNGGU)
                .build();

        when(logRepository.findById(1L)).thenReturn(Optional.of(log));
        when(logRepository.save(any(Log.class))).thenReturn(log);

        // Act
        Log updated = logService.updateStatus(1L, LogStatus.DITERIMA);

        // Assert
        assertEquals(LogStatus.DITERIMA, updated.getStatus());
        verify(logRepository).save(log);
    }

    @Test
    void testGetLogById() {
        // Arrange
        Log log = new Log.Builder()
                .judul("Review")
                .build();

        when(logRepository.findById(1L)).thenReturn(Optional.of(log));

        // Act
        Optional<Log> found = logService.getLogById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Review", found.get().getJudul());
    }

    @Test
    void testGetLogByIdNotFound() {
        // Arrange
        when(logRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Log> found = logService.getLogById(99L);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testGetAllLogs() {
        // Arrange
        Log log1 = new Log.Builder().judul("Asistensi").build();
        Log log2 = new Log.Builder().judul("Sidang").build();

        when(logRepository.findAll()).thenReturn(List.of(log1, log2));

        // Act
        List<Log> logs = logService.getAllLogs();

        // Assert
        assertEquals(2, logs.size());
        verify(logRepository).findAll();
    }

    @Test
    void testUpdateLog() {
        // Arrange
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
        when(logRepository.save(any(Log.class))).thenReturn(updated);

        // Act
        Log result = logService.updateLog(1L, updated);

        // Assert
        assertEquals("Sidang", result.getJudul());
        assertEquals(LocalTime.of(10, 0), result.getWaktuMulai());
    }

    @Test
    void testDeleteLog() {
        // Arrange
        doNothing().when(logRepository).deleteById(1L);

        // Act & Assert
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
        UUID userId = UUID.randomUUID();
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

        when(logRepository.findByTanggalLogBetweenAndUser_Id(startDate, endDate, userId))
                .thenReturn(expectedLogs);

        // Act
        CompletableFuture<List<Log>> future = logService.getLogsByMonth(month, year, userId);
        List<Log> actualLogs = future.join();

        // Assert
        assertEquals(2, actualLogs.size());
        assertEquals("Monthly Log 1", actualLogs.get(0).getJudul());
        assertEquals("Monthly Log 2", actualLogs.get(1).getJudul());
        verify(logRepository).findByTanggalLogBetweenAndUser_Id(startDate, endDate, userId);
    }

    @Test
    void testGetLogsByPendaftaran() {
        // Arrange
        UUID kode = UUID.randomUUID();
        Log log1 = new Log.Builder()
                .id(1L)
                .judul("MK Log 1")
                .build();
        Log log2 = new Log.Builder()
                .id(2L)
                .judul("MK Log 2")
                .build();

        List<Log> expectedLogs = Arrays.asList(log1, log2);

        when(logRepository.findByPendaftaran_pendaftaranId(kode)).thenReturn(expectedLogs);

        // Act
        List<Log> actualLogs = logService.getLogsByPendaftaran(String.valueOf(kode));

        // Assert
        assertEquals(2, actualLogs.size());
        assertEquals("MK Log 1", actualLogs.get(0).getJudul());
        assertEquals("MK Log 2", actualLogs.get(1).getJudul());
        verify(logRepository).findByPendaftaran_pendaftaranId(UUID.fromString(String.valueOf(kode)));
    }

    @Test
    void testGetLogsByUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Log log1 = new Log.Builder()
                .id(1L)
                .judul("User Log 1")
                .build();
        Log log2 = new Log.Builder()
                .id(2L)
                .judul("User Log 2")
                .build();

        List<Log> expectedLogs = Arrays.asList(log1, log2);

        when(logRepository.findByUserId(userId)).thenReturn(expectedLogs);

        // Act
        List<Log> actualLogs = logService.getLogsByUser(userId);

        // Assert
        assertEquals(2, actualLogs.size());
        assertEquals("User Log 1", actualLogs.get(0).getJudul());
        assertEquals("User Log 2", actualLogs.get(1).getJudul());
        verify(logRepository).findByUserId(userId);
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

    @Test
    void testGetLowonganYangDiterima() {
        UUID kandidatId = UUID.randomUUID();

        Pendaftaran diterima1 = mock(Pendaftaran.class);
        Pendaftaran diterima2 = mock(Pendaftaran.class);
        Pendaftaran ditolak = mock(Pendaftaran.class);

        when(diterima1.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        when(diterima2.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        when(ditolak.getStatus()).thenReturn(StatusPendaftaran.DITOLAK);

        List<Pendaftaran> semuaPendaftaran = List.of(diterima1, diterima2, ditolak);
        when(pendaftaranRepository.findByKandidatId(kandidatId)).thenReturn(semuaPendaftaran);

        List<Pendaftaran> hasil = logService.getLowonganYangDiterima(kandidatId);

        assertEquals(2, hasil.size());
        assertTrue(hasil.contains(diterima1));
        assertTrue(hasil.contains(diterima2));
        assertFalse(hasil.contains(ditolak));

        verify(pendaftaranRepository).findByKandidatId(kandidatId);
    }
}
