package id.ac.ui.cs.advprog.hiringgo.matakuliah.repository;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@DataJpaTest
class MataKuliahRepositoryTest {

    @Autowired
    MataKuliahRepository mataKuliahRepository;

    @Test
    void testSaveMataKuliah() {
        MataKuliah matkul = new MataKuliah(
                "CSGE602023 - 01.00.12.01-2024",
                "Pengantar Keamanan Perangkat Lunak",
                "Membahas Keamanan Perangkat Lunak")
                .addDosenPengampu("Dosen A")
                .addDosenPengampu("Dosen B")
                .addDosenPengampu("Dosen C");

        mataKuliahRepository.save(matkul);

        MataKuliah found = mataKuliahRepository.findById(matkul.getKode()).orElse(null);
        assertNotNull(found);
        assertEquals(matkul.getKode(), found.getKode());
        assertEquals(matkul.getNama(), found.getNama());
        assertEquals(matkul.getDeskripsi(), found.getDeskripsi());
        assertEquals(matkul.getDosenPengampu().size(), found.getDosenPengampu().size());
    }

    @Test
    void testUpdateMataKuliah() {
        MataKuliah matkul = new MataKuliah(
                "CSGE602023 - 01.00.12.01-2024",
                "Pengantar Keamanan Perangkat Lunak",
                "Membahas Keamanan Perangkat Lunak")
                .addDosenPengampu("Dosen A");
        mataKuliahRepository.save(matkul);

        MataKuliah updatedMatkul = new MataKuliah(
                matkul.getKode(),
                matkul.getNama(), // sama
                "Mata Kuliah baru")
                .addDosenPengampu("Dosen X");
        mataKuliahRepository.save(updatedMatkul);

        MataKuliah found = mataKuliahRepository.findById(matkul.getKode()).orElse(null);
        assertNotNull(found);
        assertEquals("Mata Kuliah baru", found.getDeskripsi());
        assertEquals(1, found.getDosenPengampu().size());
    }

    @Test
    void testDeleteMataKuliah() {
        MataKuliah matkul = new MataKuliah("CSCM602023 - 01.00.12.01-2020", "Pemrograman Lanjut", "Membahas Java & Spring Boot" )
                .addDosenPengampu("Dosen A")
                .addDosenPengampu("Dosen B");

        mataKuliahRepository.save(matkul);
        assertTrue(mataKuliahRepository.findById(matkul.getKode()).isPresent());

        mataKuliahRepository.deleteById(matkul.getKode());
        assertFalse(mataKuliahRepository.findById(matkul.getKode()).isPresent());
    }

    @Test
    void testFindAllMataKuliahIfEmpty() {
        List<MataKuliah> all = mataKuliahRepository.findAll();
        assertTrue(all.isEmpty());
    }

    @Test
    void testFindAllMataKuliahIfMoreThanOne() {
        MataKuliah mk1 = new MataKuliah(
                "CSCM602023 - 01.00.12.01-2020",
                "Pemrograman Lanjut",
                "Membahas Java & Spring Boot"
        )
                .addDosenPengampu("Dosen A");

        MataKuliah mk2 = new MataKuliah(
                "CSGE602023 - 01.00.12.01-2024",
                "Pengantar Keamanan Perangkat Lunak",
                "Membahas Keamanan Perangkat Lunak"
        )
                .addDosenPengampu("Dosen B");

        mataKuliahRepository.save(mk1);
        mataKuliahRepository.save(mk2);

        List<MataKuliah> all = mataKuliahRepository.findAll();
        assertEquals(2, all.size());
    }
}
