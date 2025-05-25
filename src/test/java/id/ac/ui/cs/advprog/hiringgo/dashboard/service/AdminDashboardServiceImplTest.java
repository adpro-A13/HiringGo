package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.AdminDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation.AdminStatisticsService;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature.AdminFeatureProvider;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.validation.AdminValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceImplTest {

    @Mock
    private AdminValidationService validationService;

    @Mock
    private AdminStatisticsService statisticsService;

    @Mock
    private AdminFeatureProvider featureProvider;

    @InjectMocks
    private AdminDashboardServiceImpl service;

    private UUID userId;
    private Admin adminMock;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        adminMock = mock(Admin.class);

        // Use lenient() for common stubbing that might not always be used
        lenient().when(adminMock.getUsername()).thenReturn("admin@example.com");
        lenient().when(featureProvider.getAvailableFeatures()).thenReturn(new HashMap<>());
        lenient().when(statisticsService.countDosenAsync())
                .thenReturn(CompletableFuture.completedFuture(0));
        lenient().when(statisticsService.countMahasiswaAsync())
                .thenReturn(CompletableFuture.completedFuture(0));
        lenient().when(statisticsService.countCoursesAsync())
                .thenReturn(CompletableFuture.completedFuture(0));
        lenient().when(statisticsService.countLowonganAsync())
                .thenReturn(CompletableFuture.completedFuture(0));
    }

    @Test
    void getDashboardData_happyPath() {
        // Setup validation
        doNothing().when(validationService).validateAdmin(userId);
        when(validationService.getAdminById(userId)).thenReturn(adminMock);

        // Setup feature provider
        Map<String, String> features = new HashMap<>();
        features.put("manajemenAkun", "/api/admin/accounts");
        features.put("manajemenMataKuliah", "/api/admin/matakuliah");
        features.put("manajemenLowongan", "/api/admin/lowongan");
        features.put("profile", "/api/profile");
        when(featureProvider.getAvailableFeatures()).thenReturn(features);

        // Setup statistics service with async operations
        when(statisticsService.countDosenAsync())
                .thenReturn(CompletableFuture.completedFuture(5));
        when(statisticsService.countMahasiswaAsync())
                .thenReturn(CompletableFuture.completedFuture(120));
        when(statisticsService.countCoursesAsync())
                .thenReturn(CompletableFuture.completedFuture(15));
        when(statisticsService.countLowonganAsync())
                .thenReturn(CompletableFuture.completedFuture(8));

        // Execute
        DashboardResponse base = service.getDashboardData(userId);

        // Verify response type
        assertTrue(base instanceof AdminDashboardResponse);
        AdminDashboardResponse resp = (AdminDashboardResponse) base;

        // Verify common data
        assertEquals("ADMIN", resp.getUserRole());
        assertEquals("admin@example.com", resp.getUsername());
        assertEquals("admin@example.com", resp.getFullName()); // Admin uses username as fullName
        assertEquals(features, resp.getAvailableFeatures());

        // Verify role-specific data
        assertEquals(5, resp.getDosenCount());
        assertEquals(120, resp.getMahasiswaCount());
        assertEquals(15, resp.getCourseCount());
        assertEquals(8, resp.getLowonganCount());
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
                .when(validationService).validateAdmin(userId);

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("User tidak ditemukan"));
    }

    @Test
    void validateUser_notAnAdmin_shouldThrow() {
        doThrow(new IllegalArgumentException("User dengan ID: " + userId + " bukan admin"))
                .when(validationService).validateAdmin(userId);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("bukan admin"));
    }

    @Test
    void populateRoleSpecificData_withZeroCounts_shouldHandleGracefully() {
        // Setup validation
        doNothing().when(validationService).validateAdmin(userId);
        when(validationService.getAdminById(userId)).thenReturn(adminMock);

        // All statistics return zero
        when(statisticsService.countDosenAsync())
                .thenReturn(CompletableFuture.completedFuture(0));
        when(statisticsService.countMahasiswaAsync())
                .thenReturn(CompletableFuture.completedFuture(0));
        when(statisticsService.countCoursesAsync())
                .thenReturn(CompletableFuture.completedFuture(0));
        when(statisticsService.countLowonganAsync())
                .thenReturn(CompletableFuture.completedFuture(0));

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        AdminDashboardResponse resp = (AdminDashboardResponse) base;

        // Verify all counts are zero
        assertEquals(0, resp.getDosenCount());
        assertEquals(0, resp.getMahasiswaCount());
        assertEquals(0, resp.getCourseCount());
        assertEquals(0, resp.getLowonganCount());
    }

    @Test
    void populateRoleSpecificData_withAsyncException_shouldThrow() {
        // Setup validation
        doNothing().when(validationService).validateAdmin(userId);
        when(validationService.getAdminById(userId)).thenReturn(adminMock);

        // Setup one async operation to fail
        CompletableFuture<Integer> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Database error"));
        when(statisticsService.countDosenAsync()).thenReturn(failedFuture);

        when(statisticsService.countMahasiswaAsync())
                .thenReturn(CompletableFuture.completedFuture(10));
        when(statisticsService.countCoursesAsync())
                .thenReturn(CompletableFuture.completedFuture(5));
        when(statisticsService.countLowonganAsync())
                .thenReturn(CompletableFuture.completedFuture(3));

        // Expect CompletionException to be thrown due to async failure
        assertThrows(CompletionException.class, () -> service.getDashboardData(userId));
    }

    @Test
    void asyncStatistics_performanceTest() {
        // Setup validation
        doNothing().when(validationService).validateAdmin(userId);
        when(validationService.getAdminById(userId)).thenReturn(adminMock);

        // Setup async operations with delay to simulate real async behavior
        CompletableFuture<Integer> dosenFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100); // Simulate processing time
                return 10;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Integer> mahasiswaFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100); // Simulate processing time
                return 200;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Integer> courseFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100); // Simulate processing time
                return 25;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Integer> lowonganFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100); // Simulate processing time
                return 15;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        when(statisticsService.countDosenAsync()).thenReturn(dosenFuture);
        when(statisticsService.countMahasiswaAsync()).thenReturn(mahasiswaFuture);
        when(statisticsService.countCoursesAsync()).thenReturn(courseFuture);
        when(statisticsService.countLowonganAsync()).thenReturn(lowonganFuture);

        long startTime = System.currentTimeMillis();

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        AdminDashboardResponse resp = (AdminDashboardResponse) base;

        long duration = System.currentTimeMillis() - startTime;

        // Verify results
        assertEquals(10, resp.getDosenCount());
        assertEquals(200, resp.getMahasiswaCount());
        assertEquals(25, resp.getCourseCount());
        assertEquals(15, resp.getLowonganCount());

        // Verify async processing was reasonably fast (should be around 100ms, not 400ms if sequential)
        assertTrue(duration < 300, "Async processing should be faster than sequential: " + duration + "ms");
    }

    @Test
    void createDashboardResponse_shouldReturnCorrectType() {
        DashboardResponse response = service.createDashboardResponse();
        assertTrue(response instanceof AdminDashboardResponse);
    }

    @Test
    void populateCommonData_shouldSetCorrectValues() {
        // Setup validation
        doNothing().when(validationService).validateAdmin(userId);
        when(validationService.getAdminById(userId)).thenReturn(adminMock);

        Map<String, String> features = new HashMap<>();
        features.put("test", "/api/test");
        when(featureProvider.getAvailableFeatures()).thenReturn(features);

        // Execute
        DashboardResponse response = service.getDashboardData(userId);

        // Verify common data
        assertEquals("ADMIN", response.getUserRole());
        assertEquals("admin@example.com", response.getUsername());
        assertEquals("admin@example.com", response.getFullName());
        assertEquals(features, response.getAvailableFeatures());
    }

    @Test
    void getDashboardData_verifyServiceInteractions() {
        // Setup validation
        doNothing().when(validationService).validateAdmin(userId);
        when(validationService.getAdminById(userId)).thenReturn(adminMock);

        // Setup statistics service
        when(statisticsService.countDosenAsync())
                .thenReturn(CompletableFuture.completedFuture(3));
        when(statisticsService.countMahasiswaAsync())
                .thenReturn(CompletableFuture.completedFuture(50));
        when(statisticsService.countCoursesAsync())
                .thenReturn(CompletableFuture.completedFuture(8));
        when(statisticsService.countLowonganAsync())
                .thenReturn(CompletableFuture.completedFuture(5));

        // Execute
        service.getDashboardData(userId);

        // Verify all service interactions
        verify(validationService).validateAdmin(userId);
        verify(validationService, times(1)).getAdminById(userId); // Only called once in populateCommonData
        verify(featureProvider).getAvailableFeatures();
        verify(statisticsService).countDosenAsync();
        verify(statisticsService).countMahasiswaAsync();
        verify(statisticsService).countCoursesAsync();
        verify(statisticsService).countLowonganAsync();
    }

    @Test
    void populateRoleSpecificData_withPartialAsyncFailures_shouldThrow() {
        // Setup validation
        doNothing().when(validationService).validateAdmin(userId);
        when(validationService.getAdminById(userId)).thenReturn(adminMock);

        // Setup partial failures
        when(statisticsService.countDosenAsync())
                .thenReturn(CompletableFuture.completedFuture(5));
        when(statisticsService.countMahasiswaAsync())
                .thenReturn(CompletableFuture.completedFuture(100));

        // These will fail
        CompletableFuture<Integer> failedCourseFuture = new CompletableFuture<>();
        failedCourseFuture.completeExceptionally(new RuntimeException("Course count failed"));
        when(statisticsService.countCoursesAsync()).thenReturn(failedCourseFuture);

        CompletableFuture<Integer> failedLowonganFuture = new CompletableFuture<>();
        failedLowonganFuture.completeExceptionally(new RuntimeException("Lowongan count failed"));
        when(statisticsService.countLowonganAsync()).thenReturn(failedLowonganFuture);

        // Should fail due to async exceptions
        assertThrows(CompletionException.class, () -> service.getDashboardData(userId));

        // Verify that successful services were still called
        verify(statisticsService).countDosenAsync();
        verify(statisticsService).countMahasiswaAsync();
        verify(statisticsService).countCoursesAsync();
        verify(statisticsService).countLowonganAsync();
    }

    @Test
    void populateRoleSpecificData_withLargeNumbers_shouldHandleCorrectly() {
        // Setup validation
        doNothing().when(validationService).validateAdmin(userId);
        when(validationService.getAdminById(userId)).thenReturn(adminMock);

        // Setup large numbers to test integer handling
        when(statisticsService.countDosenAsync())
                .thenReturn(CompletableFuture.completedFuture(999));
        when(statisticsService.countMahasiswaAsync())
                .thenReturn(CompletableFuture.completedFuture(10000));
        when(statisticsService.countCoursesAsync())
                .thenReturn(CompletableFuture.completedFuture(500));
        when(statisticsService.countLowonganAsync())
                .thenReturn(CompletableFuture.completedFuture(1500));

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        AdminDashboardResponse resp = (AdminDashboardResponse) base;

        // Verify large numbers are handled correctly
        assertEquals(999, resp.getDosenCount());
        assertEquals(10000, resp.getMahasiswaCount());
        assertEquals(500, resp.getCourseCount());
        assertEquals(1500, resp.getLowonganCount());
    }

    @Test
    void populateRoleSpecificData_withAllAsyncOperationsSucceeding() {
        // Setup validation
        doNothing().when(validationService).validateAdmin(userId);
        when(validationService.getAdminById(userId)).thenReturn(adminMock);

        // Setup all async operations to succeed with different values
        when(statisticsService.countDosenAsync())
                .thenReturn(CompletableFuture.completedFuture(12));
        when(statisticsService.countMahasiswaAsync())
                .thenReturn(CompletableFuture.completedFuture(345));
        when(statisticsService.countCoursesAsync())
                .thenReturn(CompletableFuture.completedFuture(67));
        when(statisticsService.countLowonganAsync())
                .thenReturn(CompletableFuture.completedFuture(89));

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        AdminDashboardResponse resp = (AdminDashboardResponse) base;

        // Verify all values are set correctly
        assertEquals(12, resp.getDosenCount());
        assertEquals(345, resp.getMahasiswaCount());
        assertEquals(67, resp.getCourseCount());
        assertEquals(89, resp.getLowonganCount());

        // Verify all async methods were called exactly once
        verify(statisticsService, times(1)).countDosenAsync();
        verify(statisticsService, times(1)).countMahasiswaAsync();
        verify(statisticsService, times(1)).countCoursesAsync();
        verify(statisticsService, times(1)).countLowonganAsync();
    }

    @Test
    void populateCommonData_withValidationServiceException_shouldThrow() {
        // Setup validation to succeed initially but fail on getAdminById
        doNothing().when(validationService).validateAdmin(userId);
        when(validationService.getAdminById(userId))
                .thenThrow(new NoSuchElementException("Admin not found"));

        // Should fail during populateCommonData
        assertThrows(NoSuchElementException.class, () -> service.getDashboardData(userId));

        // Verify validation was attempted
        verify(validationService).validateAdmin(userId);
        verify(validationService).getAdminById(userId);
    }

    @Test
    void getDashboardData_withRealAdminInstance_shouldWork() {
        // Create real Admin instance instead of mock
        Admin realAdmin = new Admin("real-admin@test.com", "password123");

        // Setup validation
        doNothing().when(validationService).validateAdmin(userId);
        when(validationService.getAdminById(userId)).thenReturn(realAdmin);

        // Setup feature provider
        Map<String, String> features = new HashMap<>();
        features.put("manajemenAkun", "/api/admin/accounts");
        when(featureProvider.getAvailableFeatures()).thenReturn(features);

        // Setup statistics
        when(statisticsService.countDosenAsync())
                .thenReturn(CompletableFuture.completedFuture(7));
        when(statisticsService.countMahasiswaAsync())
                .thenReturn(CompletableFuture.completedFuture(150));
        when(statisticsService.countCoursesAsync())
                .thenReturn(CompletableFuture.completedFuture(20));
        when(statisticsService.countLowonganAsync())
                .thenReturn(CompletableFuture.completedFuture(12));

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        AdminDashboardResponse resp = (AdminDashboardResponse) base;

        // Verify with real admin data
        assertEquals("ADMIN", resp.getUserRole());
        assertEquals("real-admin@test.com", resp.getUsername());
        assertEquals("real-admin@test.com", resp.getFullName());
        assertEquals(features, resp.getAvailableFeatures());
        assertEquals(7, resp.getDosenCount());
        assertEquals(150, resp.getMahasiswaCount());
        assertEquals(20, resp.getCourseCount());
        assertEquals(12, resp.getLowonganCount());
    }
}