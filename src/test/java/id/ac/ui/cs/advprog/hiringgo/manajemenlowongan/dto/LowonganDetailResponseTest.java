package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LowonganDetailResponseTest {

    @Test
    @DisplayName("Test LowonganDetailResponse constructor with Lowongan")
    void testConstructorWithLowongan() {
        UUID lowonganId = UUID.randomUUID();
        String idMataKuliah = "CSGE602022";
        String tahunAjaran = "2023/2024";
        Semester semester = Semester.GENAP;
        StatusLowongan statusLowongan = StatusLowongan.DIBUKA;
        int jumlahAsdosDibutuhkan = 5;
        int jumlahAsdosDiterima = 2;
        int jumlahAsdosPendaftar = 10;

        MataKuliah mataKuliah = new MataKuliah("CSGE602022", "AdvProg", "Design Pattern");
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setTahunAjaran(tahunAjaran);
        lowongan.setSemester(semester.toString());
        lowongan.setStatusLowongan(statusLowongan.toString());
        lowongan.setJumlahAsdosDibutuhkan(jumlahAsdosDibutuhkan);
        lowongan.setJumlahAsdosDiterima(jumlahAsdosDiterima);
        lowongan.setJumlahAsdosPendaftar(jumlahAsdosPendaftar);

        LowonganDetailResponse response = new LowonganDetailResponse(lowongan);
        assertEquals(lowonganId, response.getLowonganId());
        assertEquals(idMataKuliah, response.getIdMataKuliah());
        assertEquals("Mata Kuliah " + idMataKuliah, response.getMataKuliah());
        assertEquals(tahunAjaran, response.getTahunAjaran());
        assertEquals(semester.toString(), response.getSemester());
        assertEquals(statusLowongan.toString(), response.getStatusLowongan());
        assertEquals("Asisten Dosen " + idMataKuliah, response.getJudul());
        assertTrue(response.getDeskripsi().contains(idMataKuliah));
        assertTrue(response.getPersyaratan().contains(idMataKuliah));
        assertEquals(jumlahAsdosDibutuhkan, response.getJumlahAsdosDibutuhkan());
        assertEquals(jumlahAsdosDiterima, response.getJumlahAsdosDiterima());
        assertEquals(jumlahAsdosPendaftar, response.getJumlahAsdosPendaftar());
    }

    @Test
    @DisplayName("Test LowonganDetailResponse setters")
    void testLowonganDetailResponseSetters() {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(UUID.randomUUID());
        MataKuliah mataKuliah = new MataKuliah("TEST101", "AdvProg", "Design Pattern");
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setSemester("GANJIL");
        lowongan.setStatusLowongan("DIBUKA");
        lowongan.setTahunAjaran("2023/2024");

        LowonganDetailResponse response = new LowonganDetailResponse(lowongan);

        UUID lowonganId = UUID.randomUUID();
        response.setLowonganId(lowonganId);
        response.setIdMataKuliah("CSGE602022");
        response.setMataKuliah("Programming Foundations 2");
        response.setTahunAjaran("2023/2024");
        response.setSemester("GENAP");
        response.setStatusLowongan("DIBUKA");
        response.setJudul("Teaching Assistant Position");
        response.setDeskripsi("Assist in labs and grading");
        response.setPersyaratan("Min GPA 3.0");
        response.setJumlahAsdosDibutuhkan(3);
        response.setJumlahAsdosDiterima(1);
        response.setJumlahAsdosPendaftar(5);

        // Verify values
        assertEquals(lowonganId, response.getLowonganId());
        assertEquals("CSGE602022", response.getIdMataKuliah());
        assertEquals("Programming Foundations 2", response.getMataKuliah());
        assertEquals("2023/2024", response.getTahunAjaran());
        assertEquals("GENAP", response.getSemester());
        assertEquals("DIBUKA", response.getStatusLowongan());
        assertEquals("Teaching Assistant Position", response.getJudul());
        assertEquals("Assist in labs and grading", response.getDeskripsi());
        assertEquals("Min GPA 3.0", response.getPersyaratan());
        assertEquals(3, response.getJumlahAsdosDibutuhkan());
        assertEquals(1, response.getJumlahAsdosDiterima());
        assertEquals(5, response.getJumlahAsdosPendaftar());
    }

    // ────────────────────────────────────────────────────────────────────────────────
    // New test to cover equals(Object) and hashCode()
    // ────────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Test equals() and hashCode() for LowonganDetailResponse")
    void testEqualsAndHashCode() {
        MataKuliah mk = new MataKuliah("ABC123", "Course", "Desc");
        Lowongan l = new Lowongan();
        l.setLowonganId(UUID.randomUUID());
        l.setMataKuliah(mk);
        l.setTahunAjaran("2024/2025");
        l.setSemester("GANJIL");
        l.setStatusLowongan("DIBUKA");
        l.setJumlahAsdosDibutuhkan(1);
        l.setJumlahAsdosDiterima(0);
        l.setJumlahAsdosPendaftar(0);

        LowonganDetailResponse a = new LowonganDetailResponse(l);
        LowonganDetailResponse b = new LowonganDetailResponse(l);

        // reflexive and symmetric
        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(b, a);

        // consistent hashCode
        assertEquals(a.hashCode(), b.hashCode());

        // null and different type
        assertNotEquals(a, null);
        assertNotEquals(a, "string");

        // change one field -> not equal
        b.setStatusLowongan("TUTUP");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }
}
