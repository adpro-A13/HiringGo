package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterBySemester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterByStatus;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LowonganServiceImplTest {

    @Mock
    private LowonganRepository lowonganRepository;

    @InjectMocks
    private LowonganServiceImpl lowonganService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByIdReturnsLowongan() {
        UUID id = UUID.randomUUID();
        Lowongan dummy = new Lowongan();
        dummy.setLowonganId(id);
        when(lowonganRepository.findById(id)).thenReturn(java.util.Optional.of(dummy));

        Lowongan result = lowonganService.findById(id);

        assertEquals(dummy, result);
        verify(lowonganRepository).findById(id);
    }

    @Test
    void testFindAllReturnsList() {
        List<Lowongan> dummyList = List.of(new Lowongan(), new Lowongan());
        when(lowonganRepository.findAll()).thenReturn(dummyList);

        List<Lowongan> result = lowonganService.findAll();

        assertEquals(dummyList, result);
    }

    @Test
    void testCreateLowonganWhenLowonganDoesNotExist() {
        Lowongan newLowongan = new Lowongan();
        newLowongan.setIdMataKuliah("CS101");
        newLowongan.setSemester(Semester.GANJIL.getValue());
        newLowongan.setTahunAjaran("2023");

        // Mocking the repository method
        when(lowonganRepository.findByIdMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getIdMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran())
        ).thenReturn(Optional.empty());

        when(lowonganRepository.save(any(Lowongan.class))).thenReturn(newLowongan);

        // Call the method to test
        Lowongan createdLowongan = lowonganService.createLowongan(newLowongan);

        // Validate the results
        assertNotNull(createdLowongan);
        assertEquals(0, createdLowongan.getJumlahAsdosDiterima());
        assertEquals(0, createdLowongan.getJumlahAsdosPendaftar());

        // Verify interactions with the repository
        verify(lowonganRepository).findByIdMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getIdMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran());
        verify(lowonganRepository).save(newLowongan);
    }

    @Test
    void testCreateLowonganWhenLowonganAlreadyExists() {
        Lowongan newLowongan = new Lowongan();
        newLowongan.setIdMataKuliah("CS101");
        newLowongan.setSemester(Semester.GANJIL.getValue());
        newLowongan.setTahunAjaran("2023");

        // Mocking the repository to return an existing lowongan
        Lowongan existingLowongan = new Lowongan();
        when(lowonganRepository.findByIdMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getIdMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran())
        ).thenReturn(Optional.of(existingLowongan));

        // Try to create a lowongan when one already exists, expect an exception
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> {
            lowonganService.createLowongan(newLowongan);
        });

        // Verify that findByIdMataKuliahAndSemesterAndTahunAjaran was called
        verify(lowonganRepository).findByIdMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getIdMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran());
        verify(lowonganRepository, times(0)).save(any(Lowongan.class)); // Ensure save was not called
    }

    @Test
    void testFilterByStatusLowongan() {
        Lowongan aktif = new Lowongan();
        aktif.setStatusLowongan(StatusLowongan.DIBUKA.getValue());

        Lowongan tidakAktif = new Lowongan();
        tidakAktif.setStatusLowongan(StatusLowongan.DITUTUP.getValue());

        when(lowonganRepository.findAll()).thenReturn(List.of(aktif, tidakAktif));

        var strategy = new FilterByStatus(StatusLowongan.DIBUKA);
        List<Lowongan> result = lowonganService.filterLowongan(strategy);

        assertEquals(1, result.size());
        assertEquals(StatusLowongan.DIBUKA, result.get(0).getStatusLowongan());
    }

    @Test
    void testFilterBySemester() {
        Lowongan genap = new Lowongan();
        genap.setSemester(Semester.GENAP.getValue());

        Lowongan ganjil = new Lowongan();
        ganjil.setSemester(Semester.GANJIL.getValue());

        when(lowonganRepository.findAll()).thenReturn(List.of(genap, ganjil));

        var strategy = new FilterBySemester(Semester.GANJIL);
        List<Lowongan> result = lowonganService.filterLowongan(strategy);

        assertEquals(1, result.size());
        assertEquals(Semester.GANJIL, result.get(0).getSemester());
    }

    @Test
    void testDeleteLowonganByIdSuccess() {
        UUID id = UUID.randomUUID();

        // Perhatikan ini: kita mock existsById, bukan findById
        when(lowonganRepository.existsById(id)).thenReturn(true);

        lowonganService.deleteLowonganById(id);

        verify(lowonganRepository).existsById(id);
        verify(lowonganRepository).deleteById(id);
    }



    @Test
    void testDeleteLowonganByIdThrowsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(lowonganRepository.existsById(id)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lowonganService.deleteLowonganById(id);
        });

        assertEquals("Lowongan tidak ditemukan", exception.getMessage());
        verify(lowonganRepository).existsById(id);
        verify(lowonganRepository, never()).deleteById(any());
    }


}
