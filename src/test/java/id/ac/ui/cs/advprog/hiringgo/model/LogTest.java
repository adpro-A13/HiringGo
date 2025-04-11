package id.ac.ui.cs.advprog.hiringgo.model;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class LogTest {

    @Test
    public void testValidLogCreation() {
        Log log = new Log();
        log.setJudul("Mengoreksi Tugas");
        log.setKeterangan("Mengoreksi tugas mahasiswa");
        log.setKategori(LogKategori.MENGOREKSI);
        log.setWaktuMulai(LocalTime.of(10, 0));
        log.setWaktuSelesai(LocalTime.of(12, 0));
        log.setTanggalLog(LocalDate.now());

        assertDoesNotThrow(log::validate);
        assertEquals(LogStatus.MENUNGGU, log.getStatus());
    }

    @Test
    public void testWaktuMulaiDanSelesaiHarusValid() {
        Log log = new Log();
        log.setWaktuMulai(LocalTime.of(14, 0));
        log.setWaktuSelesai(LocalTime.of(13, 0));

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu selesai harus setelah waktu mulai.", exception.getMessage());
    }

    @Test
    public void testWaktuTidakBolehKosong() {
        Log log = new Log();
        log.setWaktuMulai(null);
        log.setWaktuSelesai(null);

        Exception exception = assertThrows(IllegalArgumentException.class, log::validate);
        assertEquals("Waktu mulai dan selesai harus diisi.", exception.getMessage());
    }

    @Test
    public void testDefaultStatusAdalahMenunggu() {
        Log log = new Log();
        assertEquals(LogStatus.MENUNGGU, log.getStatus());
    }

}
