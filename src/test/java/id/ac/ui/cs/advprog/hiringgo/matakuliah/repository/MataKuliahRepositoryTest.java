package id.ac.ui.cs.advprog.hiringgo.matakuliah.repository;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@DataJpaTest
class MataKuliahRepositoryTest {

    @Autowired
    MataKuliahRepository mataKuliahRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void testSaveMataKuliah() {
        Dosen dosenA = new Dosen("a@u.id", "pass", "A", "123");
        Dosen dosenB = new Dosen("b@u.id", "pass", "B", "124");
        entityManager.persist(dosenA);
        entityManager.persist(dosenB);

        MataKuliah matkul = new MataKuliah(
                "CSGE602023 - 01.00.12.01-2024",
                "Pengantar Keamanan Perangkat Lunak",
                "Membahas Keamanan Perangkat Lunak")
                .addDosenPengampu(dosenA)
                .addDosenPengampu(dosenB);

        mataKuliahRepository.save(matkul);

        MataKuliah found = mataKuliahRepository.findByKode(matkul.getKode()).orElse(null);
        assertNotNull(found);
        assertEquals(matkul.getKode(), found.getKode());
        assertEquals(matkul.getNama(), found.getNama());
        assertEquals(matkul.getDeskripsi(), found.getDeskripsi());
        assertEquals(2, found.getDosenPengampu().size());
    }

    @Test
    void testUpdateMataKuliah() {
        Dosen dosenA = new Dosen("a@u.id", "pass", "A", "123");
        entityManager.persist(dosenA);

        MataKuliah matkul = new MataKuliah(
                "CSGE602023 - 01.00.12.01-2024",
                "Pengantar Keamanan Perangkat Lunak",
                "Membahas Keamanan Perangkat Lunak")
                .addDosenPengampu(dosenA);
        mataKuliahRepository.save(matkul);

        Dosen dosenB = new Dosen("b@u.id", "pass", "B", "124");
        entityManager.persist(dosenB);
        MataKuliah updatedMatkul = new MataKuliah(
                matkul.getKode(),
                matkul.getNama(), // sama
                "Mata Kuliah baru")
                .addDosenPengampu(dosenB);
        mataKuliahRepository.save(updatedMatkul);

        MataKuliah found = mataKuliahRepository.findByKode(matkul.getKode()).orElse(null);
        assertNotNull(found);
        assertEquals("Mata Kuliah baru", found.getDeskripsi());
        assertEquals(1, found.getDosenPengampu().size());
    }

    @Test
    void testDeleteMataKuliah() {
        MataKuliah matkul = new MataKuliah("CSCM602023 - 01.00.12.01-2020", "Pemrograman Lanjut", "Membahas Java & Spring Boot" );

        mataKuliahRepository.save(matkul);
        assertTrue(mataKuliahRepository.findByKode(matkul.getKode()).isPresent());

        mataKuliahRepository.deleteById(matkul.getKode());
        assertFalse(mataKuliahRepository.findByKode(matkul.getKode()).isPresent());
    }

    @Test
    void testFindAllMataKuliahIfEmpty() {
        List<MataKuliah> all = mataKuliahRepository.findAll();
        assertTrue(all.isEmpty());
    }

    @Test
    void testFindAllMataKuliahIfMoreThanOne() {
        Dosen dosenA = new Dosen("a@u.id", "pass", "A", "123");
        entityManager.persist(dosenA);

        MataKuliah mk1 = new MataKuliah(
                "CSCM602023 - 01.00.12.01-2020",
                "Pemrograman Lanjut",
                "Membahas Java & Spring Boot"
        )
                .addDosenPengampu(dosenA);

        Dosen dosenB = new Dosen("b@u.id", "pass", "B", "124");
        entityManager.persist(dosenB);
        MataKuliah mk2 = new MataKuliah(
                "CSGE602023 - 01.00.12.01-2024",
                "Pengantar Keamanan Perangkat Lunak",
                "Membahas Keamanan Perangkat Lunak"
        )
                .addDosenPengampu(dosenB);

        mataKuliahRepository.save(mk1);
        mataKuliahRepository.save(mk2);

        List<MataKuliah> all = mataKuliahRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testFindByDosenPengampu(){
        Dosen dosen = new Dosen("dosen@u.id", "pass", "dosen", "124");
        entityManager.persist(dosen);

        MataKuliah matkul = new MataKuliah("CS101", "Algoritma", "Dasar");
        matkul.addDosenPengampu(dosen);
        entityManager.persist(matkul);

        MataKuliah matkul2 = new MataKuliah("CS002", "Jaringan", "Belajar TCP/IP");
        matkul2.addDosenPengampu(dosen);
        entityManager.persist(matkul2);

        entityManager.flush();

        List<MataKuliah> result = mataKuliahRepository.findByDosenPengampu(dosen);

        assertEquals(2, result.size());
        assertEquals("CS101", result.get(0).getKode());
    }
}
