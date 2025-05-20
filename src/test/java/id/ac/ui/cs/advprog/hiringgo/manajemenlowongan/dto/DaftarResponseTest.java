package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DaftarResponseTest {

    @Test
    @DisplayName("Test DaftarResponse constructor with non-null Pendaftaran")
    void testConstructorWithNonNullPendaftaran() {
        UUID pendaftaranId = UUID.randomUUID();
        UUID lowonganId = UUID.randomUUID();
        UUID kandidatId = UUID.randomUUID();
        Mahasiswa kandidat = new Mahasiswa();
        kandidat.setId(kandidatId);
        BigDecimal ipk = new BigDecimal("3.75");
        int sks = 100;
        LocalDateTime waktuDaftar = LocalDateTime.now();

        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);

        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(pendaftaranId);
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setKandidat(kandidat);
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
        UUID newKandidatId = UUID.randomUUID();
        response.setKandidatId(newKandidatId);
        BigDecimal newIpk = new BigDecimal("3.50");
        response.setIpk(newIpk);
        response.setSks(120);
        LocalDateTime newTime = LocalDateTime.now();
        response.setWaktuDaftar(newTime);

        assertFalse(response.isSuccess());
        assertEquals("Updated message", response.getMessage());
        assertEquals(newPendaftaranId, response.getPendaftaranId());
        assertEquals(newLowonganId, response.getLowonganId());
        assertEquals(newKandidatId, response.getKandidatId());
        assertEquals(newIpk, response.getIpk());
        assertEquals(120, response.getSks());
        assertEquals(newTime, response.getWaktuDaftar());
    }

    @Test
    @DisplayName("Test equals() and hashCode()")
    void testEqualsAndHashCode() {
        UUID pId = UUID.randomUUID();
        UUID lId = UUID.randomUUID();
        UUID kId = UUID.randomUUID();
        Mahasiswa m = new Mahasiswa();
        m.setId(kId);

        Lowongan low = new Lowongan();
        low.setLowonganId(lId);

        Pendaftaran p = new Pendaftaran();
        p.setPendaftaranId(pId);
        p.setLowongan(low);
        p.setKandidat(m);
        p.setIpk(new BigDecimal("3.75"));
        p.setSks(20);
        LocalDateTime w = LocalDateTime.now().withNano(0);
        p.setWaktuDaftar(w);

        DaftarResponse a = new DaftarResponse(true, "OK", p);
        DaftarResponse b = new DaftarResponse(true, "OK", p);

        // reflexive
        assertEquals(a, a);
        // symmetric
        assertEquals(a, b);
        assertEquals(b, a);
        // transitive
        DaftarResponse c = new DaftarResponse(true, "OK", p);
        assertEquals(a, b);
        assertEquals(b, c);
        assertEquals(a, c);

        // consistent hashCode
        assertEquals(a.hashCode(), b.hashCode());

        // null and different type
        assertNotEquals(a, null);
        assertNotEquals(a, "some string");

        // change a field on b → they should no longer be equal
        b.setMessage("NOPE");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    // ────────────────────────────────────────────────────────────────────────────────
    // New tests to cover toString()
    // ────────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("toString should include all field names and their values")
    void testToStringContainsAllFields() {
        UUID pId = UUID.randomUUID();
        UUID lId = UUID.randomUUID();
        UUID kId = UUID.randomUUID();
        Mahasiswa m = new Mahasiswa();
        m.setId(kId);

        Lowongan low = new Lowongan();
        low.setLowonganId(lId);

        Pendaftaran p = new Pendaftaran();
        p.setPendaftaranId(pId);
        p.setLowongan(low);
        p.setKandidat(m);
        p.setIpk(new BigDecimal("3.10"));
        p.setSks(18);
        LocalDateTime w = LocalDateTime.now().withNano(0);
        p.setWaktuDaftar(w);

        DaftarResponse resp = new DaftarResponse(true, "MSG", p);

        String s = resp.toString();
        // Ensure key fields are present
        assertTrue(s.contains("success=true"),      "toString missing success");
        assertTrue(s.contains("message=MSG"),        "toString missing message");
        assertTrue(s.contains("pendaftaranId=" + pId),     "toString missing pendaftaranId");
        assertTrue(s.contains("lowonganId=" + lId),        "toString missing lowonganId");
        assertTrue(s.contains("kandidatId=" + kId),        "toString missing kandidatId");
        assertTrue(s.contains("ipk=3.10"),          "toString missing ipk");
        assertTrue(s.contains("sks=18"),            "toString missing sks");
        assertTrue(s.contains("waktuDaftar="),       "toString missing waktuDaftar");
    }

    @Test
    @DisplayName("Test equals() and hashCode() with all fields")
    void testEqualsAndHashCodeAllFields() {
        // Setup base objects
        UUID pId = UUID.randomUUID();
        UUID lId = UUID.randomUUID();
        UUID kId = UUID.randomUUID();
        Mahasiswa m = new Mahasiswa();
        m.setId(kId);

        Lowongan low = new Lowongan();
        low.setLowonganId(lId);

        Pendaftaran p = new Pendaftaran();
        p.setPendaftaranId(pId);
        p.setLowongan(low);
        p.setKandidat(m);
        p.setIpk(new BigDecimal("3.75"));
        p.setSks(20);
        LocalDateTime w = LocalDateTime.now().withNano(0);
        p.setWaktuDaftar(w);

        DaftarResponse original = new DaftarResponse(true, "OK", p);

        // Test each field one by one

        // Test success field
        DaftarResponse diffSuccess = new DaftarResponse(true, "OK", p);
        diffSuccess.setSuccess(false);
        assertNotEquals(original, diffSuccess);
        assertNotEquals(original.hashCode(), diffSuccess.hashCode());

        // Test message field
        DaftarResponse diffMessage = new DaftarResponse(true, "OK", p);
        diffMessage.setMessage("Different message");
        assertNotEquals(original, diffMessage);
        assertNotEquals(original.hashCode(), diffMessage.hashCode());

        // Test pendaftaranId field
        DaftarResponse diffPendaftaranId = new DaftarResponse(true, "OK", p);
        diffPendaftaranId.setPendaftaranId(UUID.randomUUID());
        assertNotEquals(original, diffPendaftaranId);
        assertNotEquals(original.hashCode(), diffPendaftaranId.hashCode());

        // Test lowonganId field
        DaftarResponse diffLowonganId = new DaftarResponse(true, "OK", p);
        diffLowonganId.setLowonganId(UUID.randomUUID());
        assertNotEquals(original, diffLowonganId);
        assertNotEquals(original.hashCode(), diffLowonganId.hashCode());

        // Test kandidatId field
        DaftarResponse diffKandidatId = new DaftarResponse(true, "OK", p);
        diffKandidatId.setKandidatId(UUID.randomUUID());
        assertNotEquals(original, diffKandidatId);
        assertNotEquals(original.hashCode(), diffKandidatId.hashCode());

        // Test ipk field
        DaftarResponse diffIpk = new DaftarResponse(true, "OK", p);
        diffIpk.setIpk(new BigDecimal("2.50"));
        assertNotEquals(original, diffIpk);
        assertNotEquals(original.hashCode(), diffIpk.hashCode());

        // Test sks field
        DaftarResponse diffSks = new DaftarResponse(true, "OK", p);
        diffSks.setSks(15);
        assertNotEquals(original, diffSks);
        assertNotEquals(original.hashCode(), diffSks.hashCode());

        // Test waktuDaftar field
        DaftarResponse diffWaktuDaftar = new DaftarResponse(true, "OK", p);
        diffWaktuDaftar.setWaktuDaftar(LocalDateTime.now().minusDays(1).withNano(0));
        assertNotEquals(original, diffWaktuDaftar);
        assertNotEquals(original.hashCode(), diffWaktuDaftar.hashCode());

        // Test with null fields
        DaftarResponse withNullMessage = new DaftarResponse(true, "OK", p);
        withNullMessage.setMessage(null);
        assertNotEquals(original, withNullMessage);

        DaftarResponse withNullIds = new DaftarResponse(true, "OK", p);
        withNullIds.setPendaftaranId(null);
        withNullIds.setLowonganId(null);
        withNullIds.setKandidatId(null);
        assertNotEquals(original, withNullIds);

        DaftarResponse withNullDateTime = new DaftarResponse(true, "OK", p);
        withNullDateTime.setWaktuDaftar(null);
        assertNotEquals(original, withNullDateTime);

        // Test with all fields modified
        DaftarResponse allDifferent = new DaftarResponse(false, "Different", null);
        allDifferent.setPendaftaranId(UUID.randomUUID());
        allDifferent.setLowonganId(UUID.randomUUID());
        allDifferent.setKandidatId(UUID.randomUUID());
        allDifferent.setIpk(new BigDecimal("1.0"));
        allDifferent.setSks(10);
        allDifferent.setWaktuDaftar(LocalDateTime.now().plusDays(1));
        assertNotEquals(original, allDifferent);
        assertNotEquals(original.hashCode(), allDifferent.hashCode());
    }
}
