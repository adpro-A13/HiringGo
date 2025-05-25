package id.ac.ui.cs.advprog.hiringgo.matakuliah.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MataKuliahTest {

    @Test
    void testCreateMataKuliahandDosenPengampuSuccess() {
        MataKuliah mk = new MataKuliah(
                "CSCM602023-01.00.12.01-2020",
                "Pemrograman Lanjut",
                "Membahas Java & Spring Boot"
        );

        assertEquals("CSCM602023-01.00.12.01-2020", mk.getKode());
        assertEquals("Pemrograman Lanjut", mk.getNama());
        assertEquals("Membahas Java & Spring Boot", mk.getDeskripsi());

        assertTrue(mk.getDosenPengampu().isEmpty());

        Dosen dosenA = new Dosen("dosen1@univ.ac.id", "pass", "Dosen A", "12345");
        MataKuliah returned = mk.addDosenPengampu(dosenA);
        assertSame(mk, returned);

        assertEquals(1, mk.getDosenPengampu().size());
        assertTrue(mk.getDosenPengampu().contains(dosenA));
    }

    @Test
    void testCantModifyDosenPengampuOutsideOfClass() {
        MataKuliah mk = new MataKuliah("CS123", "Nama", "Desc");
        Dosen dosenA = new Dosen("dosen1@univ.ac.id", "pass", "Dosen A", "12345");
        mk.addDosenPengampu(dosenA);

        Set<Dosen> dosenPengampu = mk.getDosenPengampu();
        Dosen dosenB = new Dosen("dosen3@ITB.ac.id", "pass", "Dosen B", "12543");
        assertThrows(UnsupportedOperationException.class, () -> dosenPengampu.add(dosenB));
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
        assertEquals("Dosen tidak boleh null", ex1.getMessage());
    }
    @Test
    void constructorAndGetters() {
        MataKuliah mk = new MataKuliah("CS001", "Dasar", "Deskripsi");
        assertEquals("CS001", mk.getKode());
        assertEquals("Dasar", mk.getNama());
        assertEquals("Deskripsi", mk.getDeskripsi());
        assertTrue(mk.getDosenPengampu().isEmpty());
    }

    @Test
    void setterNamaDeskripsi() {
        MataKuliah mk = new MataKuliah("CS002", "OldName", "OldDesc");
        mk.setNama("NewName");
        mk.setDeskripsi("NewDesc");
        assertEquals("NewName", mk.getNama());
        assertEquals("NewDesc", mk.getDeskripsi());
    }

    @Test
    void addDosenPengampuAndImmutability() {
        MataKuliah mk = new MataKuliah("CS003", "M1", "D1");
        Dosen dosen = new Dosen("a@ui.ac.id", "pass", "FullName", "NIP001");
        mk.addDosenPengampu(dosen);
        Set<Dosen> pengampu = mk.getDosenPengampu();
        assertTrue(pengampu.contains(dosen));
        assertThrows(UnsupportedOperationException.class, () -> pengampu.add(dosen));
    }

    @Test
    void addInvalidDosenThrows() {
        MataKuliah mk = new MataKuliah("CS004", "M2", "D2");
        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> mk.addDosenPengampu(null)
        );
        assertEquals("Dosen tidak boleh null", ex1.getMessage());
        Dosen invalid = new Dosen("e@ui.ac.id", "pw", "Name", "   ");
        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> mk.addDosenPengampu(invalid)
        );
        assertEquals("Dosen tidak boleh null", ex2.getMessage());
    }

    @Test
    void equalsAndHashCodeBasedOnKode() {
        MataKuliah mk1 = new MataKuliah("CS005", "N1", "D1");
        MataKuliah mk2 = new MataKuliah("CS005", "N2", "D2");
        MataKuliah mk3 = new MataKuliah("CS006", "N1", "D1");

        assertEquals(mk1, mk2);
        assertEquals(mk1.hashCode(), mk2.hashCode());
        assertNotEquals(mk1, mk3);
        assertNotEquals(mk1.hashCode(), mk3.hashCode());
        assertNotEquals(null, mk1);
        assertNotEquals("mk1", mk1);
    }

    @Test
    void toStringContainsKode() {
        MataKuliah mk = new MataKuliah("CS007", "M3", "D3");
        String repr = mk.toString();
        assertTrue(repr.contains("CS007"), () -> "toString should include kode, was: " + repr);
    }
}