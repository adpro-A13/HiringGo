package id.ac.ui.cs.advprog.hiringgo.log.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LogTest {

    @Test
    void testValidLogCreation() {
        // Create mock objects for dependencies
        Pendaftaran mockPendaftaran = mock(Pendaftaran.class);
        when(mockPendaftaran.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        User mockUser = mock(User.class);

        Log log = new Log.Builder()
                .pendaftaran(mockPendaftaran)
                .user(mockUser)
                .judul("Mengoreksi Tugas")
                .keterangan("Mengoreksi tugas mahasiswa")
                .kategori(LogKategori.MENGOREKSI)
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .tanggalLog(LocalDate.now())
                .build();

        assertDoesNotThrow(log::validate);
        assertEquals(LogStatus.MENUNGGU, log.getStatus());
        assertEquals("Mengoreksi Tugas", log.getJudul());
        assertEquals("Mengoreksi tugas mahasiswa", log.getKeterangan());
        assertEquals(LogKategori.MENGOREKSI, log.getKategori());
    }

    @Test
    void testWaktuMulaiDanSelesaiHarusValid() {
        Log log = new Log.Builder()
                .waktuMulai(LocalTime.of(14, 0))
                .waktuSelesai(LocalTime.of(13, 0))
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu selesai harus setelah waktu mulai.", exception.getMessage());
    }

    @Test
    void testWaktuTidakBolehKosong() {
        Log log = new Log.Builder()
                .waktuMulai(null)
                .waktuSelesai(null)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu mulai dan selesai harus diisi.", exception.getMessage());
    }

    @Test
    void testDefaultStatusAdalahMenunggu() {
        Log log = new Log.Builder().build();

        assertEquals(LogStatus.MENUNGGU, log.getStatus());
    }

    @Test
    void testWaktuMulaiNullSelesaiValid() {
        Log log = new Log.Builder()
                .waktuMulai(null)
                .waktuSelesai(LocalTime.of(12, 0))
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu mulai dan selesai harus diisi.", exception.getMessage());
    }

    @Test
    void testWaktuMulaiValidSelesaiNull() {
        Log log = new Log.Builder()
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(null)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu mulai dan selesai harus diisi.", exception.getMessage());
    }

    @Test
    void testCustomStatusSetting() {
        Log log = new Log.Builder()
                .status(LogStatus.DITERIMA)
                .build();

        assertEquals(LogStatus.DITERIMA, log.getStatus());
    }

    @Test
    void testPesanUntukDosen() {
        String pesan = "Ini adalah pesan untuk dosen";
        Log log = new Log.Builder()
                .pesanUntukDosen(pesan)
                .build();

        assertEquals(pesan, log.getPesanUntukDosen());
    }

    @Test
    void testPendaftaranNullValidation() {
        // Test untuk validasi pendaftaran null
        Log log = new Log.Builder()
                .pendaftaran(null) // Pendaftaran null
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Pendaftaran harus memiliki status DITERIMA.", exception.getMessage());
    }

    @Test
    void testPendaftaranStatusBukanDiterima() {
        // Test untuk pendaftaran dengan status selain DITERIMA
        Pendaftaran mockPendaftaran = mock(Pendaftaran.class);
        when(mockPendaftaran.getStatus()).thenReturn(StatusPendaftaran.DITOLAK);

        Log log = new Log.Builder()
                .pendaftaran(mockPendaftaran)
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Pendaftaran harus memiliki status DITERIMA.", exception.getMessage());
    }

    @Test
    void testPendaftaranStatusMenunggu() {
        // Test untuk pendaftaran dengan status MENUNGGU
        Pendaftaran mockPendaftaran = mock(Pendaftaran.class);
        when(mockPendaftaran.getStatus()).thenReturn(StatusPendaftaran.BELUM_DIPROSES);

        Log log = new Log.Builder()
                .pendaftaran(mockPendaftaran)
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Pendaftaran harus memiliki status DITERIMA.", exception.getMessage());
    }

    @Test
    void testWaktuSamaDenganWaktuSelesai() {
        // Test untuk waktu mulai sama dengan waktu selesai (edge case)
        Pendaftaran mockPendaftaran = mock(Pendaftaran.class);
        when(mockPendaftaran.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);

        Log log = new Log.Builder()
                .pendaftaran(mockPendaftaran)
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(10, 0)) // Waktu sama
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu selesai harus setelah waktu mulai.", exception.getMessage());
    }

    @Test
    void testCompleteLogBuilderWithAllFields() {
        // Test untuk builder dengan semua field
        UUID testId = UUID.randomUUID();
        Pendaftaran mockPendaftaran = mock(Pendaftaran.class);
        when(mockPendaftaran.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        User mockUser = mock(User.class);
        LocalDate testDate = LocalDate.of(2023, 12, 25);
        LocalTime startTime = LocalTime.of(9, 30);
        LocalTime endTime = LocalTime.of(11, 45);

        Log log = new Log.Builder()
                .id(testId)
                .pendaftaran(mockPendaftaran)
                .user(mockUser)
                .judul("Test Judul")
                .keterangan("Test Keterangan")
                .kategori(LogKategori.ASISTENSI)
                .waktuMulai(startTime)
                .waktuSelesai(endTime)
                .tanggalLog(testDate)
                .pesanUntukDosen("Test Pesan untuk Dosen")
                .status(LogStatus.DITERIMA)
                .build();

        // Verify all fields are set correctly
        assertEquals(testId, log.getId());
        assertEquals(mockPendaftaran, log.getPendaftaran());
        assertEquals(mockUser, log.getUser());
        assertEquals("Test Judul", log.getJudul());
        assertEquals("Test Keterangan", log.getKeterangan());
        assertEquals(LogKategori.ASISTENSI, log.getKategori());
        assertEquals(startTime, log.getWaktuMulai());
        assertEquals(endTime, log.getWaktuSelesai());
        assertEquals(testDate, log.getTanggalLog());
        assertEquals("Test Pesan untuk Dosen", log.getPesanUntukDosen());
        assertEquals(LogStatus.DITERIMA, log.getStatus());

        // Should not throw exception
        assertDoesNotThrow(log::validate);
    }

    @Test
    void testBuilderChaining() {
        // Test untuk memastikan builder chaining berfungsi dengan baik
        Pendaftaran mockPendaftaran = mock(Pendaftaran.class);
        when(mockPendaftaran.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        User mockUser = mock(User.class);

        Log.Builder builder = new Log.Builder();

        // Test that each method returns the builder instance
        assertSame(builder, builder.id(UUID.randomUUID()));
        assertSame(builder, builder.pendaftaran(mockPendaftaran));
        assertSame(builder, builder.user(mockUser));
        assertSame(builder, builder.judul("Test"));
        assertSame(builder, builder.keterangan("Test"));
        assertSame(builder, builder.kategori(LogKategori.ASISTENSI));
        assertSame(builder, builder.waktuMulai(LocalTime.of(10, 0)));
        assertSame(builder, builder.waktuSelesai(LocalTime.of(12, 0)));
        assertSame(builder, builder.tanggalLog(LocalDate.now()));
        assertSame(builder, builder.pesanUntukDosen("Test"));
        assertSame(builder, builder.status(LogStatus.DITERIMA));
    }

    @Test
    void testLogSettersAndGetters() {
        // Test untuk setter dan getter methods
        Log log = new Log();
        UUID testId = UUID.randomUUID();
        Pendaftaran mockPendaftaran = mock(Pendaftaran.class);
        User mockUser = mock(User.class);
        LocalDate testDate = LocalDate.now();
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(10, 0);

        // Test setters
        log.setId(testId);
        log.setPendaftaran(mockPendaftaran);
        log.setUser(mockUser);
        log.setJudul("Setter Test");
        log.setKeterangan("Keterangan Test");
        log.setKategori(LogKategori.MENGOREKSI);
        log.setWaktuMulai(startTime);
        log.setWaktuSelesai(endTime);
        log.setTanggalLog(testDate);
        log.setPesanUntukDosen("Pesan Test");
        log.setStatus(LogStatus.DITOLAK);

        // Test getters
        assertEquals(testId, log.getId());
        assertEquals(mockPendaftaran, log.getPendaftaran());
        assertEquals(mockUser, log.getUser());
        assertEquals("Setter Test", log.getJudul());
        assertEquals("Keterangan Test", log.getKeterangan());
        assertEquals(LogKategori.MENGOREKSI, log.getKategori());
        assertEquals(startTime, log.getWaktuMulai());
        assertEquals(endTime, log.getWaktuSelesai());
        assertEquals(testDate, log.getTanggalLog());
        assertEquals("Pesan Test", log.getPesanUntukDosen());
        assertEquals(LogStatus.DITOLAK, log.getStatus());
    }

    @Test
    void testNoArgsConstructor() {
        // Test untuk no-args constructor
        Log log = new Log();

        // All fields should be null/default except status (which has default in builder)
        assertNull(log.getId());
        assertNull(log.getPendaftaran());
        assertNull(log.getUser());
        assertNull(log.getJudul());
        assertNull(log.getKeterangan());
        assertNull(log.getKategori());
        assertNull(log.getWaktuMulai());
        assertNull(log.getWaktuSelesai());
        assertNull(log.getTanggalLog());
        assertNull(log.getPesanUntukDosen());
        assertNull(log.getStatus()); // No default status in entity, only in builder
    }

    @Test
    void testValidateWithValidPendaftaranButNullTimes() {
        // Test kombinasi validasi: pendaftaran valid tapi waktu null
        Pendaftaran mockPendaftaran = mock(Pendaftaran.class);
        when(mockPendaftaran.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);

        Log log = new Log.Builder()
                .pendaftaran(mockPendaftaran)
                .waktuMulai(null)
                .waktuSelesai(null)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu mulai dan selesai harus diisi.", exception.getMessage());
    }

    @Test
    void testValidateWithValidTimesButInvalidPendaftaran() {
        // Test kombinasi validasi: waktu valid tapi pendaftaran invalid
        Pendaftaran mockPendaftaran = mock(Pendaftaran.class);
        when(mockPendaftaran.getStatus()).thenReturn(StatusPendaftaran.BELUM_DIPROSES);

        Log log = new Log.Builder()
                .pendaftaran(mockPendaftaran)
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Pendaftaran harus memiliki status DITERIMA.", exception.getMessage());
    }

    @Test
    void testLogKategoriEnumValues() {
        // Test untuk memastikan semua nilai LogKategori dapat digunakan
        Log.Builder builder = new Log.Builder();

        // Test each LogKategori enum value
        for (LogKategori kategori : LogKategori.values()) {
            Log log = builder.kategori(kategori).build();
            assertEquals(kategori, log.getKategori());
        }
    }

    @Test
    void testLogStatusEnumValues() {
        // Test untuk memastikan semua nilai LogStatus dapat digunakan
        Log.Builder builder = new Log.Builder();

        // Test each LogStatus enum value
        for (LogStatus status : LogStatus.values()) {
            Log log = builder.status(status).build();
            assertEquals(status, log.getStatus());
        }
    }

    @Test
    void testBuilderDefaultStatusOverride() {
        // Test untuk memastikan default status dapat di-override
        Log logDefault = new Log.Builder().build();
        assertEquals(LogStatus.MENUNGGU, logDefault.getStatus());

        Log logCustom = new Log.Builder()
                .status(LogStatus.DITERIMA)
                .build();
        assertEquals(LogStatus.DITERIMA, logCustom.getStatus());
    }

    @Test
    void testMultipleBuildsFromSameBuilder() {
        // Test untuk memastikan builder dapat digunakan multiple kali
        Log.Builder builder = new Log.Builder()
                .judul("Test Title")
                .keterangan("Test Description");

        Log log1 = builder.status(LogStatus.MENUNGGU).build();
        Log log2 = builder.status(LogStatus.DITERIMA).build();

        assertEquals("Test Title", log1.getJudul());
        assertEquals("Test Title", log2.getJudul());
        assertEquals(LogStatus.MENUNGGU, log1.getStatus());
        assertEquals(LogStatus.DITERIMA, log2.getStatus());
    }
}