package id.ac.ui.cs.advprog.hiringgo.dashboard.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.AdminDashboardServiceImpl;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.DosenDashboardServiceImpl;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.MahasiswaDashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.concurrent.CompletionException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private MahasiswaDashboardServiceImpl mahasiswaDashboardService;

    @Mock
    private DosenDashboardServiceImpl dosenDashboardService;

    @Mock
    private AdminDashboardServiceImpl adminDashboardService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DashboardController dashboardController;

    private DashboardResponse mahasiswaResponse;
    private DashboardResponse dosenResponse;
    private DashboardResponse adminResponse;

    private UUID mahasiswaId;
    private UUID dosenId;
    private UUID adminId;

    @BeforeEach
    void setup() {
        // Generate IDs
        mahasiswaId = UUID.randomUUID();
        dosenId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        // Build dummy responses
        mahasiswaResponse = new DashboardResponse();
        mahasiswaResponse.setUserRole("MAHASISWA");
        mahasiswaResponse.setUsername("mhs@example.com");
        mahasiswaResponse.setFullName("Test Mahasiswa");
        Map<String, String> mf = new HashMap<>();
        mf.put("pendaftaran", "/api/pendaftaran");
        mahasiswaResponse.setAvailableFeatures(mf);

        dosenResponse = new DashboardResponse();
        dosenResponse.setUserRole("DOSEN");
        dosenResponse.setUsername("dosen@example.com");
        dosenResponse.setFullName("Test Dosen");
        Map<String, String> df = new HashMap<>();
        df.put("lowongan", "/api/lowongan");
        dosenResponse.setAvailableFeatures(df);

        adminResponse = new DashboardResponse();
        adminResponse.setUserRole("ADMIN");
        adminResponse.setUsername("admin@example.com");
        adminResponse.setFullName("Test Admin");
        Map<String, String> af = new HashMap<>();
        af.put("account-management", "/api/account-management");
        adminResponse.setAvailableFeatures(af);
    }

    @Test
    void testGetMahasiswaDashboardSuccess() {
        // Set up specific stubs for this test
        when(authentication.isAuthenticated()).thenReturn(true);
        Mahasiswa m = mock(Mahasiswa.class);
        when(m.getId()).thenReturn(mahasiswaId);
        when(authentication.getPrincipal()).thenReturn(m);
        when(mahasiswaDashboardService.getDashboardData(mahasiswaId)).thenReturn(mahasiswaResponse);

        ResponseEntity<DashboardResponse> resp =
                dashboardController.getMahasiswaDashboard(authentication);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(mahasiswaResponse, resp.getBody());
        verify(mahasiswaDashboardService).getDashboardData(mahasiswaId);
    }

    @Test
    void testGetDosenDashboardSuccess() {
        when(authentication.isAuthenticated()).thenReturn(true);
        Dosen d = mock(Dosen.class);
        when(d.getId()).thenReturn(dosenId);
        when(authentication.getPrincipal()).thenReturn(d);
        when(dosenDashboardService.getDashboardData(dosenId)).thenReturn(dosenResponse);

        ResponseEntity<DashboardResponse> resp =
                dashboardController.getDosenDashboard(authentication);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(dosenResponse, resp.getBody());
        verify(dosenDashboardService).getDashboardData(dosenId);
    }

    @Test
    void testGetAdminDashboardSuccess() {
        when(authentication.isAuthenticated()).thenReturn(true);
        Admin a = mock(Admin.class);
        when(a.getId()).thenReturn(adminId);
        when(authentication.getPrincipal()).thenReturn(a);
        when(adminDashboardService.getDashboardData(adminId)).thenReturn(adminResponse);

        ResponseEntity<DashboardResponse> resp =
                dashboardController.getAdminDashboard(authentication);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(adminResponse, resp.getBody());
        verify(adminDashboardService).getDashboardData(adminId);
    }

    @Test
    void testGetMahasiswaDashboardUnauthorized() {
        ResponseEntity<DashboardResponse> resp =
                dashboardController.getMahasiswaDashboard(null);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        verify(mahasiswaDashboardService, never()).getDashboardData(any());
    }

    @Test
    void testGetDosenDashboardUnauthorized() {
        ResponseEntity<DashboardResponse> resp =
                dashboardController.getDosenDashboard(null);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        verify(dosenDashboardService, never()).getDashboardData(any());
    }

    @Test
    void testGetAdminDashboardUnauthorized() {
        ResponseEntity<DashboardResponse> resp =
                dashboardController.getAdminDashboard(null);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        verify(adminDashboardService, never()).getDashboardData(any());
    }

    @Test
    void testGetMahasiswaDashboardNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<DashboardResponse> resp =
                dashboardController.getMahasiswaDashboard(authentication);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        verify(mahasiswaDashboardService, never()).getDashboardData(any());
    }

    @Test
    void testGetDosenDashboardNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<DashboardResponse> resp =
                dashboardController.getDosenDashboard(authentication);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        verify(dosenDashboardService, never()).getDashboardData(any());
    }

    @Test
    void testGetAdminDashboardNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<DashboardResponse> resp =
                dashboardController.getAdminDashboard(authentication);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        verify(adminDashboardService, never()).getDashboardData(any());
    }

    @Test
    void testClassCastExceptionInMahasiswaDashboard() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new Object());

        assertThrows(ClassCastException.class, () ->
                dashboardController.getMahasiswaDashboard(authentication)
        );
        verify(mahasiswaDashboardService, never()).getDashboardData(any());
    }

    @Test
    void testClassCastExceptionInDosenDashboard() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new Object());

        assertThrows(ClassCastException.class, () ->
                dashboardController.getDosenDashboard(authentication)
        );
        verify(dosenDashboardService, never()).getDashboardData(any());
    }

    @Test
    void testClassCastExceptionInAdminDashboard() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new Object());

        assertThrows(ClassCastException.class, () ->
                dashboardController.getAdminDashboard(authentication)
        );
        verify(adminDashboardService, never()).getDashboardData(any());
    }

    // New tests for exception handlers
    @Test
    void testMahasiswaDashboardWithNoSuchElementException() {
        // Setup
        when(authentication.isAuthenticated()).thenReturn(true);
        Mahasiswa m = mock(Mahasiswa.class);
        when(m.getId()).thenReturn(mahasiswaId);
        when(authentication.getPrincipal()).thenReturn(m);

        // Make the service throw the exception
        NoSuchElementException ex = new NoSuchElementException("Mahasiswa tidak ditemukan");
        when(mahasiswaDashboardService.getDashboardData(mahasiswaId)).thenThrow(ex);

        // Execute and verify - exception should propagate to DashboardExceptionHandler
        assertThrows(NoSuchElementException.class, () ->
                dashboardController.getMahasiswaDashboard(authentication)
        );

        // Verify service was called
        verify(mahasiswaDashboardService).getDashboardData(mahasiswaId);
    }

    @Test
    void testDosenDashboardWithIllegalArgumentException() {
        // Setup
        when(authentication.isAuthenticated()).thenReturn(true);
        Dosen d = mock(Dosen.class);
        when(d.getId()).thenReturn(dosenId);
        when(authentication.getPrincipal()).thenReturn(d);

        // Make the service throw the exception
        IllegalArgumentException ex = new IllegalArgumentException("Invalid dosen ID");
        when(dosenDashboardService.getDashboardData(dosenId)).thenThrow(ex);

        // Execute and verify - exception should propagate to DashboardExceptionHandler
        assertThrows(IllegalArgumentException.class, () ->
                dashboardController.getDosenDashboard(authentication)
        );

        // Verify service was called
        verify(dosenDashboardService).getDashboardData(dosenId);
    }

    @Test
    void testAdminDashboardWithCompletionException() {
        // Setup
        when(authentication.isAuthenticated()).thenReturn(true);
        Admin a = mock(Admin.class);
        when(a.getId()).thenReturn(adminId);
        when(authentication.getPrincipal()).thenReturn(a);

        // Make the service throw async exception
        CompletionException ex = new CompletionException("Async error", new RuntimeException("Database error"));
        when(adminDashboardService.getDashboardData(adminId)).thenThrow(ex);

        // Execute and verify - exception should propagate to DashboardExceptionHandler
        assertThrows(CompletionException.class, () ->
                dashboardController.getAdminDashboard(authentication)
        );

        // Verify service was called
        verify(adminDashboardService).getDashboardData(adminId);
    }
}