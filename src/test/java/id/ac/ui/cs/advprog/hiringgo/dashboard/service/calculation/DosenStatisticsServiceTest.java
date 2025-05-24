package id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
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
class DosenStatisticsServiceTest {

    @Mock
    private LowonganRepository lowonganRepository;

    @InjectMocks
    private DosenStatisticsService service;

    private MataKuliah course1, course2, course3;
    private Lowongan lowongan1, lowongan2, lowongan3, lowongan4;

    @BeforeEach
    void setup() {
        // Setup courses
        course1 = new MataKuliah("CS101", "Programming", "Basic programming");
        course2 = new MataKuliah("CS102", "Data Structure", "Advanced data structures");
        course3 = new MataKuliah("CS103", "Algorithms", "Algorithm design");

        // Setup lowongan for course1
        lowongan1 = new Lowongan();
        lowongan1.setMataKuliah(course1);
        lowongan1.setJumlahAsdosDiterima(3);
        lowongan1.setJumlahAsdosDibutuhkan(5);

        lowongan2 = new Lowongan();
        lowongan2.setMataKuliah(course1);
        lowongan2.setJumlahAsdosDiterima(2);
        lowongan2.setJumlahAsdosDibutuhkan(3);

        // Setup lowongan for course2
        lowongan3 = new Lowongan();
        lowongan3.setMataKuliah(course2);
        lowongan3.setJumlahAsdosDiterima(1);
        lowongan3.setJumlahAsdosDibutuhkan(2);

        // Setup lowongan for course3
        lowongan4 = new Lowongan();
        lowongan4.setMataKuliah(course3);
        lowongan4.setJumlahAsdosDiterima(0);
        lowongan4.setJumlahAsdosDibutuhkan(4);
    }

    @Test
    void countAcceptedAssistantsPerCourse_withMultipleLowongan_shouldReturnCorrectCounts() {
        // Setup
        List<MataKuliah> courses = Arrays.asList(course1, course2, course3);

        when(lowonganRepository.findByMataKuliah(course1))
                .thenReturn(Arrays.asList(lowongan1, lowongan2));
        when(lowonganRepository.findByMataKuliah(course2))
                .thenReturn(Arrays.asList(lowongan3));
        when(lowonganRepository.findByMataKuliah(course3))
                .thenReturn(Arrays.asList(lowongan4));

        // Execute
        Map<String, Integer> result = service.countAcceptedAssistantsPerCourse(courses);

        // Verify
        assertEquals(3, result.size());
        assertEquals(5, result.get("CS101")); // 3 + 2
        assertEquals(1, result.get("CS102")); // 1
        assertEquals(0, result.get("CS103")); // 0

        verify(lowonganRepository).findByMataKuliah(course1);
        verify(lowonganRepository).findByMataKuliah(course2);
        verify(lowonganRepository).findByMataKuliah(course3);
    }

    @Test
    void countAcceptedAssistantsPerCourse_withNoLowongan_shouldReturnZeroCounts() {
        // Setup
        List<MataKuliah> courses = Arrays.asList(course1, course2);

        when(lowonganRepository.findByMataKuliah(course1))
                .thenReturn(Collections.emptyList());
        when(lowonganRepository.findByMataKuliah(course2))
                .thenReturn(Collections.emptyList());

        // Execute
        Map<String, Integer> result = service.countAcceptedAssistantsPerCourse(courses);

        // Verify
        assertEquals(2, result.size());
        assertEquals(0, result.get("CS101"));
        assertEquals(0, result.get("CS102"));

        verify(lowonganRepository).findByMataKuliah(course1);
        verify(lowonganRepository).findByMataKuliah(course2);
    }

    @Test
    void countAcceptedAssistantsPerCourse_withNullLowongan_shouldReturnZeroCounts() {
        // Setup
        List<MataKuliah> courses = Arrays.asList(course1, course2);

        when(lowonganRepository.findByMataKuliah(course1)).thenReturn(null);
        when(lowonganRepository.findByMataKuliah(course2)).thenReturn(null);

        // Execute
        Map<String, Integer> result = service.countAcceptedAssistantsPerCourse(courses);

        // Verify
        assertEquals(2, result.size());
        assertEquals(0, result.get("CS101"));
        assertEquals(0, result.get("CS102"));
    }

    @Test
    void countAcceptedAssistantsPerCourse_withRepositoryException_shouldReturnZeroForFailedCourse() {
        // Setup
        List<MataKuliah> courses = Arrays.asList(course1, course2);

        when(lowonganRepository.findByMataKuliah(course1))
                .thenThrow(new RuntimeException("Database error"));
        when(lowonganRepository.findByMataKuliah(course2))
                .thenReturn(Arrays.asList(lowongan3));

        // Execute
        Map<String, Integer> result = service.countAcceptedAssistantsPerCourse(courses);

        // Verify
        assertEquals(2, result.size());
        assertEquals(0, result.get("CS101")); // Should return 0 due to exception
        assertEquals(1, result.get("CS102")); // Should work normally
    }

    @Test
    void countAcceptedAssistantsPerCourse_withEmptyCoursesList_shouldReturnEmptyMap() {
        // Setup
        List<MataKuliah> courses = Collections.emptyList();

        // Execute
        Map<String, Integer> result = service.countAcceptedAssistantsPerCourse(courses);

        // Verify
        assertTrue(result.isEmpty());
        verify(lowonganRepository, never()).findByMataKuliah(any());
    }

