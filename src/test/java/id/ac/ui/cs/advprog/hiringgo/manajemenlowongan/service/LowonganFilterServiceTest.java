package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterBySemester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterByStatus;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LowonganFilterServiceTest {

    private final FilterByStatus filterByStatus = new FilterByStatus();

    private final FilterBySemester filterBySemester = new FilterBySemester();

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
}
