package id.ac.ui.cs.advprog.hiringgo.matakuliah.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.service.MataKuliahService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MataKuliahControllerTest {

    @Mock
    private MataKuliahService mataKuliahService;

    @Mock
    private MataKuliahMapper mataKuliahMapper;

    @InjectMocks
    private MataKuliahController mataKuliahController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMataKuliah() {
        MataKuliah m1 = new MataKuliah("CS101","Dasar","Desc");
        MataKuliah m2 = new MataKuliah("CS102","Lanjut","Desc");
        List<MataKuliah> entities = List.of(m1, m2);

        MataKuliahDTO dto1 = new MataKuliahDTO();
        dto1.setKode("CS101"); dto1.setNama("Dasar"); dto1.setDeskripsi("Desc"); dto1.setDosenPengampuEmails(List.of());
        MataKuliahDTO dto2 = new MataKuliahDTO();
        dto2.setKode("CS102"); dto2.setNama("Lanjut"); dto2.setDeskripsi("Desc"); dto2.setDosenPengampuEmails(List.of());
        List<MataKuliahDTO> dtos = List.of(dto1, dto2);

        when(mataKuliahService.findAll()).thenReturn(CompletableFuture.completedFuture(entities));
        when(mataKuliahMapper.toDtoList(entities)).thenReturn(dtos);

        ResponseEntity<List<MataKuliahDTO>> resp = mataKuliahController.getAllMataKuliah().join();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(2, Objects.requireNonNull(resp.getBody()).size());
        assertSame(dtos, resp.getBody());

        verify(mataKuliahService).findAll();
        verify(mataKuliahMapper).toDtoList(entities);
    }

    @Test
    void testGetMataKuliahByKode_Found() {
        MataKuliah domain = new MataKuliah("CS101","Dasar","Desc");
        MataKuliahDTO dto = new MataKuliahDTO();
        dto.setKode("CS101"); dto.setNama("Dasar"); dto.setDeskripsi("Desc"); dto.setDosenPengampuEmails(List.of());

        when(mataKuliahService.findByKode("CS101")).thenReturn(domain);
        when(mataKuliahMapper.toDto(domain)).thenReturn(dto);

        ResponseEntity<MataKuliahDTO> resp = mataKuliahController.getMataKuliahByKode("CS101");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("CS101", Objects.requireNonNull(resp.getBody()).getKode());

        verify(mataKuliahService).findByKode("CS101");
        verify(mataKuliahMapper).toDto(domain);
    }

    @Test
    void testGetMataKuliahByKode_NotFound() {
        when(mataKuliahService.findByKode("CS999")).thenReturn(null);

        ResponseEntity<MataKuliahDTO> resp = mataKuliahController.getMataKuliahByKode("CS999");

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        verify(mataKuliahService).findByKode("CS999");
        verifyNoMoreInteractions(mataKuliahMapper);
    }

    @Test
    void testCreateMataKuliah_Success() {
        MataKuliahDTO dtoIn = new MataKuliahDTO();
        dtoIn.setKode("CS101"); dtoIn.setNama("Dasar"); dtoIn.setDeskripsi("Desc"); dtoIn.setDosenPengampuEmails(List.of("Dosen A"));

        MataKuliah entityIn = new MataKuliah("CS101","Dasar","Desc");
        Dosen dosenA = new Dosen("a@u.id", "pass", "A", "123");
        entityIn.addDosenPengampu(dosenA);

        when(mataKuliahMapper.toEntity(dtoIn)).thenReturn(entityIn);
        when(mataKuliahService.create(entityIn)).thenReturn(entityIn);
        when(mataKuliahMapper.toDto(entityIn)).thenReturn(dtoIn);

        ResponseEntity<MataKuliahDTO> resp = mataKuliahController.createMataKuliah(dtoIn);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals("CS101", Objects.requireNonNull(resp.getBody()).getKode());

        InOrder ord = inOrder(mataKuliahMapper, mataKuliahService);
        ord.verify(mataKuliahMapper).toEntity(dtoIn);
        ord.verify(mataKuliahService).create(entityIn);
        ord.verify(mataKuliahMapper).toDto(entityIn);
    }

    @Test
    void testCreateMataKuliah_Duplicate() {
        MataKuliahDTO dtoIn = new MataKuliahDTO();
        dtoIn.setKode("CS101"); dtoIn.setNama("Dasar"); dtoIn.setDeskripsi("Desc"); dtoIn.setDosenPengampuEmails(List.of());

        when(mataKuliahMapper.toEntity(dtoIn)).thenReturn(new MataKuliah("CS101","Dasar","Desc"));
        when(mataKuliahService.create(any())).thenThrow(new IllegalArgumentException("Kode sudah digunakan."));

        ResponseEntity<MataKuliahDTO> resp = mataKuliahController.createMataKuliah(dtoIn);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        verify(mataKuliahMapper).toEntity(dtoIn);
        verify(mataKuliahService).create(any());
        verifyNoMoreInteractions(mataKuliahMapper);
    }

    @Test
    void testUpdateMataKuliah_Success() {
        MataKuliahDTO dtoIn = new MataKuliahDTO();
        dtoIn.setKode("CS101"); dtoIn.setNama("Update"); dtoIn.setDeskripsi("New"); dtoIn.setDosenPengampuEmails(List.of());

        MataKuliah entityIn = new MataKuliah("CS101","Update","New");

        when(mataKuliahMapper.toEntity(dtoIn)).thenReturn(entityIn);
        when(mataKuliahService.update(entityIn)).thenReturn(entityIn);
        when(mataKuliahMapper.toDto(entityIn)).thenReturn(dtoIn);

        ResponseEntity<MataKuliahDTO> resp = mataKuliahController.updateMataKuliah("CS101", dtoIn);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Update", Objects.requireNonNull(resp.getBody()).getNama());

        InOrder ord = inOrder(mataKuliahMapper, mataKuliahService);
        ord.verify(mataKuliahMapper).toEntity(dtoIn);
        ord.verify(mataKuliahService).update(entityIn);
        ord.verify(mataKuliahMapper).toDto(entityIn);
    }

    @Test
    void testUpdateMataKuliah_PathMismatch() {
        MataKuliahDTO dtoIn = new MataKuliahDTO();
        dtoIn.setKode("CS999"); dtoIn.setNama("X"); dtoIn.setDeskripsi("D"); dtoIn.setDosenPengampuEmails(List.of());

        ResponseEntity<MataKuliahDTO> resp = mataKuliahController.updateMataKuliah("CS101", dtoIn);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        verifyNoInteractions(mataKuliahMapper, mataKuliahService);
    }

    @Test
    void testUpdateMataKuliah_NotFound() {
        MataKuliahDTO dtoIn = new MataKuliahDTO();
        dtoIn.setKode("CS101"); dtoIn.setNama("X"); dtoIn.setDeskripsi("D"); dtoIn.setDosenPengampuEmails(List.of());

        MataKuliah entityIn = new MataKuliah("CS101","X","D");

        when(mataKuliahMapper.toEntity(dtoIn)).thenReturn(entityIn);
        when(mataKuliahService.update(entityIn))
                .thenThrow(new IllegalArgumentException("Mata Kuliah tidak ditemukan."));

        ResponseEntity<MataKuliahDTO> resp = mataKuliahController.updateMataKuliah("CS101", dtoIn);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        InOrder ord = inOrder(mataKuliahMapper, mataKuliahService);
        ord.verify(mataKuliahMapper).toEntity(dtoIn);
        ord.verify(mataKuliahService).update(entityIn);
    }

    @Test
    void testDeleteMataKuliah() {
        doNothing().when(mataKuliahService).deleteByKode("CS101");

        ResponseEntity<Void> resp = mataKuliahController.deleteMataKuliah("CS101");

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(mataKuliahService).deleteByKode("CS101");
        verifyNoMoreInteractions(mataKuliahMapper);
    }
}