    @Test
    void getTotalAcceptedAssistants_withMultipleCourses_shouldReturnCorrectSum() {
        // Setup
        List<MataKuliah> courses = Arrays.asList(course1, course2, course3);

        when(lowonganRepository.findByMataKuliah(course1))
                .thenReturn(Arrays.asList(lowongan1, lowongan2));
        when(lowonganRepository.findByMataKuliah(course2))
                .thenReturn(Arrays.asList(lowongan3));
        when(lowonganRepository.findByMataKuliah(course3))
                .thenReturn(Arrays.asList(lowongan4));

        // Execute
        int result = service.getTotalAcceptedAssistants(courses);

        // Verify - Should sum: (3+2) + 1 + 0 = 6
        assertEquals(6, result);
    }

    @Test
    void getTotalAcceptedAssistants_withNoCourses_shouldReturnZero() {
        // Setup
        List<MataKuliah> courses = Collections.emptyList();

        // Execute
        int result = service.getTotalAcceptedAssistants(courses);

        // Verify
        assertEquals(0, result);
    }

    @Test
    void getTotalOpenPositions_withMultipleCourses_shouldReturnCorrectSum() {
        // Setup
        List<MataKuliah> courses = Arrays.asList(course1, course2, course3);

        when(lowonganRepository.findByMataKuliah(course1))
                .thenReturn(Arrays.asList(lowongan1, lowongan2));
        when(lowonganRepository.findByMataKuliah(course2))
                .thenReturn(Arrays.asList(lowongan3));
        when(lowonganRepository.findByMataKuliah(course3))
                .thenReturn(Arrays.asList(lowongan4));

        // Execute
        int result = service.getTotalOpenPositions(courses);

        // Verify - Should sum: (5+3) + 2 + 4 = 14
        assertEquals(14, result);
    }

    @Test
    void getTotalOpenPositions_withNoLowongan_shouldReturnZero() {
        // Setup
        List<MataKuliah> courses = Arrays.asList(course1, course2);

        when(lowonganRepository.findByMataKuliah(course1))
                .thenReturn(Collections.emptyList());
        when(lowonganRepository.findByMataKuliah(course2))
                .thenReturn(Collections.emptyList());

        // Execute
        int result = service.getTotalOpenPositions(courses);

        // Verify
        assertEquals(0, result);
    }

    @Test
    void getTotalOpenPositions_withNullLowongan_shouldReturnZero() {
        // Setup
        List<MataKuliah> courses = Arrays.asList(course1, course2);

        when(lowonganRepository.findByMataKuliah(course1)).thenReturn(null);
        when(lowonganRepository.findByMataKuliah(course2)).thenReturn(null);

        // Execute
        int result = service.getTotalOpenPositions(courses);

        // Verify
        assertEquals(0, result);
    }

    @Test
    void getTotalOpenPositions_withRepositoryException_shouldIgnoreFailedCourses() {
        // Setup
        List<MataKuliah> courses = Arrays.asList(course1, course2);

        when(lowonganRepository.findByMataKuliah(course1))
                .thenThrow(new RuntimeException("Database error"));
        when(lowonganRepository.findByMataKuliah(course2))
                .thenReturn(Arrays.asList(lowongan3)); // Returns 2 positions

        // Execute
        int result = service.getTotalOpenPositions(courses);

        // Verify - Should only count course2's positions
        assertEquals(2, result);
    }

    @Test
    void getTotalOpenPositions_withEmptyCoursesList_shouldReturnZero() {
        // Setup
        List<MataKuliah> courses = Collections.emptyList();

        // Execute
        int result = service.getTotalOpenPositions(courses);

        // Verify
        assertEquals(0, result);
        verify(lowonganRepository, never()).findByMataKuliah(any());
    }

    @Test
    void allMethods_consistencyTest() {
        // Setup
        List<MataKuliah> courses = Arrays.asList(course1, course2);

        when(lowonganRepository.findByMataKuliah(course1))
                .thenReturn(Arrays.asList(lowongan1, lowongan2));
        when(lowonganRepository.findByMataKuliah(course2))
                .thenReturn(Arrays.asList(lowongan3));

        // Execute
        Map<String, Integer> perCourseCount = service.countAcceptedAssistantsPerCourse(courses);
        int totalAccepted = service.getTotalAcceptedAssistants(courses);
        int totalPositions = service.getTotalOpenPositions(courses);

        // Verify consistency
        int sumFromMap = perCourseCount.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(sumFromMap, totalAccepted, "Total should equal sum of individual counts");
        assertTrue(totalPositions >= totalAccepted, "Total positions should be >= accepted assistants");

        // Specific values: accepted=(3+2)+1=6, positions=(5+3)+2=10
        assertEquals(6, totalAccepted);
        assertEquals(10, totalPositions);
    }

    @Test
    void countAcceptedAssistantsPerCourse_withLargeCourseList_shouldHandleEfficiently() {
        // Setup - Create many courses
        List<MataKuliah> manyCourses = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            MataKuliah course = new MataKuliah("CS" + String.format("%03d", i), "Course " + i, "Description " + i);
            manyCourses.add(course);

            // Mock repository to return empty list for each course
            when(lowonganRepository.findByMataKuliah(course))
                    .thenReturn(Collections.emptyList());
        }

        long startTime = System.currentTimeMillis();

        // Execute
        Map<String, Integer> result = service.countAcceptedAssistantsPerCourse(manyCourses);

        long duration = System.currentTimeMillis() - startTime;

        // Verify
        assertEquals(100, result.size());
        assertTrue(result.values().stream().allMatch(count -> count == 0));
        assertTrue(duration < 1000, "Should handle large course list efficiently");

        // Verify each course was queried exactly once
        for (MataKuliah course : manyCourses) {
            verify(lowonganRepository, times(1)).findByMataKuliah(course);
        }
    }
}