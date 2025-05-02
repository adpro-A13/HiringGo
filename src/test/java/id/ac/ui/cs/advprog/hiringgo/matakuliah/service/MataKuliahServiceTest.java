package id.ac.ui.cs.advprog.hiringgo.matakuliah.service;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.junit.jupiter.api.BeforeEach;
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
    void testFindByKodeExists() {
        MataKuliah matkul = new MataKuliah("CS001", "Dasar", "Deskripsi");
        when(mataKuliahRepository.findById("CS001")).thenReturn(Optional.of(matkul));

        MataKuliah result = mataKuliahService.findByKode("CS001");

        assertNotNull(result);
        assertEquals("Dasar", result.getNama());
        verify(mataKuliahRepository).findById("CS001");
    }

    @Test
    void testFindByKodeNotFound() {
        when(mataKuliahRepository.findById("CS002")).thenReturn(Optional.empty());

        MataKuliah result = mataKuliahService.findByKode("CS002");

        assertNull(result);
        verify(mataKuliahRepository).findById("CS002");
    }

    @Test
    void testUpdateMataKuliah() {
        MataKuliah matkul = new MataKuliah("CS003", "Pemrograman", "Spring Boot");
        when(mataKuliahRepository.save(matkul)).thenReturn(matkul);

        MataKuliah result = mataKuliahService.update(matkul);

        assertEquals("CS003", result.getKode());
        assertEquals("Pemrograman", result.getNama());
        verify(mataKuliahRepository).save(matkul);
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