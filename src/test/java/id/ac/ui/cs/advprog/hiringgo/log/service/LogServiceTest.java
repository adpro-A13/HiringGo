package id.ac.ui.cs.advprog.hiringgo.log.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.log.dto.request.CreateLogRequest;
import id.ac.ui.cs.advprog.hiringgo.log.dto.response.LowonganWithPendaftaranDTO;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

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

class LogServiceTest {

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
                .id(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"))
                .status(LogStatus.MENUNGGU)
                .build();

        when(logRepository.findById(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"))).thenReturn(Optional.of(log));
        when(logRepository.save(any(Log.class))).thenReturn(log);

        // Act
        Log updated = logService.updateStatus(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"), LogStatus.DITERIMA);

        // Assert
        assertEquals(LogStatus.DITERIMA, updated.getStatus());
        verify(logRepository).save(log);
    }

    @Test
    void testGetLogById() {
        // Arrange
        Log log = new Log.Builder()
                .id(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"))
                .judul("Review")
                .build();

        when(logRepository.findById(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"))).thenReturn(Optional.of(log));

        // Act
        Optional<Log> found = logService.getLogById(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"));

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Review", found.get().getJudul());
    }

    @Test
    void testGetLogByIdNotFound() {
        // Arrange
        when(logRepository.findById(UUID.fromString("17fc3ab6-2a61-426a-b9a7-19fb09492104"))).thenReturn(Optional.empty());

        // Act
        Optional<Log> found = logService.getLogById(UUID.fromString("17fc3ab6-2a61-426a-b9a7-19fb09492104"));

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

        when(logRepository.findById(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"))).thenReturn(Optional.of(existing));
        when(logRepository.save(any(Log.class))).thenReturn(updated);

        // Act
        Log result = logService.updateLog(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"), updated);

        // Assert
        assertEquals("Sidang", result.getJudul());
        assertEquals(LocalTime.of(10, 0), result.getWaktuMulai());
    }

    @Test
    void testDeleteLog() {
        // Arrange
        doNothing().when(logRepository).deleteById(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"));

        // Act & Assert
        assertDoesNotThrow(() -> logService.deleteLog(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25")));
        verify(logRepository).deleteById(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"));
    }

    @Test
    void testGetLogsByStatus() {
        // Arrange
        LogStatus status = LogStatus.DITERIMA;
        Log log1 = new Log.Builder()
                .id(UUID.randomUUID())
                .judul("Log 1")
                .status(status)
                .build();
        Log log2 = new Log.Builder()
                .id(UUID.randomUUID())
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
                .id(UUID.randomUUID())
                .judul("Monthly Log 1")
                .tanggalLog(LocalDate.of(year, month, 15))
                .build();
        Log log2 = new Log.Builder()
                .id(UUID.randomUUID())
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
    void testGetLogsByDosenMataKuliah() {
        UUID dosenId = UUID.randomUUID();
        List<Log> mockLogs = List.of(new Log());

        when(logRepository.findLogsByDosenMataKuliah(dosenId)).thenReturn(mockLogs);

        List<Log> result = logService.getLogsByDosenMataKuliah(dosenId);

        assertEquals(1, result.size());
        verify(logRepository, times(1)).findLogsByDosenMataKuliah(dosenId);
    }

    @Test
    void testGetLogsByUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Log log1 = new Log.Builder()
                .id(UUID.randomUUID())
                .judul("User Log 1")
                .build();
        Log log2 = new Log.Builder()
                .id(UUID.randomUUID())
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
        UUID id = UUID.randomUUID();
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
        UUID id = UUID.randomUUID();
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
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException("Log not found")).when(logRepository).deleteById(id);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> logService.deleteLog(id));
        verify(logRepository).deleteById(id);
    }

    @Test
    void testGetLowonganYangDiterima() {
        UUID kandidatId = UUID.randomUUID();

        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(kandidatId);
        mahasiswa.setUsername("testMahasiswa");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mahasiswa, null, mahasiswa.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Pendaftaran diterima1 = mock(Pendaftaran.class);
        Pendaftaran diterima2 = mock(Pendaftaran.class);
        Pendaftaran ditolak = mock(Pendaftaran.class);

        Lowongan lowongan1 = mock(Lowongan.class);
        Lowongan lowongan2 = mock(Lowongan.class);

        when(diterima1.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        when(diterima2.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        when(ditolak.getStatus()).thenReturn(StatusPendaftaran.DITOLAK);

        when(diterima1.getLowongan()).thenReturn(lowongan1);
        when(diterima2.getLowongan()).thenReturn(lowongan2);
        when(ditolak.getLowongan()).thenReturn(mock(Lowongan.class)); // opsional

        List<Pendaftaran> semuaPendaftaran = List.of(diterima1, diterima2, ditolak);
        when(pendaftaranRepository.findByKandidatId(kandidatId)).thenReturn(semuaPendaftaran);

        List<LowonganWithPendaftaranDTO> hasil = logService.getLowonganYangDiterima();

        assertEquals(2, hasil.size());

        assertTrue(hasil.stream().anyMatch(dto -> dto.getLowongan().equals(lowongan1)));
        assertTrue(hasil.stream().anyMatch(dto -> dto.getLowongan().equals(lowongan2)));

        assertFalse(hasil.stream()
                .anyMatch(dto -> dto.getLowongan().equals(ditolak.getLowongan())));

        for (LowonganWithPendaftaranDTO dto : hasil) {
            for (Pendaftaran p : dto.getPendaftaranUser()) {
                assertEquals(StatusPendaftaran.DITERIMA, p.getStatus());
                assertEquals(dto.getLowongan(), p.getLowongan());
            }
        }

        verify(pendaftaranRepository).findByKandidatId(kandidatId);
    }

    // Test cases tambahan untuk mencapai 100% branch coverage LogService

    @Test
    void testCreateLogWithNullWaktuMulai() {
        // Test untuk validateLogTime dengan waktuMulai null
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
        request.setWaktuMulai(null); // Null waktu mulai
        request.setWaktuSelesai(LocalTime.of(10, 0));
        request.setPendaftaran(String.valueOf(pendaftaranId));
        request.setUser(userId);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> logService.createLog(request));
        assertEquals("Waktu mulai dan selesai tidak boleh kosong", exception.getMessage());
        verify(logRepository, never()).save(any());
    }

    @Test
    void testCreateLogWithNullWaktuSelesai() {
        // Test untuk validateLogTime dengan waktuSelesai null
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
        request.setWaktuSelesai(null); // Null waktu selesai
        request.setPendaftaran(String.valueOf(pendaftaranId));
        request.setUser(userId);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> logService.createLog(request));
        assertEquals("Waktu mulai dan selesai tidak boleh kosong", exception.getMessage());
        verify(logRepository, never()).save(any());
    }

    @Test
    void testCreateLogWithBothTimesNull() {
        // Test untuk validateLogTime dengan kedua waktu null
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
        request.setWaktuMulai(null); // Null waktu mulai
        request.setWaktuSelesai(null); // Null waktu selesai
        request.setPendaftaran(String.valueOf(pendaftaranId));
        request.setUser(userId);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> logService.createLog(request));
        assertEquals("Waktu mulai dan selesai tidak boleh kosong", exception.getMessage());
        verify(logRepository, never()).save(any());
    }

    @Test
    void testCreateLogWithEqualTimes() {
        // Test untuk waktu mulai sama dengan waktu selesai (edge case)
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
        request.setWaktuMulai(LocalTime.of(10, 0));
        request.setWaktuSelesai(LocalTime.of(10, 0)); // Waktu sama
        request.setPendaftaran(String.valueOf(pendaftaranId));
        request.setUser(userId);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> logService.createLog(request));
        assertEquals("Waktu mulai harus sebelum waktu selesai", exception.getMessage());
        verify(logRepository, never()).save(any());
    }

    @Test
    void testUpdateLogWithNullWaktuMulai() {
        // Test untuk updateLog dengan validasi waktu mulai null
        UUID logId = UUID.randomUUID();

        Log existingLog = new Log.Builder()
                .id(logId)
                .judul("Existing Log")
                .waktuMulai(LocalTime.of(8, 0))
                .waktuSelesai(LocalTime.of(9, 0))
                .build();

        Log updatedLog = new Log.Builder()
                .judul("Updated Log")
                .waktuMulai(null) // Null waktu mulai
                .waktuSelesai(LocalTime.of(11, 0))
                .build();

        when(logRepository.findById(logId)).thenReturn(Optional.of(existingLog));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> logService.updateLog(logId, updatedLog));
        assertEquals("Waktu mulai dan selesai tidak boleh kosong", exception.getMessage());
        verify(logRepository, never()).save(any());
    }

    @Test
    void testUpdateLogWithNullWaktuSelesai() {
        // Test untuk updateLog dengan validasi waktu selesai null
        UUID logId = UUID.randomUUID();

        Log existingLog = new Log.Builder()
                .id(logId)
                .judul("Existing Log")
                .waktuMulai(LocalTime.of(8, 0))
                .waktuSelesai(LocalTime.of(9, 0))
                .build();

        Log updatedLog = new Log.Builder()
                .judul("Updated Log")
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(null) // Null waktu selesai
                .build();

        when(logRepository.findById(logId)).thenReturn(Optional.of(existingLog));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> logService.updateLog(logId, updatedLog));
        assertEquals("Waktu mulai dan selesai tidak boleh kosong", exception.getMessage());
        verify(logRepository, never()).save(any());
    }

    @Test
    void testUpdateLogWithInvalidTime() {
        // Test untuk updateLog dengan waktu tidak valid
        UUID logId = UUID.randomUUID();

        Log existingLog = new Log.Builder()
                .id(logId)
                .judul("Existing Log")
                .waktuMulai(LocalTime.of(8, 0))
                .waktuSelesai(LocalTime.of(9, 0))
                .build();

        Log updatedLog = new Log.Builder()
                .judul("Updated Log")
                .waktuMulai(LocalTime.of(15, 0)) // Waktu mulai setelah waktu selesai
                .waktuSelesai(LocalTime.of(11, 0))
                .build();

        when(logRepository.findById(logId)).thenReturn(Optional.of(existingLog));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> logService.updateLog(logId, updatedLog));
        assertEquals("Waktu mulai harus sebelum waktu selesai", exception.getMessage());
        verify(logRepository, never()).save(any());
    }

    @Test
    void testGetLowonganYangDiterimaWithNonMahasiswaPrincipal() {
        // Test untuk case ketika principal bukan Mahasiswa
        User nonMahasiswaUser = mock(User.class); // User yang bukan Mahasiswa

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(nonMahasiswaUser, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> logService.getLowonganYangDiterima());
        assertEquals("User not authenticated or user data unavailable", exception.getMessage());
    }

    @Test
    void testGetLowonganYangDiterimaWithNullPrincipal() {
        // Test untuk case ketika principal null
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(null, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> logService.getLowonganYangDiterima());
        assertEquals("User not authenticated or user data unavailable", exception.getMessage());
    }

    @Test
    void testGetLowonganYangDiterimaWithEmptyPendaftaran() {
        // Test untuk case ketika tidak ada pendaftaran
        UUID kandidatId = UUID.randomUUID();

        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(kandidatId);
        mahasiswa.setUsername("testMahasiswa");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mahasiswa, null, mahasiswa.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(pendaftaranRepository.findByKandidatId(kandidatId)).thenReturn(List.of());

        // Act
        List<LowonganWithPendaftaranDTO> hasil = logService.getLowonganYangDiterima();

        // Assert
        assertTrue(hasil.isEmpty());
        verify(pendaftaranRepository).findByKandidatId(kandidatId);
    }

    @Test
    void testGetLowonganYangDiterimaWithOnlyDitolakPendaftaran() {
        // Test untuk case ketika semua pendaftaran ditolak
        UUID kandidatId = UUID.randomUUID();

        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(kandidatId);
        mahasiswa.setUsername("testMahasiswa");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mahasiswa, null, mahasiswa.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Pendaftaran ditolak1 = mock(Pendaftaran.class);
        Pendaftaran ditolak2 = mock(Pendaftaran.class);

        when(ditolak1.getStatus()).thenReturn(StatusPendaftaran.DITOLAK);
        when(ditolak2.getStatus()).thenReturn(StatusPendaftaran.DITOLAK);

        List<Pendaftaran> semuaPendaftaran = List.of(ditolak1, ditolak2);
        when(pendaftaranRepository.findByKandidatId(kandidatId)).thenReturn(semuaPendaftaran);

        // Act
        List<LowonganWithPendaftaranDTO> hasil = logService.getLowonganYangDiterima();

        // Assert
        assertTrue(hasil.isEmpty());
        verify(pendaftaranRepository).findByKandidatId(kandidatId);
    }

    @Test
    void testGetLowonganYangDiterimaWithMultiplePendaftaranSameLowongan() {
        // Test untuk case ketika ada multiple pendaftaran untuk lowongan yang sama
        UUID kandidatId = UUID.randomUUID();

        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(kandidatId);
        mahasiswa.setUsername("testMahasiswa");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mahasiswa, null, mahasiswa.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Lowongan sameLowongan = mock(Lowongan.class);

        Pendaftaran diterima1 = mock(Pendaftaran.class);
        Pendaftaran diterima2 = mock(Pendaftaran.class);

        when(diterima1.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        when(diterima2.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        when(diterima1.getLowongan()).thenReturn(sameLowongan);
        when(diterima2.getLowongan()).thenReturn(sameLowongan);

        List<Pendaftaran> semuaPendaftaran = List.of(diterima1, diterima2);
        when(pendaftaranRepository.findByKandidatId(kandidatId)).thenReturn(semuaPendaftaran);

        // Act
        List<LowonganWithPendaftaranDTO> hasil = logService.getLowonganYangDiterima();

        // Assert
        assertEquals(1, hasil.size()); // Hanya 1 lowongan
        assertEquals(2, hasil.get(0).getPendaftaranUser().size()); // Tapi ada 2 pendaftaran
        assertEquals(sameLowongan, hasil.get(0).getLowongan());

        verify(pendaftaranRepository).findByKandidatId(kandidatId);
    }
}
