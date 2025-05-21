package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LowonganSortServiceTest {

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private LowonganSortService sortService;

    @InjectMocks
    private LowonganServiceImpl lowonganService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSortedLowonganReturnsSortedList() {
        // Dummy data sebelum disort
        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");
        Lowongan lowongan1 = createLowongan(UUID.randomUUID(), mataKuliah, 5, 0);
        lowongan1.setJumlahAsdosPendaftar(0);
        Lowongan lowongan2 = createLowongan(UUID.randomUUID(), mataKuliah, 7, 0);
        lowongan1.setJumlahAsdosPendaftar(0);

        List<Lowongan> unsortedList = List.of(lowongan1, lowongan2);
        List<Lowongan> sortedList = List.of(lowongan2, lowongan1);

        String sortKey = "SortByJumlahAsdosDibutuhkan";

        when(lowonganRepository.findAll()).thenReturn(unsortedList);
        when(sortService.sort(unsortedList, sortKey)).thenReturn(sortedList);

        List<Lowongan> result = lowonganService.getSortedLowongan(sortKey);

        assertNotNull(result);
        assertEquals(sortedList.size(), result.size());
        assertEquals(sortedList, result);

        verify(lowonganRepository).findAll();
        verify(sortService).sort(unsortedList, sortKey);
    }

    @Test
    void testGetSortedLowonganSortByJumlahAsdosDiterima() {
        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");
        Lowongan lowongan1 = createLowongan(UUID.randomUUID(), mataKuliah, 5, 0);
        lowongan1.setJumlahAsdosDiterima(2);
        Lowongan lowongan2 = createLowongan(UUID.randomUUID(), mataKuliah, 7, 0);
        lowongan2.setJumlahAsdosDiterima(5);

        List<Lowongan> unsortedList = List.of(lowongan1, lowongan2);
        List<Lowongan> sortedList = List.of(lowongan2, lowongan1);

        String sortKey = "SortByJumlahAsdosDiterima";

        when(lowonganRepository.findAll()).thenReturn(unsortedList);
        when(sortService.sort(unsortedList, sortKey)).thenReturn(sortedList);

        List<Lowongan> result = lowonganService.getSortedLowongan(sortKey);

        assertNotNull(result);
        assertEquals(sortedList.size(), result.size());
        assertEquals(sortedList, result);

        verify(lowonganRepository).findAll();
        verify(sortService).sort(unsortedList, sortKey);
    }

    @Test
    void testGetSortedLowonganSortByJumlahAsdosPendaftar() {
        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");
        Lowongan lowongan1 = createLowongan(UUID.randomUUID(), mataKuliah, 5, 0);
        lowongan1.setJumlahAsdosPendaftar(3);
        Lowongan lowongan2 = createLowongan(UUID.randomUUID(), mataKuliah, 7, 0);
        lowongan2.setJumlahAsdosPendaftar(10);

        List<Lowongan> unsortedList = List.of(lowongan1, lowongan2);
        List<Lowongan> sortedList = List.of(lowongan2, lowongan1);

        String sortKey = "SortByJumlahAsdosPendaftar";

        when(lowonganRepository.findAll()).thenReturn(unsortedList);
        when(sortService.sort(unsortedList, sortKey)).thenReturn(sortedList);

        List<Lowongan> result = lowonganService.getSortedLowongan(sortKey);

        assertNotNull(result);
        assertEquals(sortedList.size(), result.size());
        assertEquals(sortedList, result);

        verify(lowonganRepository).findAll();
        verify(sortService).sort(unsortedList, sortKey);
    }


    @Test
    void testGetSortedLowonganReturnsEmptyListWhenNoLowongan() {
        when(lowonganRepository.findAll()).thenReturn(List.of());
        when(sortService.sort(List.of(), "anyKey")).thenReturn(List.of());

        List<Lowongan> result = lowonganService.getSortedLowongan("anyKey");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(lowonganRepository).findAll();
        verify(sortService).sort(List.of(), "anyKey");
    }

    private Lowongan createLowongan(UUID lowonganId, MataKuliah mataKuliah, int dibutuhkan, int diterima) {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setJumlahAsdosDibutuhkan(dibutuhkan);
        lowongan.setJumlahAsdosDiterima(diterima);
        return lowongan;
    }
}
