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

class LowonganResponseTest {

    @Test
    @DisplayName("Test LowonganResponse constructor with Lowongan")
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

        LowonganResponse response = new LowonganResponse(lowongan);

        assertEquals(lowonganId, response.getLowonganId());
        assertEquals(idMataKuliah, response.getIdMataKuliah());
        assertEquals(tahunAjaran, response.getTahunAjaran());
        assertEquals(semester, response.getSemester());  // Compare with enum directly
        assertEquals(statusLowongan, response.getStatusLowongan());  // Compare with enum directly
        assertEquals(jumlahAsdosDibutuhkan, response.getJumlahAsdosDibutuhkan());
        assertEquals(jumlahAsdosDiterima, response.getJumlahAsdosDiterima());
        assertEquals(jumlahAsdosPendaftar, response.getJumlahAsdosPendaftar());
        assertEquals(idAsdosDiterima, response.getIdAsdosDiterima());
    }

    @Test
    @DisplayName("Test LowonganResponse setters")
    void testLowonganResponseSetters() {
        LowonganResponse response = new LowonganResponse(new Lowongan());

        UUID lowonganId = UUID.randomUUID();
        response.setLowonganId(lowonganId);
        response.setIdMataKuliah("CSGE602022");
        response.setTahunAjaran("2023/2024");
        response.setSemester(Semester.GENAP);  // Use enum directly
        response.setStatusLowongan(StatusLowongan.DIBUKA);  // Use enum directly
        response.setJumlahAsdosDibutuhkan(3);
        response.setJumlahAsdosDiterima(1);
        response.setJumlahAsdosPendaftar(5);
        List<String> idAsdosDiterima = Arrays.asList("user3");
        response.setIdAsdosDiterima(idAsdosDiterima);

        assertEquals(lowonganId, response.getLowonganId());
        assertEquals("CSGE602022", response.getIdMataKuliah());
        assertEquals("2023/2024", response.getTahunAjaran());
        assertEquals(Semester.GENAP, response.getSemester());  // Compare with enum directly
        assertEquals(StatusLowongan.DIBUKA, response.getStatusLowongan());  // Compare with enum directly
        assertEquals(3, response.getJumlahAsdosDibutuhkan());
        assertEquals(1, response.getJumlahAsdosDiterima());
        assertEquals(5, response.getJumlahAsdosPendaftar());
        assertEquals(idAsdosDiterima, response.getIdAsdosDiterima());
    }
}