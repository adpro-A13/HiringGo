package id.ac.ui.cs.advprog.hiringgo.matakuliah.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MataKuliahTest {

    @Test
    void testCreateMataKuliah_Success(){
        MataKuliah mataKuliah = new MataKuliah.Builder()
                .withKode("CSCM602023 - 01.00.12.01-2020")
                .withNama("Pemrograman Lanjut")
                .withDeskripsi("Membahas Java & Spring Boot")
                .addDosenPengampu("Dosen A")
                .addDosenPengampu("Dosen B")
                .build();

        assertEquals("CSCM602023 - 01.00.12.01-2020", mataKuliah.getKode());
        assertEquals("Pemrograman Lanjut", mataKuliah.getNama());
        assertEquals("Membahas Java & Spring Boot", mataKuliah.getDeskripsi());
        assertEquals(2, mataKuliah.getDosenPengampu().size());
    }

    @Test
    void testCreateMataKuliahWithoutNama() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MataKuliah.Builder()
                    .withKode("CSCM602023 - 01.00.12.01-2020")
                    .withDeskripsi("Deskripsi")
                    .addDosenPengampu("Dosen A")
                    .build();
        }, "Harap sertakan nama mata kuliah");
    }


    @Test
    void testCreateMataKuliahWithoutKode(){
        assertThrows(IllegalArgumentException.class, () -> {
            new MataKuliah.Builder()
                    .withNama("Pemrograman Lanjut")
                    .withDeskripsi("Deskripsi")
                    .addDosenPengampu("Dosen A")
                    .build();
        }, "Kode Mata Kuliah harus diisi");
    }

    @Test
    void testCreateMataKuliahWithNoDosenPengampu(){
        assertThrows(IllegalArgumentException.class, () -> {
            new MataKuliah.Builder()
                    .withKode("CSCM602023 - 01.00.12.01-2020")
                    .withNama("Pemrograman Lanjut")
                    .withDeskripsi("Deskripsi")
                    .build();
        }, "Harus ada minimal 1 dosen pengampu!");
    }
}