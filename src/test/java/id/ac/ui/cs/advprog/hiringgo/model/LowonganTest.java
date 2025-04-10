package id.ac.ui.cs.advprog.hiringgo.model;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LowonganTest {

    private List<String> idAsdosDiterima;

    @BeforeEach
    void setUp() {
        idAsdosDiterima = new ArrayList<>();
        idAsdosDiterima.add("mahasiswa-001");
        idAsdosDiterima.add("mahasiswa-002");
    }

    @Test
    void testCreateLowonganSuccess() {
        Lowongan lowongan = new Lowongan(
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "CSUI-MK1",
                "2024/2025",
                "DIBUKA",
                "GANJIL",
                3,
                2,
                5,
                idAsdosDiterima
        );

        assertEquals(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"), lowongan.getLowonganId());
        assertEquals("CSUI-MK1", lowongan.getIdMataKuliah());
        assertEquals("2024/2025", lowongan.getTahunAjaran());
        assertEquals(StatusLowongan.DIBUKA, lowongan.getStatusLowongan());
        assertEquals(Semester.GANJIL, lowongan.getSemester());
        assertEquals(3, lowongan.getJumlahAsdosDibutuhkan());
        assertEquals(2, lowongan.getJumlahAsdosDiterima());
        assertEquals(5, lowongan.getJumlahAsdosPendaftar());
        assertEquals(2, lowongan.getIdAsdosDiterima().size());
        assertTrue(lowongan.getIdAsdosDiterima().contains("mahasiswa-001"));
    }

    @Test
    void testCreateLowonganInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Lowongan(
                    "a2c62328-4a37-4664-83c7-f32db8620155",
                    "CSUI-MK2",
                    "2024/2025",
                    "MEOW", // status tidak valid
                    "GENAP",
                    2,
                    0,
                    1,
                    new ArrayList<>()
            );
        });
    }

    @Test
    void testSetStatusLowonganToValidValue() {
        Lowongan lowongan = new Lowongan();
        lowongan.setStatusLowongan("DIBUKA");
        assertEquals(StatusLowongan.DIBUKA, lowongan.getStatusLowongan());
    }

    @Test
    void testSetStatusLowonganToInvalidValue() {
        Lowongan lowongan = new Lowongan();
        assertThrows(IllegalArgumentException.class, () -> {
            lowongan.setStatusLowongan("TUTUP_TAPI_BOONG");
        });
    }

    @Test
    void testSetSemesterToInvalidValue() {
        Lowongan lowongan = new Lowongan();
        assertThrows(IllegalArgumentException.class, () -> {
            lowongan.setSemester("SUMMER");
        });
    }

    @Test
    void testDefaultConstructorAndSetters() {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(UUID.fromString("13652556-012a-4c07-b546-54eb1396d79b"));
        lowongan.setIdMataKuliah("CSUI-MK3");
        lowongan.setTahunAjaran("2024/2025");
        lowongan.setStatusLowongan("DITUTUP");
        lowongan.setSemester("GENAP");
        lowongan.setJumlahAsdosDibutuhkan(4);
        lowongan.setJumlahAsdosDiterima(1);
        lowongan.setJumlahAsdosPendaftar(7);
        lowongan.setIdAsdosDiterima(List.of("mahasiswa-003"));

        assertEquals(UUID.fromString("13652556-012a-4c07-b546-54eb1396d79b"), lowongan.getLowonganId());
        assertEquals(StatusLowongan.DITUTUP, lowongan.getStatusLowongan());
        assertEquals("mahasiswa-003", lowongan.getIdAsdosDiterima().get(0));
    }
}
