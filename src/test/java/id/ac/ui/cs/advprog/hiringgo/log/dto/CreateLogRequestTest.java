package id.ac.ui.cs.advprog.hiringgo.log.dto;

import id.ac.ui.cs.advprog.hiringgo.log.dto.request.CreateLogRequest;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateLogRequestTest {

    private CreateLogRequest createLogRequest;
    private UUID testUserId;
    private LocalTime testStartTime;
    private LocalTime testEndTime;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        createLogRequest = new CreateLogRequest();
        testUserId = UUID.randomUUID();
        testStartTime = LocalTime.of(9, 0);
        testEndTime = LocalTime.of(11, 0);
        testDate = LocalDate.now();
    }

    @Test
    void testSetAndGetJudul() {
        String expectedJudul = "Test Judul";
        createLogRequest.setJudul(expectedJudul);
        assertEquals(expectedJudul, createLogRequest.getJudul());
    }

    @Test
    void testSetAndGetMataKuliah() {
        String expectedMataKuliah = "Pemrograman Lanjut";
        createLogRequest.setMataKuliah(expectedMataKuliah);
        assertEquals(expectedMataKuliah, createLogRequest.getMataKuliah());
    }

    @Test
    void testSetAndGetUser() {
        createLogRequest.setUser(testUserId);
        assertEquals(testUserId, createLogRequest.getUser());
    }

    @Test
    void testSetAndGetKategori() {
        LogKategori expectedKategori = LogKategori.ASISTENSI; // Assuming this enum value exists
        createLogRequest.setKategori(expectedKategori);
        assertEquals(expectedKategori, createLogRequest.getKategori());
    }

    @Test
    void testSetAndGetWaktuMulai() {
        createLogRequest.setWaktuMulai(testStartTime);
        assertEquals(testStartTime, createLogRequest.getWaktuMulai());
    }

    @Test
    void testSetAndGetWaktuSelesai() {
        createLogRequest.setWaktuSelesai(testEndTime);
        assertEquals(testEndTime, createLogRequest.getWaktuSelesai());
    }

    @Test
    void testSetAndGetTanggalLog() {
        createLogRequest.setTanggalLog(testDate);
        assertEquals(testDate, createLogRequest.getTanggalLog());
    }

    @Test
    void testSetAndGetKeterangan() {
        String expectedKeterangan = "Test keterangan";
        createLogRequest.setKeterangan(expectedKeterangan);
        assertEquals(expectedKeterangan, createLogRequest.getKeterangan());
    }

    @Test
    void testSetAndGetPesanUntukDosen() {
        String expectedPesan = "Test pesan untuk dosen";
        createLogRequest.setPesanUntukDosen(expectedPesan);
        assertEquals(expectedPesan, createLogRequest.getPesanUntukDosen());
    }

    @Test
    void testAllFieldsSetCorrectly() {
        String judul = "Test Log";
        String mataKuliah = "Advanced Programming";
        LogKategori kategori = LogKategori.ASISTENSI; // Assuming this enum value exists
        String keterangan = "Test keterangan lengkap";
        String pesanUntukDosen = "Mohon review";

        createLogRequest.setJudul(judul);
        createLogRequest.setMataKuliah(mataKuliah);
        createLogRequest.setUser(testUserId);
        createLogRequest.setKategori(kategori);
        createLogRequest.setWaktuMulai(testStartTime);
        createLogRequest.setWaktuSelesai(testEndTime);
        createLogRequest.setTanggalLog(testDate);
        createLogRequest.setKeterangan(keterangan);
        createLogRequest.setPesanUntukDosen(pesanUntukDosen);

        assertAll("All fields should be set correctly",
                () -> assertEquals(judul, createLogRequest.getJudul()),
                () -> assertEquals(mataKuliah, createLogRequest.getMataKuliah()),
                () -> assertEquals(testUserId, createLogRequest.getUser()),
                () -> assertEquals(kategori, createLogRequest.getKategori()),
                () -> assertEquals(testStartTime, createLogRequest.getWaktuMulai()),
                () -> assertEquals(testEndTime, createLogRequest.getWaktuSelesai()),
                () -> assertEquals(testDate, createLogRequest.getTanggalLog()),
                () -> assertEquals(keterangan, createLogRequest.getKeterangan()),
                () -> assertEquals(pesanUntukDosen, createLogRequest.getPesanUntukDosen())
        );
    }

    @Test
    void testEqualsAndHashCode() {
        CreateLogRequest request1 = new CreateLogRequest();
        CreateLogRequest request2 = new CreateLogRequest();

        // Test equals for empty objects
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());

        // Set same values
        String judul = "Test";
        request1.setJudul(judul);
        request2.setJudul(judul);
        request1.setUser(testUserId);
        request2.setUser(testUserId);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());

        // Change one value
        request2.setJudul("Different");
        assertNotEquals(request1, request2);
    }

    @Test
    void testToString() {
        createLogRequest.setJudul("Test Log");
        createLogRequest.setMataKuliah("Test Course");

        String toString = createLogRequest.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Test Log"));
        assertTrue(toString.contains("Test Course"));
    }

    @Test
    void testNullValues() {
        // Test that null values can be set and retrieved
        createLogRequest.setJudul(null);
        createLogRequest.setMataKuliah(null);
        createLogRequest.setUser(null);
        createLogRequest.setKategori(null);
        createLogRequest.setWaktuMulai(null);
        createLogRequest.setWaktuSelesai(null);
        createLogRequest.setTanggalLog(null);
        createLogRequest.setKeterangan(null);
        createLogRequest.setPesanUntukDosen(null);

        assertAll("All null values should be handled correctly",
                () -> assertNull(createLogRequest.getJudul()),
                () -> assertNull(createLogRequest.getMataKuliah()),
                () -> assertNull(createLogRequest.getUser()),
                () -> assertNull(createLogRequest.getKategori()),
                () -> assertNull(createLogRequest.getWaktuMulai()),
                () -> assertNull(createLogRequest.getWaktuSelesai()),
                () -> assertNull(createLogRequest.getTanggalLog()),
                () -> assertNull(createLogRequest.getKeterangan()),
                () -> assertNull(createLogRequest.getPesanUntukDosen())
        );
    }
}