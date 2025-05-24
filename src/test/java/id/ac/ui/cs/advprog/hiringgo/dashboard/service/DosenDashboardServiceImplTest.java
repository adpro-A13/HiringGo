package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DosenDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation.DosenStatisticsService;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.data.DosenCourseDataService;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature.DosenFeatureProvider;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.validation.DosenValidationService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DosenDashboardServiceImplTest {

    @Mock
    private DosenValidationService validationService;

    @Mock
    private DosenCourseDataService courseDataService;

    @Mock
    private DosenStatisticsService statisticsService;

    @Mock
    private DosenFeatureProvider featureProvider;

    @InjectMocks
    private DosenDashboardServiceImpl service;

    private UUID userId;
    private Dosen dosenMock;
    private MataKuliah mataKuliah1;
    private MataKuliah mataKuliah2;
    private MataKuliahDTO mataKuliahDTO1;
    private MataKuliahDTO mataKuliahDTO2;
    private LowonganDTO lowonganDTO1;
    private LowonganDTO lowonganDTO2;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        dosenMock = mock(Dosen.class);

        // Setup course data
        mataKuliah1 = new MataKuliah("CS101", "Advanced Programming", "Object-oriented programming");
        mataKuliah2 = new MataKuliah("CS102", "Data Structures", "Collection of data structures");

        // Setup DTOs
        mataKuliahDTO1 = mock(MataKuliahDTO.class);
        mataKuliahDTO2 = mock(MataKuliahDTO.class);
        lowonganDTO1 = mock(LowonganDTO.class);
        lowonganDTO2 = mock(LowonganDTO.class);

        // Use lenient() for common stubbing that might not always be used
        lenient().when(dosenMock.getUsername()).thenReturn("dosen@example.com");
        lenient().when(dosenMock.getFullName()).thenReturn("Dr. Dosen");
        lenient().when(featureProvider.getAvailableFeatures()).thenReturn(new HashMap<>());
        lenient().when(courseDataService.getCoursesTaughtByDosen(any())).thenReturn(Collections.emptyList());
        lenient().when(courseDataService.convertCoursesToDTO(any())).thenReturn(Collections.emptyList());
        lenient().when(courseDataService.mapLowonganToCoursesAsync(any()))
                .thenReturn(CompletableFuture.completedFuture(Collections.emptyMap()));
        lenient().when(statisticsService.countAcceptedAssistantsPerCourse(any())).thenReturn(Collections.emptyMap());
        lenient().when(statisticsService.getTotalAcceptedAssistants(any())).thenReturn(0);
        lenient().when(statisticsService.getTotalOpenPositions(any())).thenReturn(0);
    }

    @Test
    void getDashboardData_happyPath() {
        // Setup validation
        doNothing().when(validationService).validateDosen(userId);
        when(validationService.getDosenById(userId)).thenReturn(dosenMock);

        // Setup feature provider
        Map<String, String> features = new HashMap<>();
        features.put("manajemenlowongan", "/api/manajemenlowongan");
        features.put("manajemenAsdos", "/api/asdos");
        features.put("profile", "/api/profile");
        features.put("periksaLog", "/api/log");
        when(featureProvider.getAvailableFeatures()).thenReturn(features);

        // Setup course data
        List<MataKuliah> courses = Arrays.asList(mataKuliah1, mataKuliah2);
        when(courseDataService.getCoursesTaughtByDosen(dosenMock)).thenReturn(courses);

        List<MataKuliahDTO> coursesDTO = Arrays.asList(mataKuliahDTO1, mataKuliahDTO2);
        when(courseDataService.convertCoursesToDTO(courses)).thenReturn(coursesDTO);

        // Setup lowongan data
        Map<String, List<LowonganDTO>> lowonganPerCourse = new HashMap<>();
        lowonganPerCourse.put("CS101", Arrays.asList(lowonganDTO1));
        lowonganPerCourse.put("CS102", Arrays.asList(lowonganDTO2));
        when(courseDataService.mapLowonganToCoursesAsync(courses))
                .thenReturn(CompletableFuture.completedFuture(lowonganPerCourse));

        // Setup statistics
        Map<String, Integer> assistantCounts = new HashMap<>();
        assistantCounts.put("CS101", 2);
        assistantCounts.put("CS102", 1);
        when(statisticsService.countAcceptedAssistantsPerCourse(courses)).thenReturn(assistantCounts);
        when(statisticsService.getTotalAcceptedAssistants(courses)).thenReturn(3);
        when(statisticsService.getTotalOpenPositions(courses)).thenReturn(5);

        // Execute
        DashboardResponse base = service.getDashboardData(userId);

        // Verify response type
        assertTrue(base instanceof DosenDashboardResponse);
        DosenDashboardResponse resp = (DosenDashboardResponse) base;

        // Verify common data
        assertEquals("DOSEN", resp.getUserRole());
        assertEquals("dosen@example.com", resp.getUsername());
        assertEquals("Dr. Dosen", resp.getFullName());
        assertEquals(features, resp.getAvailableFeatures());

        // Verify role-specific data
        assertEquals(coursesDTO, resp.getCoursesTaught());
        assertEquals(lowonganPerCourse, resp.getLowonganPerCourse());
        assertEquals(assistantCounts, resp.getAcceptedAssistantsPerCourse());
        assertEquals(2, resp.getCourseCount());
        assertEquals(3, resp.getAcceptedAssistantCount());
        assertEquals(5, resp.getOpenPositionCount());
    }

    @Test
    void getDashboardData_withNullUserId_shouldThrow() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDashboardData(null)
        );
        assertEquals("User ID tidak boleh null", ex.getMessage());
    }

    @Test
    void validateUser_userDoesNotExist_shouldThrow() {
        doThrow(new NoSuchElementException("User tidak ditemukan dengan ID: " + userId))
                .when(validationService).validateDosen(userId);

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("User tidak ditemukan"));
    }

    @Test
    void validateUser_notADosen_shouldThrow() {
        doThrow(new IllegalArgumentException("User dengan ID: " + userId + " bukan seorang Dosen"))
                .when(validationService).validateDosen(userId);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("bukan seorang Dosen"));
    }

    @Test
    void populateRoleSpecificData_withEmptyCoursesList_shouldHandleGracefully() {
        // Setup validation
        doNothing().when(validationService).validateDosen(userId);
        when(validationService.getDosenById(userId)).thenReturn(dosenMock);

        // Setup empty courses list
        when(courseDataService.getCoursesTaughtByDosen(dosenMock)).thenReturn(Collections.emptyList());

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        DosenDashboardResponse resp = (DosenDashboardResponse) base;

        // Verify empty data
        assertEquals(0, resp.getCourseCount());
        assertEquals(0, resp.getAcceptedAssistantCount());
        assertEquals(0, resp.getOpenPositionCount());
        assertTrue(resp.getCoursesTaught().isEmpty());
        assertTrue(resp.getLowonganPerCourse().isEmpty());
        assertTrue(resp.getAcceptedAssistantsPerCourse().isEmpty());
    }

    @Test
    void populateRoleSpecificData_withCourseDataServiceException_shouldThrow() {
        // Setup validation
        doNothing().when(validationService).validateDosen(userId);
        when(validationService.getDosenById(userId)).thenReturn(dosenMock);

        // Setup course data service to throw exception
        when(courseDataService.getCoursesTaughtByDosen(dosenMock))
                .thenThrow(new RuntimeException("Database error"));

        // Expect exception
        assertThrows(RuntimeException.class, () -> service.getDashboardData(userId));
    }

    @Test
    void populateRoleSpecificData_withAsyncException_shouldThrow() {
        // Setup validation
        doNothing().when(validationService).validateDosen(userId);
        when(validationService.getDosenById(userId)).thenReturn(dosenMock);

        // Setup courses
        List<MataKuliah> courses = Arrays.asList(mataKuliah1);
        when(courseDataService.getCoursesTaughtByDosen(dosenMock)).thenReturn(courses);
        when(courseDataService.convertCoursesToDTO(courses)).thenReturn(Arrays.asList(mataKuliahDTO1));

        // Setup async failure
        CompletableFuture<Map<String, List<LowonganDTO>>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Async processing failed"));
        when(courseDataService.mapLowonganToCoursesAsync(courses)).thenReturn(failedFuture);

        // Expect exception due to async failure
        assertThrows(RuntimeException.class, () -> service.getDashboardData(userId));
    }

    @Test
    void asyncProcessing_performanceTest() {
        // Setup validation
        doNothing().when(validationService).validateDosen(userId);
        when(validationService.getDosenById(userId)).thenReturn(dosenMock);

        // Create multiple courses for testing async performance
        List<MataKuliah> manyCourses = new ArrayList<>();
        List<MataKuliahDTO> manyCoursesDTO = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            MataKuliah course = new MataKuliah("CS" + String.format("%03d", i), "Course " + i, "Desc " + i);
            manyCourses.add(course);
            manyCoursesDTO.add(mock(MataKuliahDTO.class));
        }

        when(courseDataService.getCoursesTaughtByDosen(dosenMock)).thenReturn(manyCourses);
        when(courseDataService.convertCoursesToDTO(manyCourses)).thenReturn(manyCoursesDTO);

        // Setup async processing with delay to simulate real async behavior
        CompletableFuture<Map<String, List<LowonganDTO>>> asyncFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100); // Simulate processing time
                Map<String, List<LowonganDTO>> result = new HashMap<>();
                for (MataKuliah course : manyCourses) {
                    result.put(course.getKode(), Arrays.asList(mock(LowonganDTO.class)));
                }
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        when(courseDataService.mapLowonganToCoursesAsync(manyCourses)).thenReturn(asyncFuture);

        // Setup statistics
        Map<String, Integer> assistantCounts = new HashMap<>();
        for (MataKuliah course : manyCourses) {
            assistantCounts.put(course.getKode(), 1);
        }
        when(statisticsService.countAcceptedAssistantsPerCourse(manyCourses)).thenReturn(assistantCounts);
        when(statisticsService.getTotalAcceptedAssistants(manyCourses)).thenReturn(10);
        when(statisticsService.getTotalOpenPositions(manyCourses)).thenReturn(20);

        long startTime = System.currentTimeMillis();

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        DosenDashboardResponse resp = (DosenDashboardResponse) base;

        long duration = System.currentTimeMillis() - startTime;

        // Verify results
        assertEquals(10, resp.getCourseCount());
        assertEquals(10, resp.getAcceptedAssistantCount());
        assertEquals(20, resp.getOpenPositionCount());
        assertEquals(10, resp.getCoursesTaught().size());
        assertEquals(10, resp.getLowonganPerCourse().size());

        // Verify async processing was reasonably fast
        assertTrue(duration < 300, "Async processing should be faster than sequential");
    }

    @Test
    void createDashboardResponse_shouldReturnCorrectType() {
        DashboardResponse response = service.createDashboardResponse();
        assertTrue(response instanceof DosenDashboardResponse);
    }

    @Test
    void populateCommonData_shouldSetCorrectValues() {
        // Setup validation
        doNothing().when(validationService).validateDosen(userId);
        when(validationService.getDosenById(userId)).thenReturn(dosenMock);

        Map<String, String> features = new HashMap<>();
        features.put("test", "/api/test");
        when(featureProvider.getAvailableFeatures()).thenReturn(features);

        // Execute
        DashboardResponse response = service.getDashboardData(userId);

        // Verify common data
        assertEquals("DOSEN", response.getUserRole());
        assertEquals("dosen@example.com", response.getUsername());
        assertEquals("Dr. Dosen", response.getFullName());
        assertEquals(features, response.getAvailableFeatures());
    }

    @Test
    void getDashboardData_verifyServiceInteractions() {
        // Setup validation
        doNothing().when(validationService).validateDosen(userId);
        when(validationService.getDosenById(userId)).thenReturn(dosenMock);

        // Setup courses
        List<MataKuliah> courses = Arrays.asList(mataKuliah1, mataKuliah2);
        when(courseDataService.getCoursesTaughtByDosen(dosenMock)).thenReturn(courses);
        when(courseDataService.convertCoursesToDTO(courses)).thenReturn(Arrays.asList(mataKuliahDTO1, mataKuliahDTO2));
        when(courseDataService.mapLowonganToCoursesAsync(courses))
                .thenReturn(CompletableFuture.completedFuture(new HashMap<>()));

        Map<String, Integer> assistantCounts = new HashMap<>();
        when(statisticsService.countAcceptedAssistantsPerCourse(courses)).thenReturn(assistantCounts);
        when(statisticsService.getTotalAcceptedAssistants(courses)).thenReturn(0);
        when(statisticsService.getTotalOpenPositions(courses)).thenReturn(0);

        // Execute
        service.getDashboardData(userId);

        // Verify all service interactions
        verify(validationService).validateDosen(userId);
        verify(validationService, times(2)).getDosenById(userId); // Called in populateCommonData and populateRoleSpecificData
        verify(featureProvider).getAvailableFeatures();
        verify(courseDataService).getCoursesTaughtByDosen(dosenMock);
        verify(courseDataService).convertCoursesToDTO(courses);
        verify(courseDataService).mapLowonganToCoursesAsync(courses);
        verify(statisticsService).countAcceptedAssistantsPerCourse(courses);
        verify(statisticsService).getTotalAcceptedAssistants(courses);
        verify(statisticsService).getTotalOpenPositions(courses);
    }

    @Test
    void populateRoleSpecificData_withPartialServiceFailures_shouldHandleGracefully() {
        // Setup validation
        doNothing().when(validationService).validateDosen(userId);
        when(validationService.getDosenById(userId)).thenReturn(dosenMock);

        // Setup courses
        List<MataKuliah> courses = Arrays.asList(mataKuliah1, mataKuliah2);
        when(courseDataService.getCoursesTaughtByDosen(dosenMock)).thenReturn(courses);
        when(courseDataService.convertCoursesToDTO(courses)).thenReturn(Arrays.asList(mataKuliahDTO1, mataKuliahDTO2));

        // Async service succeeds
        Map<String, List<LowonganDTO>> lowonganPerCourse = new HashMap<>();
        lowonganPerCourse.put("CS101", Arrays.asList(lowonganDTO1));
        when(courseDataService.mapLowonganToCoursesAsync(courses))
                .thenReturn(CompletableFuture.completedFuture(lowonganPerCourse));

        // Statistics service partially fails (some methods succeed, some fail)
        when(statisticsService.countAcceptedAssistantsPerCourse(courses)).thenReturn(new HashMap<>());
        when(statisticsService.getTotalAcceptedAssistants(courses)).thenReturn(5);
        when(statisticsService.getTotalOpenPositions(courses)).thenThrow(new RuntimeException("Statistics error"));

        // Should still process other data even if one statistics method fails
        assertThrows(RuntimeException.class, () -> service.getDashboardData(userId));

        // Verify that other services were still called
        verify(courseDataService).getCoursesTaughtByDosen(dosenMock);
        verify(courseDataService).convertCoursesToDTO(courses);
        verify(courseDataService).mapLowonganToCoursesAsync(courses);
        verify(statisticsService).countAcceptedAssistantsPerCourse(courses);
        verify(statisticsService).getTotalAcceptedAssistants(courses);
    }

    @Test
    void populateRoleSpecificData_withMultipleLowonganForCourses() {
        // Setup validation
        doNothing().when(validationService).validateDosen(userId);
        when(validationService.getDosenById(userId)).thenReturn(dosenMock);

        // Setup courses
        List<MataKuliah> courses = Arrays.asList(mataKuliah1, mataKuliah2);
        when(courseDataService.getCoursesTaughtByDosen(dosenMock)).thenReturn(courses);
        when(courseDataService.convertCoursesToDTO(courses)).thenReturn(Arrays.asList(mataKuliahDTO1, mataKuliahDTO2));

        // Setup multiple lowongan per course
        Map<String, List<LowonganDTO>> lowonganPerCourse = new HashMap<>();
        lowonganPerCourse.put("CS101", Arrays.asList(lowonganDTO1, lowonganDTO2)); // Multiple lowongan for course 1
        lowonganPerCourse.put("CS102", Arrays.asList(lowonganDTO1)); // Single lowongan for course 2
        when(courseDataService.mapLowonganToCoursesAsync(courses))
                .thenReturn(CompletableFuture.completedFuture(lowonganPerCourse));

        // Setup statistics with different assistant counts
        Map<String, Integer> assistantCounts = new HashMap<>();
        assistantCounts.put("CS101", 5); // Multiple assistants
        assistantCounts.put("CS102", 2); // Fewer assistants
        when(statisticsService.countAcceptedAssistantsPerCourse(courses)).thenReturn(assistantCounts);
        when(statisticsService.getTotalAcceptedAssistants(courses)).thenReturn(7);
        when(statisticsService.getTotalOpenPositions(courses)).thenReturn(12);

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        DosenDashboardResponse resp = (DosenDashboardResponse) base;

        // Verify multiple lowongan handling
        assertEquals(2, resp.getLowonganPerCourse().size());
        assertEquals(2, resp.getLowonganPerCourse().get("CS101").size()); // Course 1 has 2 lowongan
        assertEquals(1, resp.getLowonganPerCourse().get("CS102").size()); // Course 2 has 1 lowongan

        // Verify assistant counts
        assertEquals(Integer.valueOf(5), resp.getAcceptedAssistantsPerCourse().get("CS101"));
        assertEquals(Integer.valueOf(2), resp.getAcceptedAssistantsPerCourse().get("CS102"));
        assertEquals(7, resp.getAcceptedAssistantCount());
        assertEquals(12, resp.getOpenPositionCount());
    }
}