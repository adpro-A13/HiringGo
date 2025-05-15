package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LowonganTest {
    private List<Pendaftaran> daftarPendaftaran;
    MataKuliah mataKuliah;
    @BeforeEach
    void setUp() {
        mataKuliah = new MataKuliah("CS100", "Advprog", "mata kuliah sigma");
    }

    @Test
    void testCreateLowonganSuccess() {
        MataKuliah mataKuliah = new MataKuliah("CS100", "Advprog", "mata kuliah sigma");
        Lowongan lowongan = new Lowongan(
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                mataKuliah,
                "2024/2025",
                "DIBUKA",
                "GANJIL",
                3,
                2,
                5
        );

        assertEquals(UUID.fromString("eb558e9f-1c39-460e-8860-71af6af63bd6"), lowongan.getLowonganId());
        assertEquals(mataKuliah, lowongan.getMataKuliah());
        assertEquals("2024/2025", lowongan.getTahunAjaran());
        assertEquals(StatusLowongan.DIBUKA, lowongan.getStatusLowongan());
        assertEquals(Semester.GANJIL, lowongan.getSemester());
        assertEquals(3, lowongan.getJumlahAsdosDibutuhkan());
        assertEquals(2, lowongan.getJumlahAsdosDiterima());
        assertEquals(5, lowongan.getJumlahAsdosPendaftar());
    }

    @Test
    void testCreateLowonganInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Lowongan(
                    "a2c62328-4a37-4664-83c7-f32db8620155",
                    mataKuliah,
                    "2024/2025",
                    "MEOW", // status tidak valid
                    "GENAP",
                    2,
                    0,
                    1
            );
        });
    }
    @Test
    void testCreateLowonganInvalidSemester() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Lowongan(
                    "a2c62328-4a37-4664-83c7-f32db8620155",
                    mataKuliah,
                    "2024/2025",
                    "DIBUKA", // status tidak valid
                    "semester 200",
                    2,
                    0,
                    1
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

}
