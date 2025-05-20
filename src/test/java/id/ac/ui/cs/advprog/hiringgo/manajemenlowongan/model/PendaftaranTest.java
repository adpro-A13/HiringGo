package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PendaftaranTest {

    private Pendaftaran pendaftaran;
    private Lowongan lowongan;
    private Mahasiswa kandidat;
    private UUID pendaftaranId;
    private BigDecimal ipk;
    private int sks;
    private LocalDateTime waktuDaftar;

    @BeforeEach
    void setUp() {
        pendaftaranId = UUID.randomUUID();
        lowongan = new Lowongan(); // Assuming Lowongan has a default constructor or use a mock
        kandidat = new Mahasiswa(); // Assuming Mahasiswa has a default constructor or use a mock
        ipk = new BigDecimal("3.75");
        sks = 120;
        waktuDaftar = LocalDateTime.now();

        pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(pendaftaranId);
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setKandidat(kandidat);
        pendaftaran.setIpk(ipk);
        pendaftaran.setSks(sks);
        pendaftaran.setWaktuDaftar(waktuDaftar);
    }

    @Test
    void testDefaultConstructor() {
        Pendaftaran newPendaftaran = new Pendaftaran();
        assertNotNull(newPendaftaran);
        assertEquals(StatusPendaftaran.BELUM_DIPROSES, newPendaftaran.getStatus());
        assertNull(newPendaftaran.getPendaftaranId());
        assertNull(newPendaftaran.getLowongan());
        assertNull(newPendaftaran.getKandidat());
        assertNull(newPendaftaran.getIpk());
        assertEquals(0, newPendaftaran.getSks());
        assertNull(newPendaftaran.getWaktuDaftar());
    }

    @Test
    void testParameterizedConstructor() {
        LocalDateTime specificTime = LocalDateTime.of(2024, 5, 20, 10, 0);
        Pendaftaran newPendaftaran = new Pendaftaran(lowongan, kandidat, ipk, sks, specificTime);

        assertNotNull(newPendaftaran);
        assertEquals(lowongan, newPendaftaran.getLowongan());
        assertEquals(kandidat, newPendaftaran.getKandidat());
        assertEquals(ipk, newPendaftaran.getIpk());
        assertEquals(sks, newPendaftaran.getSks());
        assertEquals(specificTime, newPendaftaran.getWaktuDaftar());
        assertEquals(StatusPendaftaran.BELUM_DIPROSES, newPendaftaran.getStatus()); // Default status
    }

    @Test
    void testGetPendaftaranId() {
        assertEquals(pendaftaranId, pendaftaran.getPendaftaranId());
    }

    @Test
    void testSetPendaftaranId() {
        UUID newId = UUID.randomUUID();
        pendaftaran.setPendaftaranId(newId);
        assertEquals(newId, pendaftaran.getPendaftaranId());
    }

    @Test
    void testGetLowongan() {
        assertEquals(lowongan, pendaftaran.getLowongan());
    }

    @Test
    void testSetLowongan() {
        Lowongan newLowongan = new Lowongan();
        pendaftaran.setLowongan(newLowongan);
        assertEquals(newLowongan, pendaftaran.getLowongan());
    }

    @Test
    void testGetKandidat() {
        assertEquals(kandidat, pendaftaran.getKandidat());
    }

    @Test
    void testSetKandidat() {
        Mahasiswa newKandidat = new Mahasiswa();
        pendaftaran.setKandidat(newKandidat);
        assertEquals(newKandidat, pendaftaran.getKandidat());
    }

    @Test
    void testGetIpk() {
        assertEquals(ipk, pendaftaran.getIpk());
    }

    @Test
    void testSetIpk() {
        BigDecimal newIpk = new BigDecimal("3.80");
        pendaftaran.setIpk(newIpk);
        assertEquals(newIpk, pendaftaran.getIpk());
    }

    @Test
    void testGetSks() {
        assertEquals(sks, pendaftaran.getSks());
    }

    @Test
    void testSetSks() {
        int newSks = 130;
        pendaftaran.setSks(newSks);
        assertEquals(newSks, pendaftaran.getSks());
    }

    @Test
    void testGetWaktuDaftar() {
        assertEquals(waktuDaftar, pendaftaran.getWaktuDaftar());
    }

    @Test
    void testSetWaktuDaftar() {
        LocalDateTime newWaktuDaftar = LocalDateTime.now().plusDays(1);
        pendaftaran.setWaktuDaftar(newWaktuDaftar);
        assertEquals(newWaktuDaftar, pendaftaran.getWaktuDaftar());
    }

    @Test
    void testGetStatus() {
        assertEquals(StatusPendaftaran.BELUM_DIPROSES, pendaftaran.getStatus());
    }

    @Test
    void testSetStatus() {
        pendaftaran.setStatus(StatusPendaftaran.DITERIMA);
        assertEquals(StatusPendaftaran.DITERIMA, pendaftaran.getStatus());
    }

    @Test
    void testSetStatusToNull() {
        // Depending on requirements, status might not be allowed to be null.
        // If @NotNull annotation is present or business logic dictates,
        // this test might expect an exception.
        // For now, assuming it's allowed by setter.
        pendaftaran.setStatus(null);
        assertNull(pendaftaran.getStatus());
    }
}