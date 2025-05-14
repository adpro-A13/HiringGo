package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterBySemester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterByStatus;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganServiceImpl;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import jakarta.persistence.EntityNotFoundException;
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

    @Mock
    private PendaftaranRepository pendaftaranRepository;
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
    void testCreateLowonganWhenLowonganDoesNotExist() {
        Lowongan newLowongan = new Lowongan();
        MataKuliah mataKuliah = new MataKuliah("CS100", "Advpro", "advanced programming");
        newLowongan.setMataKuliah(mataKuliah);
        newLowongan.setSemester("GANJIL");
        newLowongan.setTahunAjaran("2023");

        // Mocking the repository method
        when(lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getMataKuliah(),
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
        verify(lowonganRepository).findByMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran());
        verify(lowonganRepository).save(newLowongan);
    }

    @Test
    void testCreateLowonganWhenLowonganAlreadyExists() {
        Lowongan newLowongan = new Lowongan();
        MataKuliah mataKuliah = new MataKuliah("CS100", "Advpro", "advanced programming");
        newLowongan.setMataKuliah(mataKuliah);
        newLowongan.setSemester("GANJIL");
        newLowongan.setTahunAjaran("2023");

        Lowongan existingLowongan = new Lowongan();
        when(lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran())
        ).thenReturn(Optional.of(existingLowongan));

        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> {
            lowonganService.createLowongan(newLowongan);
        });

        verify(lowonganRepository).findByMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran());
        verify(lowonganRepository, times(0)).save(any(Lowongan.class)); // Ensure save was not called
    }
    @Test
    void testFindAllReturnsList() {
        List<Lowongan> dummyList = List.of(new Lowongan(), new Lowongan());
        when(lowonganRepository.findAll()).thenReturn(dummyList);

        List<Lowongan> result = lowonganService.findAll();

        assertEquals(dummyList, result);
    }


    @Test
    void testUpdateLowonganSuccess() {
        UUID id = UUID.randomUUID();

        Lowongan existingLowongan = new Lowongan();
        existingLowongan.setLowonganId(id);
        MataKuliah mataKuliah = new MataKuliah("CS100", "Advpro", "advanced programming");
        existingLowongan.setMataKuliah(mataKuliah);
        existingLowongan.setTahunAjaran("2024");
        existingLowongan.setSemester(String.valueOf(Semester.GANJIL));
        existingLowongan.setStatusLowongan(StatusLowongan.DIBUKA.getValue());
        existingLowongan.setJumlahAsdosDibutuhkan(5);
        existingLowongan.setJumlahAsdosDiterima(0);
        existingLowongan.setJumlahAsdosPendaftar(10);

        Lowongan updatedLowongan = new Lowongan();
        MataKuliah mataKuliah2 = new MataKuliah("CS102", "Sister", "sistem interaksi");
        updatedLowongan.setMataKuliah(mataKuliah2);
        updatedLowongan.setTahunAjaran("2025");
        updatedLowongan.setSemester(String.valueOf(Semester.GENAP));
        updatedLowongan.setStatusLowongan(StatusLowongan.DITUTUP.getValue());
        updatedLowongan.setJumlahAsdosDibutuhkan(8);
        updatedLowongan.setJumlahAsdosDiterima(4);
        updatedLowongan.setJumlahAsdosPendaftar(15);

        when(lowonganRepository.findById(id)).thenReturn(Optional.of(existingLowongan));
        when(lowonganRepository.save(any(Lowongan.class))).thenAnswer(i -> i.getArguments()[0]);

        Lowongan result = lowonganService.updateLowongan(id, updatedLowongan);

        assertEquals("CS102", result.getMataKuliah().getKode());
        assertEquals("2025", result.getTahunAjaran());
        assertEquals(Semester.GENAP, result.getSemester());
        assertEquals(StatusLowongan.DITUTUP, result.getStatusLowongan());
        assertEquals(8, result.getJumlahAsdosDibutuhkan());
        assertEquals(4, result.getJumlahAsdosDiterima());
        assertEquals(15, result.getJumlahAsdosPendaftar());

        verify(lowonganRepository).findById(id);
        verify(lowonganRepository).save(existingLowongan);
    }

    @Test
    void testUpdateLowonganFail() {
        UUID id = UUID.randomUUID();
        Lowongan updatedLowongan = new Lowongan();
        MataKuliah mataKuliah = new MataKuliah("CS100", "Advpro", "advanced programming");
        updatedLowongan.setMataKuliah(mataKuliah);
        updatedLowongan.setTahunAjaran("2025/2026");
        updatedLowongan.setSemester(String.valueOf(Semester.GENAP));
        updatedLowongan.setStatusLowongan(StatusLowongan.DITUTUP.getValue());
        updatedLowongan.setJumlahAsdosDibutuhkan(8);
        updatedLowongan.setJumlahAsdosDiterima(4);
        updatedLowongan.setJumlahAsdosPendaftar(15);

        when(lowonganRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.updateLowongan(id, updatedLowongan);
        });

        verify(lowonganRepository).findById(id);
        verify(lowonganRepository, never()).save(any());
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

    @Test
    void testTerimaPendaftarThrowsIfPendaftaranNotFound() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.terimaPendaftar(lowonganId, pendaftaranId);
        });
    }

    @Test
    void testTolakPendaftarThrowsIfNotFound() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        when(pendaftaranRepository.existsById(pendaftaranId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.tolakPendaftar(lowonganId, pendaftaranId);
        });
    }

}
