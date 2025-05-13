package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
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
        List<String> idAsdosDiterima = Arrays.asList("user1", "user2");

        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setIdMataKuliah(idMataKuliah);
        lowongan.setTahunAjaran(tahunAjaran);
        lowongan.setSemester(semester.toString());  // Convert enum to String
        lowongan.setStatusLowongan(statusLowongan.toString());  // Convert enum to String
        lowongan.setJumlahAsdosDibutuhkan(jumlahAsdosDibutuhkan);
        lowongan.setJumlahAsdosDiterima(jumlahAsdosDiterima);
        lowongan.setJumlahAsdosPendaftar(jumlahAsdosPendaftar);
        lowongan.setIdAsdosDiterima(idAsdosDiterima);

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
        assertEquals(idAsdosDiterima, response.getIdAsdosDiterima());
    }

    @Test
    @DisplayName("Test LowonganDetailResponse setters")
    void testLowonganDetailResponseSetters() {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(UUID.randomUUID());
        lowongan.setIdMataKuliah("TEST101");
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
        List<String> idAsdosDiterima = Arrays.asList("user3");
        response.setIdAsdosDiterima(idAsdosDiterima);

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
        assertEquals(idAsdosDiterima, response.getIdAsdosDiterima());
    }
}