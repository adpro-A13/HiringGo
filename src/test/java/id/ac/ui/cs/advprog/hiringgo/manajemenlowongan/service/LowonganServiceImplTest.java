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


}
