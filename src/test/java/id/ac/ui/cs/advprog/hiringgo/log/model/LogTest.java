package id.ac.ui.cs.advprog.hiringgo.log.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LogTest {

    @Test
    public void testValidLogCreation() {
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
    public void testWaktuMulaiDanSelesaiHarusValid() {
        Log log = new Log.Builder()
                .waktuMulai(LocalTime.of(14, 0))
                .waktuSelesai(LocalTime.of(13, 0))
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu selesai harus setelah waktu mulai.", exception.getMessage());
    }

    @Test
    public void testWaktuTidakBolehKosong() {
        Log log = new Log.Builder()
                .waktuMulai(null)
                .waktuSelesai(null)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu mulai dan selesai harus diisi.", exception.getMessage());
    }

    @Test
    public void testDefaultStatusAdalahMenunggu() {
        Log log = new Log.Builder().build();

        assertEquals(LogStatus.MENUNGGU, log.getStatus());
    }

    @Test
    public void testWaktuMulaiNullSelesaiValid() {
        Log log = new Log.Builder()
                .waktuMulai(null)
                .waktuSelesai(LocalTime.of(12, 0))
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu mulai dan selesai harus diisi.", exception.getMessage());
    }

    @Test
    public void testWaktuMulaiValidSelesaiNull() {
        Log log = new Log.Builder()
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(null)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu mulai dan selesai harus diisi.", exception.getMessage());
    }

    @Test
    public void testCustomStatusSetting() {
        Log log = new Log.Builder()
                .status(LogStatus.DITERIMA)
                .build();

        assertEquals(LogStatus.DITERIMA, log.getStatus());
    }

    @Test
    public void testPesanUntukDosen() {
        String pesan = "Ini adalah pesan untuk dosen";
        Log log = new Log.Builder()
                .pesanUntukDosen(pesan)
                .build();

        assertEquals(pesan, log.getPesanUntukDosen());
    }
}