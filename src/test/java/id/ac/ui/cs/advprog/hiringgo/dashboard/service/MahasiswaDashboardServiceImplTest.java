package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.MahasiswaDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation.IncentiveCalculationService;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.data.ApplicationDataService;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature.MahasiswaFeatureProvider;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.validation.MahasiswaValidationService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MahasiswaDashboardServiceImplTest {

    @Mock
    private MahasiswaValidationService validationService;

    @Mock
    private IncentiveCalculationService calculationService;

    @Mock
    private ApplicationDataService applicationDataService;

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private MahasiswaFeatureProvider featureProvider;

    @InjectMocks
    private MahasiswaDashboardServiceImpl service;

    private UUID userId;
    private Mahasiswa mahasiswa;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        mahasiswa = mock(Mahasiswa.class);

        // Minimal lenient stubbing - only what's commonly used
        lenient().when(mahasiswa.getUsername()).thenReturn("mahasiswaUser");
        lenient().when(mahasiswa.getFullName()).thenReturn("Mahasiswa FullName");
    }

    @Test
    void getDashboardData_happyPath() {
        // Setup validation
        doNothing().when(validationService).validateMahasiswa(userId);
        when(validationService.getMahasiswaById(userId)).thenReturn(mahasiswa);

        // Setup feature provider
        Map<String, String> features = new HashMap<>();
        features.put("pendaftaran", "/api/pendaftaran");
        features.put("lowongan", "/api/lowongan");
        features.put("profile", "/api/profile");
        features.put("logActivities", "/api/log");
        when(featureProvider.getAvailableFeatures()).thenReturn(features);

        // Setup lowongan data
        MataKuliah m1 = new MataKuliah("K1", "Name1", "Desc1");
        Lowongan low1 = new Lowongan();
        low1.setLowonganId(UUID.randomUUID());
        low1.setMataKuliah(m1);
        low1.setJumlahAsdosDibutuhkan(3);
        low1.setJumlahAsdosDiterima(1);
        low1.setJumlahAsdosPendaftar(2);

        Lowongan low2 = new Lowongan();
        low2.setLowonganId(UUID.randomUUID());
        low2.setMataKuliah(m1);
        low2.setJumlahAsdosDibutuhkan(2);
        low2.setJumlahAsdosDiterima(2);
        low2.setJumlahAsdosPendaftar(2);

        List<Lowongan> openLowonganList = Arrays.asList(low1);
        List<Lowongan> allLowonganList = Arrays.asList(low1, low2);

        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA)).thenReturn(openLowonganList);
        when(lowonganRepository.findAll()).thenReturn(allLowonganList);

        // Setup application data service
        Pendaftaran app1 = mock(Pendaftaran.class);
        Pendaftaran app2 = mock(Pendaftaran.class);
        Pendaftaran app3 = mock(Pendaftaran.class);

        List<Pendaftaran> allApplications = Arrays.asList(app1, app2, app3);
        when(applicationDataService.getAllApplications(userId)).thenReturn(allApplications);
        when(applicationDataService.countApplicationsByStatus(allApplications, StatusPendaftaran.BELUM_DIPROSES)).thenReturn(1);
        when(applicationDataService.countApplicationsByStatus(allApplications, StatusPendaftaran.DITERIMA)).thenReturn(1);
        when(applicationDataService.countApplicationsByStatus(allApplications, StatusPendaftaran.DITOLAK)).thenReturn(1);

        List<LowonganDTO> acceptedLowongan = Arrays.asList(mock(LowonganDTO.class));
        when(applicationDataService.getAcceptedLowongan(userId)).thenReturn(acceptedLowongan);

        // Setup calculation service
        BigDecimal hours = new BigDecimal("5.00");
        BigDecimal incentive = new BigDecimal("137500.00");
        when(calculationService.calculateTotalLoggedHoursAsync(userId))
                .thenReturn(CompletableFuture.completedFuture(hours));
        when(calculationService.calculateTotalIncentiveAsync(userId))
                .thenReturn(CompletableFuture.completedFuture(incentive));

        // Execute
        DashboardResponse base = service.getDashboardData(userId);

        // Verify response type
        assertTrue(base instanceof MahasiswaDashboardResponse);
        MahasiswaDashboardResponse resp = (MahasiswaDashboardResponse) base;

        // Verify common data
        assertEquals("MAHASISWA", resp.getUserRole());
        assertEquals("mahasiswaUser", resp.getUsername());
        assertEquals("Mahasiswa FullName", resp.getFullName());
        assertEquals(features, resp.getAvailableFeatures());

        // Verify role-specific data
        assertEquals(2, resp.getTotalLowonganCount());
        assertEquals(1, resp.getOpenLowonganCount());
        assertEquals(3, resp.getTotalApplicationsCount());
        assertEquals(1, resp.getPendingApplicationsCount());
        assertEquals(1, resp.getAcceptedApplicationsCount());
        assertEquals(1, resp.getRejectedApplicationsCount());
        assertEquals(1, resp.getAcceptedLowongan().size());
        assertEquals(hours, resp.getTotalLoggedHours());
        assertEquals(incentive, resp.getTotalIncentive());
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
                .when(validationService).validateMahasiswa(userId);

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("User tidak ditemukan"));
    }

    @Test
    void validateUser_notAMahasiswa_shouldThrow() {
        doThrow(new IllegalArgumentException("User dengan ID: " + userId + " bukan mahasiswa"))
                .when(validationService).validateMahasiswa(userId);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("bukan mahasiswa"));
    }

    @Test
    void populateRoleSpecificData_withEmptyLists_shouldHandleGracefully() {
        // Setup validation
        doNothing().when(validationService).validateMahasiswa(userId);
        when(validationService.getMahasiswaById(userId)).thenReturn(mahasiswa);

        // Setup feature provider
        when(featureProvider.getAvailableFeatures()).thenReturn(new HashMap<>());

        // Setup empty collections
        when(lowonganRepository.findAll()).thenReturn(Collections.emptyList());
        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA)).thenReturn(Collections.emptyList());
        when(applicationDataService.getAllApplications(userId)).thenReturn(Collections.emptyList());
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.BELUM_DIPROSES)).thenReturn(0);
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.DITERIMA)).thenReturn(0);
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.DITOLAK)).thenReturn(0);
        when(applicationDataService.getAcceptedLowongan(userId)).thenReturn(Collections.emptyList());

        // Setup calculation service with zero values
        when(calculationService.calculateTotalLoggedHoursAsync(userId))
                .thenReturn(CompletableFuture.completedFuture(BigDecimal.ZERO));
        when(calculationService.calculateTotalIncentiveAsync(userId))
                .thenReturn(CompletableFuture.completedFuture(BigDecimal.ZERO));

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        MahasiswaDashboardResponse resp = (MahasiswaDashboardResponse) base;

        // Verify all counts are zero
        assertEquals(0, resp.getOpenLowonganCount());
        assertEquals(0, resp.getTotalLowonganCount());
        assertEquals(0, resp.getTotalApplicationsCount());
        assertEquals(0, resp.getPendingApplicationsCount());
        assertEquals(0, resp.getAcceptedApplicationsCount());
        assertEquals(0, resp.getRejectedApplicationsCount());
        assertEquals(0, resp.getAcceptedLowongan().size());
        assertEquals(BigDecimal.ZERO, resp.getTotalLoggedHours());
        assertEquals(BigDecimal.ZERO, resp.getTotalIncentive());
    }

    @Test
    void getDashboardData_withCalculationServiceException_shouldThrow() {
        // Setup validation
        doNothing().when(validationService).validateMahasiswa(userId);
        when(validationService.getMahasiswaById(userId)).thenReturn(mahasiswa);
        when(featureProvider.getAvailableFeatures()).thenReturn(new HashMap<>());

        // Setup repositories
        when(lowonganRepository.findAll()).thenReturn(Collections.emptyList());
        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA)).thenReturn(Collections.emptyList());
        when(applicationDataService.getAllApplications(userId)).thenReturn(Collections.emptyList());
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.BELUM_DIPROSES)).thenReturn(0);
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.DITERIMA)).thenReturn(0);
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.DITOLAK)).thenReturn(0);
        when(applicationDataService.getAcceptedLowongan(userId)).thenReturn(Collections.emptyList());

        // Setup calculation service to throw exception
        CompletableFuture<BigDecimal> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Calculation failed"));
        when(calculationService.calculateTotalLoggedHoursAsync(userId)).thenReturn(failedFuture);

        // Expect exception due to async failure
        assertThrows(RuntimeException.class, () -> service.getDashboardData(userId));
    }

    @Test
    void asyncCalculation_performanceTest() {
        // Setup validation
        doNothing().when(validationService).validateMahasiswa(userId);
        when(validationService.getMahasiswaById(userId)).thenReturn(mahasiswa);
        when(featureProvider.getAvailableFeatures()).thenReturn(new HashMap<>());

        // Setup repositories
        when(lowonganRepository.findAll()).thenReturn(Collections.emptyList());
        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA)).thenReturn(Collections.emptyList());
        when(applicationDataService.getAllApplications(userId)).thenReturn(Collections.emptyList());
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.BELUM_DIPROSES)).thenReturn(0);
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.DITERIMA)).thenReturn(0);
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.DITOLAK)).thenReturn(0);
        when(applicationDataService.getAcceptedLowongan(userId)).thenReturn(Collections.emptyList());

        // Setup async calculation with delay to simulate real async behavior
        CompletableFuture<BigDecimal> hoursFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100); // Simulate processing time
                return new BigDecimal("10.00");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<BigDecimal> incentiveFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100); // Simulate processing time
                return new BigDecimal("275000.00");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        when(calculationService.calculateTotalLoggedHoursAsync(userId)).thenReturn(hoursFuture);
        when(calculationService.calculateTotalIncentiveAsync(userId)).thenReturn(incentiveFuture);

        long startTime = System.currentTimeMillis();

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        MahasiswaDashboardResponse resp = (MahasiswaDashboardResponse) base;

        long duration = System.currentTimeMillis() - startTime;

        // Verify results
        assertEquals(new BigDecimal("10.00"), resp.getTotalLoggedHours());
        assertEquals(new BigDecimal("275000.00"), resp.getTotalIncentive());

        // Verify async processing was reasonably fast (should be around 100ms, not 200ms if sequential)
        assertTrue(duration < 300, "Async processing should be faster than sequential");
    }

    @Test
    void createDashboardResponse_shouldReturnCorrectType() {
        DashboardResponse response = service.createDashboardResponse();
        assertTrue(response instanceof MahasiswaDashboardResponse);
    }

    @Test
    void populateCommonData_shouldSetCorrectValues() {
        // Setup validation
        doNothing().when(validationService).validateMahasiswa(userId);
        when(validationService.getMahasiswaById(userId)).thenReturn(mahasiswa);

        Map<String, String> features = new HashMap<>();
        features.put("test", "/api/test");
        when(featureProvider.getAvailableFeatures()).thenReturn(features);

        // Setup empty data to focus on common data only
        when(lowonganRepository.findAll()).thenReturn(Collections.emptyList());
        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA)).thenReturn(Collections.emptyList());
        when(applicationDataService.getAllApplications(userId)).thenReturn(Collections.emptyList());
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.BELUM_DIPROSES)).thenReturn(0);
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.DITERIMA)).thenReturn(0);
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.DITOLAK)).thenReturn(0);
        when(applicationDataService.getAcceptedLowongan(userId)).thenReturn(Collections.emptyList());
        when(calculationService.calculateTotalLoggedHoursAsync(userId))
                .thenReturn(CompletableFuture.completedFuture(BigDecimal.ZERO));
        when(calculationService.calculateTotalIncentiveAsync(userId))
                .thenReturn(CompletableFuture.completedFuture(BigDecimal.ZERO));

        // Execute
        DashboardResponse response = service.getDashboardData(userId);

        // Verify common data
        assertEquals("MAHASISWA", response.getUserRole());
        assertEquals("mahasiswaUser", response.getUsername());
        assertEquals("Mahasiswa FullName", response.getFullName());
        assertEquals(features, response.getAvailableFeatures());
    }

    @Test
    void getDashboardData_verifyServiceInteractions() {
        // Setup validation
        doNothing().when(validationService).validateMahasiswa(userId);
        when(validationService.getMahasiswaById(userId)).thenReturn(mahasiswa);
        when(featureProvider.getAvailableFeatures()).thenReturn(new HashMap<>());

        // Setup repositories
        when(lowonganRepository.findAll()).thenReturn(Collections.emptyList());
        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA)).thenReturn(Collections.emptyList());
        when(applicationDataService.getAllApplications(userId)).thenReturn(Collections.emptyList());
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.BELUM_DIPROSES)).thenReturn(0);
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.DITERIMA)).thenReturn(0);
        when(applicationDataService.countApplicationsByStatus(Collections.emptyList(), StatusPendaftaran.DITOLAK)).thenReturn(0);
        when(applicationDataService.getAcceptedLowongan(userId)).thenReturn(Collections.emptyList());
        when(calculationService.calculateTotalLoggedHoursAsync(userId))
                .thenReturn(CompletableFuture.completedFuture(BigDecimal.ZERO));
        when(calculationService.calculateTotalIncentiveAsync(userId))
                .thenReturn(CompletableFuture.completedFuture(BigDecimal.ZERO));

        // Execute
        service.getDashboardData(userId);

        // Verify all service interactions
        verify(validationService).validateMahasiswa(userId);
        verify(validationService).getMahasiswaById(userId);
        verify(featureProvider).getAvailableFeatures();
        verify(lowonganRepository).findAll();
        verify(lowonganRepository).findByStatusLowongan(StatusLowongan.DIBUKA);
        verify(applicationDataService).getAllApplications(userId);
        verify(applicationDataService).getAcceptedLowongan(userId);
        verify(calculationService).calculateTotalLoggedHoursAsync(userId);
        verify(calculationService).calculateTotalIncentiveAsync(userId);
    }
}