package id.ac.ui.cs.advprog.hiringgo.matakuliah.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MataKuliahTest {

    @Test
    void testCreateMataKuliahandDosenPengampu_Success() {
        MataKuliah mk = new MataKuliah(
                "CSCM602023-01.00.12.01-2020",
                "Pemrograman Lanjut",
                "Membahas Java & Spring Boot"
        );

        assertEquals("CSCM602023-01.00.12.01-2020", mk.getKode());
        assertEquals("Pemrograman Lanjut", mk.getNama());
        assertEquals("Membahas Java & Spring Boot", mk.getDeskripsi());

        assertTrue(mk.getDosenPengampu().isEmpty());

        MataKuliah returned = mk.addDosenPengampu("Dosen A");
        assertSame(mk, returned);

        assertEquals(1, mk.getDosenPengampu().size());
        assertEquals(List.of("Dosen A"), mk.getDosenPengampu());
    }

    @Test
    void testCantModifyDosenPengampuOutsideOfClass() {
        MataKuliah mk = new MataKuliah("CS123", "Nama", "Desc");
        mk.addDosenPengampu("D1");

        List<String> list = mk.getDosenPengampu();
        assertThrows(UnsupportedOperationException.class, () -> list.add("D2"));
    }

    @Test
    void testConstructor_NullKode_Throws() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new MataKuliah(null, "Nama", "Desc")
        );
        assertEquals("Kode Mata Kuliah harus diisi", ex.getMessage());
    }

    @Test
    void testCreateMataKuliahWithoutKode() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new MataKuliah("  ", "Nama", "Desc")
        );
        assertEquals("Kode Mata Kuliah harus diisi", ex.getMessage());
    }

    @Test
    void testCreateMataKuliahWithoutNama() {
        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> new MataKuliah("CS123", null, "Desc")
        );
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> new MataKuliah("CS123", "   ", "Desc")
        );
        assertEquals("Harap sertakan nama mata kuliah", ex1.getMessage());
        assertEquals("Harap sertakan nama mata kuliah", ex2.getMessage());
    }

    @Test
    void testAddNullorBlankDosenPengampu() {
        MataKuliah mk = new MataKuliah("CS123", "Nama", "Desc");

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> mk.addDosenPengampu(null));
        assertEquals("Nama dosen tidak boleh kosong", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,() -> mk.addDosenPengampu("   "));
        assertEquals("Nama dosen tidak boleh kosong", ex2.getMessage());
    }
}