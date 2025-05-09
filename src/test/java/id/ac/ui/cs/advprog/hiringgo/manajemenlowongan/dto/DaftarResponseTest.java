package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DaftarResponseTest {

    @Test
    @DisplayName("Test DaftarResponse constructor with non-null Pendaftaran")
    void testConstructorWithNonNullPendaftaran() {

        UUID pendaftaranId = UUID.randomUUID();
        UUID lowonganId = UUID.randomUUID();
        String kandidatId = "test123";
        BigDecimal ipk = new BigDecimal("3.75");
        int sks = 100;
        LocalDateTime waktuDaftar = LocalDateTime.now();

        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);

        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(pendaftaranId);
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setKandidatId(kandidatId);
        pendaftaran.setIpk(ipk);
        pendaftaran.setSks(sks);
        pendaftaran.setWaktuDaftar(waktuDaftar);

        DaftarResponse response = new DaftarResponse(true, "Success", pendaftaran);

        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(pendaftaranId, response.getPendaftaranId());
        assertEquals(lowonganId, response.getLowonganId());
        assertEquals(kandidatId, response.getKandidatId());
        assertEquals(ipk, response.getIpk());
        assertEquals(sks, response.getSks());
        assertEquals(waktuDaftar, response.getWaktuDaftar());
    }

    @Test
    @DisplayName("Test DaftarResponse constructor with null Pendaftaran")
    void testConstructorWithNullPendaftaran() {
        DaftarResponse response = new DaftarResponse(false, "Failed", null);

        assertFalse(response.isSuccess());
        assertEquals("Failed", response.getMessage());
        assertNull(response.getPendaftaranId());
        assertNull(response.getLowonganId());
        assertNull(response.getKandidatId());
        assertNull(response.getIpk());
        assertEquals(0, response.getSks());
        assertNull(response.getWaktuDaftar());
    }

    @Test
    @DisplayName("Test DaftarResponse setters")
    void testDaftarResponseSetters() {
        DaftarResponse response = new DaftarResponse(true, "Initial message", null);

        response.setSuccess(false);
        response.setMessage("Updated message");
        UUID newPendaftaranId = UUID.randomUUID();
        response.setPendaftaranId(newPendaftaranId);
        UUID newLowonganId = UUID.randomUUID();
        response.setLowonganId(newLowonganId);
        response.setKandidatId("newUser");
        BigDecimal newIpk = new BigDecimal("3.50");
        response.setIpk(newIpk);
        response.setSks(120);
        LocalDateTime newTime = LocalDateTime.now();
        response.setWaktuDaftar(newTime);

        assertFalse(response.isSuccess());
        assertEquals("Updated message", response.getMessage());
        assertEquals(newPendaftaranId, response.getPendaftaranId());
        assertEquals(newLowonganId, response.getLowonganId());
        assertEquals("newUser", response.getKandidatId());
        assertEquals(newIpk, response.getIpk());
        assertEquals(120, response.getSks());
        assertEquals(newTime, response.getWaktuDaftar());
    }
}