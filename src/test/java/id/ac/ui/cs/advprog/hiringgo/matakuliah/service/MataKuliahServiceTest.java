package id.ac.ui.cs.advprog.hiringgo.matakuliah.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.exception.MataKuliahNotFoundException;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MataKuliahServiceTest {
    @Mock
    MataKuliahRepository mataKuliahRepository;
    @InjectMocks
    private MataKuliahServiceImpl mataKuliahService;

    @Test
    void testCreateMataKuliah() {
        MataKuliah matkul = new MataKuliah("CS001", "Dasar", "Deskripsi");
        when(mataKuliahRepository.save(matkul)).thenReturn(matkul);

        MataKuliah result = mataKuliahService.create(matkul);

        assertNotNull(result);
        assertEquals("CS001", result.getKode());
        verify(mataKuliahRepository).save(matkul);
    }

    @Test
    void testCreateWhenKodeAlreadyExists_ShouldThrow() {
        MataKuliah matkul = new MataKuliah("CS010", "Test", "Desc");
        when(mataKuliahRepository.existsById("CS010")).thenReturn(true);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> mataKuliahService.create(matkul)
        );
        assertEquals("Kode sudah digunakan.", ex.getMessage());

        verify(mataKuliahRepository).existsById("CS010");
        verify(mataKuliahRepository, never()).save(any());
    }

    @Test
    void testFindByKodeExists() {
        MataKuliah matkul = new MataKuliah("CS001", "Dasar", "Deskripsi");
        when(mataKuliahRepository.findByKode("CS001")).thenReturn(Optional.of(matkul));

        MataKuliah result = mataKuliahService.findByKode("CS001");

        assertNotNull(result);
        assertEquals("Dasar", result.getNama());
        verify(mataKuliahRepository).findByKode("CS001");
    }

    @Test
    void testFindByKodeNotFound() {
        when(mataKuliahRepository.findByKode("CS002")).thenReturn(Optional.empty());

        MataKuliahNotFoundException ex = assertThrows(
                MataKuliahNotFoundException.class,
                () -> mataKuliahService.findByKode("CS002")
        );

        assertEquals("Mata kuliah tidak ditemukan", ex.getMessage());
        verify(mataKuliahRepository).findByKode("CS002");
    }

    @Test
    void testUpdateMataKuliah() {
        MataKuliah original = new MataKuliah("CS003", "Pemrograman", "Spring Boot");

        when(mataKuliahRepository.existsById("CS003")).thenReturn(true);
        when(mataKuliahRepository.save(any(MataKuliah.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MataKuliah updated = new MataKuliah("CS003", "Pemrograman", "Belajar Design Pattern");

        MataKuliah result = mataKuliahService.update(updated);

        assertEquals("CS003", result.getKode());
        assertEquals("Pemrograman", result.getNama());
        assertEquals("Belajar Design Pattern", result.getDeskripsi());

        verify(mataKuliahRepository).existsById("CS003");
        verify(mataKuliahRepository).save(updated);
    }

    @Test
    void testUpdateMataKuliahNotFound() {
        MataKuliah matkul = new MataKuliah("CS020", "Test", "Desc");
        when(mataKuliahRepository.existsById("CS020")).thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> mataKuliahService.update(matkul)
        );
        assertEquals("Mata Kuliah tidak ditemukan.", ex.getMessage());

        verify(mataKuliahRepository).existsById("CS020");
        verify(mataKuliahRepository, never()).save(any());
    }

    @Test
    void testFindByDosenPengampu(){
        Dosen dosen = mock(Dosen.class);

        MataKuliah m1 = new MataKuliah("CS101", "Algoritma", "Dasar algoritma");
        MataKuliah m2 = new MataKuliah("CS102", "Struktur Data", "List, Stack, Queue");

        List<MataKuliah> listMK = List.of(m1, m2);
        when(mataKuliahRepository.findByDosenPengampu(dosen)).thenReturn(listMK);

        List<MataKuliah> result = mataKuliahService.findByDosenPengampu(dosen);

        assertEquals(2, result.size());
        assertEquals("CS101", result.getFirst().getKode());
        verify(mataKuliahRepository).findByDosenPengampu(dosen);
    }

    @Test
    void testFindAllMataKuliah() {
        MataKuliah m1 = new MataKuliah("CS001", "Dasar", "desc");
        MataKuliah m2 = new MataKuliah("CS002", "Lanjut", "desc");

        when(mataKuliahRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MataKuliah> result = mataKuliahService.findAll();

        assertEquals(2, result.size());
        verify(mataKuliahRepository).findAll();
    }

    @Test
    void testDeleteMataKuliah() {
        mataKuliahService.deleteByKode("CS001");
        verify(mataKuliahRepository).deleteById("CS001");
    }
}