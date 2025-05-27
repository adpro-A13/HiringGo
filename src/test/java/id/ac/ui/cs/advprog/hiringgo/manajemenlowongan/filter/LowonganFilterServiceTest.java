package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterBySemester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterByStatus;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LowonganFilterServiceTest {

    private final FilterByStatus filterByStatus = new FilterByStatus();
    private final FilterBySemester filterBySemester = new FilterBySemester();
    private LowonganFilterService lowonganFilterService;

    @BeforeEach
    void setUp() {
        lowonganFilterService = new LowonganFilterService(List.of(filterByStatus, filterBySemester));
    }

    private Lowongan createLowongan(UUID id, Semester semester) {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(id);
        lowongan.setSemester(String.valueOf(semester));
        return lowongan;
    }

    private Lowongan createLowongan(UUID id, StatusLowongan status) {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(id);
        lowongan.setStatusLowongan(String.valueOf(status));
        return lowongan;
    }

    // Tests for individual filter strategies
    @Test
    void testFilterBySemesterGenap() {
        Lowongan l1 = createLowongan(UUID.randomUUID(), Semester.GENAP);
        Lowongan l2 = createLowongan(UUID.randomUUID(), Semester.GANJIL);
        Lowongan l3 = createLowongan(UUID.randomUUID(), Semester.GENAP);

        List<Lowongan> input = List.of(l1, l2, l3);
        List<Lowongan> result = filterBySemester.filter(input, "GENAP");

        assertEquals(2, result.size());
        assertTrue(result.contains(l1));
        assertTrue(result.contains(l3));
        assertFalse(result.contains(l2));
    }

    @Test
    void testFilterBySemesterGanjil() {
        Lowongan l1 = createLowongan(UUID.randomUUID(), Semester.GENAP);
        Lowongan l2 = createLowongan(UUID.randomUUID(), Semester.GANJIL);
        Lowongan l3 = createLowongan(UUID.randomUUID(), Semester.GANJIL);

        List<Lowongan> input = List.of(l1, l2, l3);
        List<Lowongan> result = filterBySemester.filter(input, "GANJIL");

        assertEquals(2, result.size());
        assertTrue(result.contains(l2));
        assertTrue(result.contains(l3));
        assertFalse(result.contains(l1));
    }

    @Test
    void testFilterWithInvalidSemesterReturnsOriginalList() {
        Lowongan l1 = createLowongan(UUID.randomUUID(), Semester.GENAP);
        Lowongan l2 = createLowongan(UUID.randomUUID(), Semester.GANJIL);

        List<Lowongan> input = List.of(l1, l2);
        List<Lowongan> result = filterBySemester.filter(input, "INVALID");

        assertEquals(input.size(), result.size());
        assertEquals(input, result);
    }

    @Test
    void testFilterByStatusDibuka() {
        Lowongan l1 = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        Lowongan l2 = createLowongan(UUID.randomUUID(), StatusLowongan.DITUTUP);
        Lowongan l3 = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);

        List<Lowongan> lowongans = List.of(l1, l2, l3);
        List<Lowongan> result = filterByStatus.filter(lowongans, "DIBUKA");

        assertEquals(2, result.size());
        assertTrue(result.contains(l1));
        assertTrue(result.contains(l3));
        assertFalse(result.contains(l2));
    }

    @Test
    void testFilterByStatusDitutup() {
        Lowongan l1 = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        Lowongan l2 = createLowongan(UUID.randomUUID(), StatusLowongan.DITUTUP);
        Lowongan l3 = createLowongan(UUID.randomUUID(), StatusLowongan.DITUTUP);

        List<Lowongan> lowongans = List.of(l1, l2, l3);
        List<Lowongan> result = filterByStatus.filter(lowongans, "DITUTUP");

        assertEquals(2, result.size());
        assertTrue(result.contains(l2));
        assertTrue(result.contains(l3));
        assertFalse(result.contains(l1));
    }

    @Test
    void testFilterWithInvalidStatusReturnsOriginalList() {
        Lowongan l1 = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        Lowongan l2 = createLowongan(UUID.randomUUID(), StatusLowongan.DITUTUP);

        List<Lowongan> lowongans = List.of(l1, l2);
        List<Lowongan> result = filterByStatus.filter(lowongans, "TIDAK_VALID");

        assertEquals(2, result.size());
        assertEquals(lowongans, result);
    }

    @Test
    void testGetStrategyNameReturnsCorrectName() {
        assertEquals("FilterByStatus", filterByStatus.getStrategyName());
    }

    // Tests for LowonganFilterService.filter() method
    @Test
    void testFilterService_withValidStatusStrategy_returnsFilteredList() {
        // Given
        Lowongan l1 = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        Lowongan l2 = createLowongan(UUID.randomUUID(), StatusLowongan.DITUTUP);
        Lowongan l3 = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);

        List<Lowongan> lowongans = List.of(l1, l2, l3);

        // When
        List<Lowongan> result = lowonganFilterService.filter(lowongans, "FilterByStatus", "DIBUKA");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(l1));
        assertTrue(result.contains(l3));
        assertFalse(result.contains(l2));
    }

    @Test
    void testFilterService_withValidSemesterStrategy_returnsFilteredList() {
        // Given
        Lowongan l1 = createLowongan(UUID.randomUUID(), Semester.GENAP);
        Lowongan l2 = createLowongan(UUID.randomUUID(), Semester.GANJIL);
        Lowongan l3 = createLowongan(UUID.randomUUID(), Semester.GENAP);

        List<Lowongan> lowongans = List.of(l1, l2, l3);

        // When
        List<Lowongan> result = lowonganFilterService.filter(lowongans, "FilterBySemester", "GENAP");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(l1));
        assertTrue(result.contains(l3));
        assertFalse(result.contains(l2));
    }

    @Test
    void testFilterService_withInvalidStrategy_returnsOriginalList() {
        // Given
        Lowongan l1 = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        Lowongan l2 = createLowongan(UUID.randomUUID(), StatusLowongan.DITUTUP);

        List<Lowongan> lowongans = List.of(l1, l2);

        // When - using non-existent strategy name
        List<Lowongan> result = lowonganFilterService.filter(lowongans, "NonExistentStrategy", "DIBUKA");

        // Then - should return original list unchanged
        assertEquals(2, result.size());
        assertEquals(lowongans, result);
    }

    @Test
    void testFilterService_withNullStrategy_returnsOriginalList() {
        // Given
        Lowongan l1 = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        Lowongan l2 = createLowongan(UUID.randomUUID(), StatusLowongan.DITUTUP);

        List<Lowongan> lowongans = List.of(l1, l2);

        // When - using null strategy name
        List<Lowongan> result = lowonganFilterService.filter(lowongans, null, "DIBUKA");

        // Then - should return original list unchanged
        assertEquals(2, result.size());
        assertEquals(lowongans, result);
    }

    @Test
    void testFilterService_withEmptyList_returnsEmptyList() {
        // Given
        List<Lowongan> emptyList = List.of();

        // When
        List<Lowongan> result = lowonganFilterService.filter(emptyList, "FilterByStatus", "DIBUKA");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterService_withEmptyStrategyList_alwaysReturnsOriginalList() {
        // Given - service with no strategies
        LowonganFilterService filterService = new LowonganFilterService(List.of());

        Lowongan l1 = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        List<Lowongan> lowongans = List.of(l1);

        // When
        List<Lowongan> result = filterService.filter(lowongans, "AnyStrategy", "AnyValue");

        // Then - should return original list since no strategies are registered
        assertEquals(lowongans, result);
    }

    @Test
    void testGetStrategyNameForSemester_returnsCorrectName() {
        assertEquals("FilterBySemester", filterBySemester.getStrategyName());
    }

    @Test
    void testFilterService_constructor_initializesCorrectly() {
        // When
        LowonganFilterService filterService = new LowonganFilterService(List.of(filterByStatus, filterBySemester));

        // Then - verify service can use both strategies
        assertNotNull(filterService);

        Lowongan testLowongan = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        List<Lowongan> testList = List.of(testLowongan);

        // Both strategies should be accessible
        List<Lowongan> statusResult = filterService.filter(testList, "FilterByStatus", "DIBUKA");
        List<Lowongan> semesterResult = filterService.filter(testList, "FilterBySemester", "GENAP");

        assertNotNull(statusResult);
        assertNotNull(semesterResult);
    }

    @Test
    void testFilterService_chainedFiltering_worksCorrectly() {
        // Given
        Lowongan l1 = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        l1.setSemester("GENAP");

        Lowongan l2 = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        l2.setSemester("GANJIL");

        Lowongan l3 = createLowongan(UUID.randomUUID(), StatusLowongan.DITUTUP);
        l3.setSemester("GENAP");

        List<Lowongan> lowongans = List.of(l1, l2, l3);

        // When - first filter by status, then by semester
        List<Lowongan> statusFiltered = lowonganFilterService.filter(lowongans, "FilterByStatus", "DIBUKA");
        List<Lowongan> finalResult = lowonganFilterService.filter(statusFiltered, "FilterBySemester", "GENAP");

        // Then - should only contain l1 (DIBUKA and GENAP)
        assertEquals(1, finalResult.size());
        assertTrue(finalResult.contains(l1));
    }

    @Test
    void testFilterService_singleItemList_filtersCorrectly() {
        // Given
        Lowongan singleLowongan = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        List<Lowongan> singleItemList = List.of(singleLowongan);

        // When - filtering with matching criteria
        List<Lowongan> result = lowonganFilterService.filter(singleItemList, "FilterByStatus", "DIBUKA");

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(singleLowongan));
    }

    @Test
    void testFilterService_singleItemList_filtersOutCorrectly() {
        // Given
        Lowongan singleLowongan = createLowongan(UUID.randomUUID(), StatusLowongan.DIBUKA);
        List<Lowongan> singleItemList = List.of(singleLowongan);

        // When - filtering with non-matching criteria
        List<Lowongan> result = lowonganFilterService.filter(singleItemList, "FilterByStatus", "DITUTUP");

        // Then
        assertTrue(result.isEmpty());
    }
}