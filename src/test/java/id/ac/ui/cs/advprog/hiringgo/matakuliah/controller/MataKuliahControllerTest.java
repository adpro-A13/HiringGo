package id.ac.ui.cs.advprog.hiringgo.matakuliah.controller;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.service.MataKuliahService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MataKuliahControllerTest {
    @Mock
    private MataKuliahService mataKuliahService;
    @InjectMocks
    private MataKuliahController mataKuliahController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMataKuliah() {
        MataKuliah mk1 = new MataKuliah("CS101", "Pemrograman Dasar", "Deskripsi");
        MataKuliah mk2 = new MataKuliah("CS102", "Algoritma", "Deskripsi");
        List<MataKuliah> mataKuliahList = Arrays.asList(mk1, mk2);

        when(mataKuliahService.findAll()).thenReturn(mataKuliahList);

        ResponseEntity<List<MataKuliah>> response = mataKuliahController.getAllMataKuliah();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(mataKuliahService, times(1)).findAll();
    }

    @Test
    void testGetMataKuliahByKode_Found() {
        String kode = "CS101";
        MataKuliah mk = new MataKuliah(kode, "Pemrograman Dasar", "Deskripsi");

        when(mataKuliahService.findByKode(kode)).thenReturn(mk);

        ResponseEntity<MataKuliah> response = mataKuliahController.getMataKuliahByKode(kode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(kode, response.getBody().getKode());
        verify(mataKuliahService, times(1)).findByKode(kode);
    }

    @Test
    void testGetMataKuliahByKode_NotFound() {
        String kode = "CS101";
        when(mataKuliahService.findByKode(kode)).thenReturn(null);

        ResponseEntity<MataKuliah> response = mataKuliahController.getMataKuliahByKode(kode);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(mataKuliahService, times(1)).findByKode(kode);
    }

    @Test
    void testCreateMataKuliah_Success() {
        MataKuliah newMk = new MataKuliah("CS101", "Pemrograman Dasar", "Deskripsi");

        when(mataKuliahService.create(any(MataKuliah.class))).thenReturn(newMk);

        ResponseEntity<MataKuliah> response = mataKuliahController.createMataKuliah(newMataKuliah);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CS101", response.getBody().getKode());
        verify(mataKuliahService, times(1)).create(any(MataKuliah.class));
    }

    @Test
    void testCreateMataKuliah_Duplicate() {
        MataKuliah dupMk = new MataKuliah("CS101", "Pemrograman Dasar", "Deskripsi");

        when(mataKuliahService.create(any(MataKuliah.class))).thenThrow(new IllegalArgumentException("Kode sudah digunakan."));

        ResponseEntity<MataKuliah> response = mataKuliahController.createMataKuliah(dupMk);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(mataKuliahService, times(1)).create(any(MataKuliah.class));
    }

    @Test
    void testUpdateMataKuliah_Success() {
        MataKuliah updated = new MataKuliah("CS101", "PKPL", "Deskripsi");

        when(mataKuliahService.update(any(MataKuliah.class))).thenReturn(updated);

        ResponseEntity<MataKuliah> response = mataKuliahController.updateMataKuliah("CS101", updatedMataKuliah);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CS101", response.getBody().getKode());
        verify(mataKuliahService, times(1)).update(any(MataKuliah.class));
    }

    @Test
    void testUpdateMataKuliah_NotFound() {
        MataKuliah nonExistentMataKuliah = new MataKuliah("CS999", "Mata Kuliah Tidak Ada", "Deskripsi");

        when(mataKuliahService.update(any(MataKuliah.class))).thenThrow(new IllegalArgumentException("Mata Kuliah tidak ditemukan."));

        ResponseEntity<MataKuliah> response = mataKuliahController.updateMataKuliah("CS999", nonExistentMataKuliah);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(mataKuliahService, times(1)).update(any(MataKuliah.class));
    }

    @Test
    void testDeleteMataKuliah() {
        String kode = "CS101";
        doNothing().when(mataKuliahService).deleteByKode(kode);

        ResponseEntity<Void> response = mataKuliahController.deleteMataKuliah(kode);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(mataKuliahService, times(1)).deleteByKode(kode);
    }
}