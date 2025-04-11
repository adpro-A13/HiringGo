package id.ac.ui.cs.advprog.hiringgo.repository;

import id.ac.ui.cs.advprog.hiringgo.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MataKuliahRepositoryTest {

    MataKuliahRepository mataKuliahRepository;

    List<MataKuliah> listMataKuliah;

    @BeforeEach
    public void setUp() {
        mataKuliahRepository = new MataKuliahRepository();

        listMataKuliah = new ArrayList<>();

        MataKuliah matkul1 = new MataKuliah.Builder()
                .withKode("CSCM602023 - 01.00.12.01-2020")
                .withNama("Pemrograman Lanjut")
                .withDeskripsi("Membahas Java & Spring Boot")
                .addDosenPengampu("Dosen A")
                .addDosenPengampu("Dosen B")
                .build();
        listMataKuliah.add(matkul1);

        MataKuliah matkul2 = new MataKuliah.Builder()
                .withKode("CSGE602023 - 01.00.12.01-2024")
                .withNama("Pengantar Keamanan Perangkat Lunak")
                .withDeskripsi("Membahas Keamanan Perangkat Lunak")
                .addDosenPengampu("Dosen A")
                .addDosenPengampu("Dosen B")
                .addDosenPengampu("Dosen C")
                .build();
        listMataKuliah.add(matkul2);
    }

    @Test
    void testSaveMataKuliah(){
        MataKuliah matkul = listMataKuliah.get(1);
        MataKuliah result = mataKuliahRepository.save(matkul);

        MataKuliah findResult = mataKuliahRepository.findByKode(listMataKuliah.get(1).getKode());
        assertEquals(matkul.getKode(), result.getKode());
        assertEquals(matkul.getKode(), findResult.getKode());
        assertEquals(matkul.getNama(), findResult.getNama());
        assertEquals(matkul.getDeskripsi(), findResult.getDeskripsi());

        for (int i = 0; i < matkul.getDosenPengampu().size(); i++) {
            assertEquals(matkul.getDosenPengampu().get(i), findResult.getDosenPengampu().get(i));
        }
    }

    @Test
    void testUpdateMataKuliah(){
        MataKuliah matkul = listMataKuliah.get(1);
        MataKuliah result = mataKuliahRepository.save(matkul);
        MataKuliah updatedMatkul = new MataKuliah.Builder()
                .withKode(matkul.getKode())
                .withNama(matkul.getNama())
                .withDeskripsi("Mata Kuliah baru")
                .addDosenPengampu("Dosen X")
                .build();

        mataKuliahRepository.save(updatedMatkul);
        MataKuliah findResult = mataKuliahRepository.findByKode(updatedMatkul.getKode());
        assertNotNull(findResult);
        assertEquals(matkul.getKode(), findResult.getKode());
        assertEquals(matkul.getNama(), findResult.getNama());
        assertEquals("Mata Kuliah baru", findResult.getDeskripsi());
        assertEquals(1, findResult.getDosenPengampu().size());
    }

    @Test
    void testDeleteMataKuliah(){
        MataKuliah matkul = listMataKuliah.get(1);
        MataKuliah result = mataKuliahRepository.save(matkul);

        assertNotNull(mataKuliahRepository.findByKode(result.getKode()));
        mataKuliahRepository.delete(result.getKode());
        assertNull(mataKuliahRepository.findByKode(result.getKode()));
    }

    @Test
    void testFindMataKuliahByKode_Found(){
        for (MataKuliah matkul : listMataKuliah) {
            mataKuliahRepository.save(matkul);
        }

        MataKuliah findResult = mataKuliahRepository.findByKode(listMataKuliah.get(1).getKode());
        assertEquals(listMataKuliah.get(1).getKode(), findResult.getKode());
        assertEquals(listMataKuliah.get(1).getNama(), findResult.getNama());
        assertEquals(listMataKuliah.get(1).getDeskripsi(), findResult.getDeskripsi());

        for (int i = 0; i < listMataKuliah.get(1).getDosenPengampu().size(); i++) {
            assertEquals(listMataKuliah.get(1).getDosenPengampu().get(i), findResult.getDosenPengampu().get(i));
        }
    }

    @Test
    void testFindMataKuliahByKode_NotFound(){
        for (MataKuliah matkul : listMataKuliah) {
            mataKuliahRepository.save(matkul);
        }

        MataKuliah findResult = mataKuliahRepository.findByKode("ARISE");
        assertNull(findResult);
    }

    @Test
    void testFindAllMataKuliahIfEmpty(){
        Iterator<MataKuliah> mataKuliahIterator = MataKuliahRepository.findAll();
        assertFalse(mataKuliahIterator.hasNext());
    }

    @Test
    void testFindAllMataKuliahIfMoreThanOne(){
        for (MataKuliah matkul : listMataKuliah) {
            mataKuliahRepository.save(matkul);
        }

        Iterator<MataKuliah> mataKuliahIterator = MataKuliahRepository.findAll();
        assertTrue(mataKuliahIterator.hasNext());
        MataKuliah findResult = mataKuliahIterator.next();
        String kode = findResult.getKode();
        assertEquals(kode, mataKuliahRepository.findByKode(kode).getKode());
        assertTrue(mataKuliahIterator.hasNext());
        findResult = mataKuliahIterator.next();
        kode = findResult.getKode();
        assertEquals(kode, mataKuliahRepository.findByKode(kode).getKode());
        assertFalse(mataKuliahIterator.hasNext());
    }
}
