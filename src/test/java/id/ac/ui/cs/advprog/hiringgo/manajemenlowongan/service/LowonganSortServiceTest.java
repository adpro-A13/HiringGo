package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.sort.SortByJumlahAsdosDibutuhkan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.sort.SortByJumlahAsdosDiterima;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.sort.SortByJumlahAsdosPendaftar;
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
    void testSortByJumlahAsdosDibutuhkan() {
        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");

        Lowongan lowongan1 = createLowongan(UUID.randomUUID(), mataKuliah, 5, 0); // jumlah dibutuhkan = 5
        Lowongan lowongan2 = createLowongan(UUID.randomUUID(), mataKuliah, 7, 0); // jumlah dibutuhkan = 7

        List<Lowongan> unsortedList = List.of(lowongan1, lowongan2);
        List<Lowongan> expectedSorted = List.of(lowongan1, lowongan2); // ascending default

        SortByJumlahAsdosDibutuhkan sorter = new SortByJumlahAsdosDibutuhkan();
        List<Lowongan> result = sorter.sort(unsortedList);

        assertEquals(expectedSorted, result);
    }


    @Test
    void testSortByJumlahAsdosDiterima() {
        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");

        Lowongan lowongan1 = createLowongan(UUID.randomUUID(), mataKuliah, 5, 0);
        lowongan1.setJumlahAsdosDiterima(2);

        Lowongan lowongan2 = createLowongan(UUID.randomUUID(), mataKuliah, 7, 0);
        lowongan2.setJumlahAsdosDiterima(5);

        List<Lowongan> unsortedList = List.of(lowongan1, lowongan2);
        List<Lowongan> expectedSorted = List.of(lowongan1, lowongan2); // ascending default

        SortByJumlahAsdosDiterima sorter = new SortByJumlahAsdosDiterima();
        List<Lowongan> result = sorter.sort(unsortedList);

        assertEquals(expectedSorted, result);
    }


    @Test
    void testSortByJumlahAsdosPendaftar() {
        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");

        Lowongan lowongan1 = createLowongan(UUID.randomUUID(), mataKuliah, 5, 0);
        lowongan1.setJumlahAsdosPendaftar(10);

        Lowongan lowongan2 = createLowongan(UUID.randomUUID(), mataKuliah, 7, 0);
        lowongan2.setJumlahAsdosPendaftar(3);

        List<Lowongan> unsorted = List.of(lowongan1, lowongan2);
        List<Lowongan> expectedSorted = List.of(lowongan2, lowongan1);

        SortByJumlahAsdosPendaftar sorter = new SortByJumlahAsdosPendaftar();
        List<Lowongan> result = sorter.sort(unsorted);

        assertEquals(expectedSorted, result);
    }

    private Lowongan createLowongan(UUID lowonganId, MataKuliah mataKuliah, int dibutuhkan, int diterima) {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setJumlahAsdosDibutuhkan(dibutuhkan);
        lowongan.setJumlahAsdosDiterima(diterima);
        return lowongan;
    }

    // Add these tests to the existing LowonganSortServiceTest.java file

    @Test
    void testSortService_withValidStrategy_returnsSortedList() {
        // Given
        LowonganSortService sortService = new LowonganSortService(List.of(
                new SortByJumlahAsdosDibutuhkan(),
                new SortByJumlahAsdosDiterima(),
                new SortByJumlahAsdosPendaftar()
        ));

        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");
        Lowongan l1 = createLowongan(UUID.randomUUID(), mataKuliah, 7, 0);
        Lowongan l2 = createLowongan(UUID.randomUUID(), mataKuliah, 3, 0);

        List<Lowongan> unsorted = List.of(l1, l2);

        // When
        List<Lowongan> result = sortService.sort(unsorted, "SortByJumlahAsdosDibutuhkan");

        // Then - should be sorted by jumlah dibutuhkan (3, 7)
        assertEquals(2, result.size());
        assertEquals(l2, result.get(0)); // 3 comes first
        assertEquals(l1, result.get(1)); // 7 comes second
    }

    @Test
    void testSortService_withInvalidStrategy_returnsOriginalList() {
        // Given
        LowonganSortService sortService = new LowonganSortService(List.of(
                new SortByJumlahAsdosDibutuhkan()
        ));

        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");
        Lowongan l1 = createLowongan(UUID.randomUUID(), mataKuliah, 7, 0);
        Lowongan l2 = createLowongan(UUID.randomUUID(), mataKuliah, 3, 0);

        List<Lowongan> originalList = List.of(l1, l2);

        // When - using non-existent strategy name
        List<Lowongan> result = sortService.sort(originalList, "NonExistentStrategy");

        // Then - should return original list unchanged
        assertEquals(2, result.size());
        assertEquals(originalList, result);
    }

    @Test
    void testSortService_withNullStrategy_returnsOriginalList() {
        // Given
        LowonganSortService sortService = new LowonganSortService(List.of(
                new SortByJumlahAsdosDibutuhkan()
        ));

        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");
        Lowongan l1 = createLowongan(UUID.randomUUID(), mataKuliah, 7, 0);
        Lowongan l2 = createLowongan(UUID.randomUUID(), mataKuliah, 3, 0);

        List<Lowongan> originalList = List.of(l1, l2);

        List<Lowongan> result = sortService.sort(originalList, null);

        assertEquals(2, result.size());
        assertEquals(originalList, result);
    }

    @Test
    void testSortService_withEmptyList_returnsEmptyList() {
        LowonganSortService sortService = new LowonganSortService(List.of(
                new SortByJumlahAsdosDibutuhkan()
        ));

        List<Lowongan> emptyList = List.of();

        List<Lowongan> result = sortService.sort(emptyList, "SortByJumlahAsdosDibutuhkan");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSortService_withEmptyStrategyList_returnsOriginalList() {
        LowonganSortService sortService = new LowonganSortService(List.of());

        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");
        Lowongan l1 = createLowongan(UUID.randomUUID(), mataKuliah, 7, 0);

        List<Lowongan> originalList = List.of(l1);

        List<Lowongan> result = sortService.sort(originalList, "AnyStrategy");

        assertEquals(originalList, result);
    }

    @Test
    void testSortService_constructor_initializesStrategyMapCorrectly() {
        SortByJumlahAsdosDibutuhkan dibutuhkanSorter = new SortByJumlahAsdosDibutuhkan();
        SortByJumlahAsdosDiterima diterimaSorter = new SortByJumlahAsdosDiterima();

        LowonganSortService sortService = new LowonganSortService(List.of(dibutuhkanSorter, diterimaSorter));

        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");
        Lowongan testLowongan = createLowongan(UUID.randomUUID(), mataKuliah, 5, 2);
        List<Lowongan> testList = List.of(testLowongan);

        List<Lowongan> dibutuhkanResult = sortService.sort(testList, "SortByJumlahAsdosDibutuhkan");
        List<Lowongan> diterimaResult = sortService.sort(testList, "SortByJumlahAsdosDiterima");

        assertNotNull(dibutuhkanResult);
        assertNotNull(diterimaResult);
    }

    @Test
    void testSortService_multipleSortStrategies() {
        LowonganSortService sortService = new LowonganSortService(List.of(
                new SortByJumlahAsdosDibutuhkan(),
                new SortByJumlahAsdosDiterima(),
                new SortByJumlahAsdosPendaftar()
        ));

        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");

        Lowongan l1 = createLowongan(UUID.randomUUID(), mataKuliah, 5, 2);
        l1.setJumlahAsdosPendaftar(10);

        Lowongan l2 = createLowongan(UUID.randomUUID(), mataKuliah, 3, 1);
        l2.setJumlahAsdosPendaftar(5);

        List<Lowongan> unsorted = List.of(l1, l2);

        List<Lowongan> sortedByDibutuhkan = sortService.sort(unsorted, "SortByJumlahAsdosDibutuhkan");
        List<Lowongan> sortedByDiterima = sortService.sort(unsorted, "SortByJumlahAsdosDiterima");
        List<Lowongan> sortedByPendaftar = sortService.sort(unsorted, "SortByJumlahAsdosPendaftar");

        assertEquals(l2, sortedByDibutuhkan.get(0));
        assertEquals(l2, sortedByDiterima.get(0));
        assertEquals(l2, sortedByPendaftar.get(0));
    }

    @Test
    void testSortService_singleItemList() {
        LowonganSortService sortService = new LowonganSortService(List.of(
                new SortByJumlahAsdosDibutuhkan()
        ));

        MataKuliah mataKuliah = new MataKuliah("CS1234", "advprog", "advanced programming");
        Lowongan singleLowongan = createLowongan(UUID.randomUUID(), mataKuliah, 5, 2);
        List<Lowongan> singleItemList = List.of(singleLowongan);

        List<Lowongan> result = sortService.sort(singleItemList, "SortByJumlahAsdosDibutuhkan");

        assertEquals(1, result.size());
        assertEquals(singleLowongan, result.get(0));
    }
}
