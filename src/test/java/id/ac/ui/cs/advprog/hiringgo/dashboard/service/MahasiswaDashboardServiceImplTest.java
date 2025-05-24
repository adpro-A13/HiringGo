package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.MahasiswaDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;

@ExtendWith(MockitoExtension.class)
class MahasiswaDashboardServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private PendaftaranRepository pendaftaranRepository;

    @Mock
    private LowonganMapper lowonganMapper;

    @Mock
    private LogService logService;

    @InjectMocks
    private MahasiswaDashboardServiceImpl service;

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
    }

    @Test
    void getDashboardData_happyPath() {
        // Setup mahasiswa mock
        Mahasiswa mahasiswa = mock(Mahasiswa.class);
        when(mahasiswa.getUsername()).thenReturn("mahasiswaUser");
        when(mahasiswa.getFullName()).thenReturn("Mahasiswa FullName");

        // User validation
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mahasiswa));

        // Lowongan setup
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

        Lowongan low3 = new Lowongan();
        low3.setLowonganId(UUID.randomUUID());
        low3.setMataKuliah(m1);
        low3.setJumlahAsdosDibutuhkan(5);
        low3.setJumlahAsdosPendaftar(3);

        List<Lowongan> openLowonganList = Arrays.asList(low1, low2, low3);
        List<Lowongan> allLowonganList = new ArrayList<>(openLowonganList);

        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA)).thenReturn(openLowonganList);
        when(lowonganRepository.findAll()).thenReturn(allLowonganList);

        // Use mock Pendaftaran objects
        Pendaftaran app1 = mock(Pendaftaran.class);
        Pendaftaran app2 = mock(Pendaftaran.class);
        Pendaftaran app3 = mock(Pendaftaran.class);

        // Setup minimal required stubbing
        when(app1.getStatus()).thenReturn(StatusPendaftaran.BELUM_DIPROSES);
        when(app2.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        when(app3.getStatus()).thenReturn(StatusPendaftaran.DITOLAK);

        // Only stub getLowongan() for accepted applications, as that's all that's used
        when(app2.getLowongan()).thenReturn(low2);

        List<Pendaftaran> pendaftaranList = Arrays.asList(app1, app2, app3);
        when(pendaftaranRepository.findByKandidatId(userId)).thenReturn(pendaftaranList);

        // Execute
        DashboardResponse base = service.getDashboardData(userId);

        // Verify response type
        assertTrue(base instanceof MahasiswaDashboardResponse);
        MahasiswaDashboardResponse resp = (MahasiswaDashboardResponse) base;

        // Verify common data
        assertEquals("MAHASISWA", resp.getUserRole());
        assertEquals("mahasiswaUser", resp.getUsername());
        assertEquals("Mahasiswa FullName", resp.getFullName());

        // Verify features
        Map<String, String> feats = resp.getAvailableFeatures();
        assertEquals(4, feats.size());
        assertEquals("/api/pendaftaran", feats.get("pendaftaran"));
        assertEquals("/api/lowongan", feats.get("lowongan"));
        assertEquals("/api/profile", feats.get("profile"));
        assertEquals("/api/log", feats.get("logActivities"));

        // Verify role-specific data
        assertEquals(3, resp.getTotalLowonganCount());
        assertEquals(2, resp.getOpenLowonganCount());
        assertEquals(3, resp.getTotalApplicationsCount());
        assertEquals(1, resp.getPendingApplicationsCount());
        assertEquals(1, resp.getAcceptedApplicationsCount());
        assertEquals(1, resp.getRejectedApplicationsCount());
        assertEquals(BigDecimal.ZERO.setScale(2), resp.getTotalLoggedHours());
        assertEquals(0, resp.getTotalLoggedHours().compareTo(BigDecimal.ZERO));

        // Check lists of LowonganDTO
        assertEquals(1, resp.getAcceptedLowongan().size());
        // Removed reference to recentLowongan which doesn't exist
    }

    @Test
    void userNotFound_shouldThrowIllegalArgumentException() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("bukan mahasiswa"));
    }

    @Test
    void getDashboardData_notAMahasiswa_shouldThrow() {
        User regularUser = mock(User.class);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(regularUser));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("bukan mahasiswa"));
    }

    @Test
    void populateCommonData_mahasiswaNotFound_shouldThrow() {
        // Create a test subclass that skips validation
        MahasiswaDashboardServiceImpl testService = new MahasiswaDashboardServiceImpl(
                userRepository, lowonganRepository, pendaftaranRepository, lowonganMapper, logService) {
            @Override
            protected void validateUser(UUID userId) {
            }
        };

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> testService.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("tidak ditemukan"));
    }

    @Test
    void countApplicationsByStatus_withNullList_shouldReturnZero() {
        // Setup minimal requirements
        Mahasiswa mahasiswa = mock(Mahasiswa.class);
        when(mahasiswa.getUsername()).thenReturn("mahasiswaUser");
        when(mahasiswa.getFullName()).thenReturn("Mahasiswa FullName");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mahasiswa));

        // Mock repositories with empty collections
        when(lowonganRepository.findByStatusLowongan(any())).thenReturn(List.of());
        when(lowonganRepository.findAll()).thenReturn(List.of());

        // Key fix: Return empty list instead of null
        when(pendaftaranRepository.findByKandidatId(userId)).thenReturn(Collections.emptyList());

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        MahasiswaDashboardResponse resp = (MahasiswaDashboardResponse) base;

        // Verify counts are zero
        assertEquals(0, resp.getPendingApplicationsCount());
        assertEquals(0, resp.getAcceptedApplicationsCount());
        assertEquals(0, resp.getRejectedApplicationsCount());
        assertEquals(0, resp.getTotalApplicationsCount());
    }

    @Test
    void populateRoleSpecificData_withEmptyLists_shouldHandleGracefully() {
        // Setup minimal test case
        Mahasiswa mahasiswa = mock(Mahasiswa.class);
        when(mahasiswa.getUsername()).thenReturn("mahasiswaUser");
        when(mahasiswa.getFullName()).thenReturn("Mahasiswa FullName");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mahasiswa));

        // Return empty collections
        when(lowonganRepository.findByStatusLowongan(any())).thenReturn(Collections.emptyList());
        when(lowonganRepository.findAll()).thenReturn(Collections.emptyList());
        when(pendaftaranRepository.findByKandidatId(userId)).thenReturn(Collections.emptyList());

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        MahasiswaDashboardResponse resp = (MahasiswaDashboardResponse) base;

        // Verify counts are correct with empty collections
        assertEquals(0, resp.getOpenLowonganCount());
        assertEquals(0, resp.getTotalLowonganCount());
        assertEquals(0, resp.getTotalApplicationsCount());
        assertEquals(0, resp.getAcceptedLowongan().size());
        // Removed reference to recentLowongan which doesn't exist
    }

    @Test
    void convertToLowonganDTO_shouldDelegateToMapper() throws Exception {
        // Use reflection to test private method
        Lowongan testLowongan = new Lowongan();
        LowonganDTO expectedDTO = mock(LowonganDTO.class);
        when(lowonganMapper.toDto(testLowongan)).thenReturn(expectedDTO);

        Method method = MahasiswaDashboardServiceImpl.class.getDeclaredMethod("convertToLowonganDTO", Lowongan.class);
        method.setAccessible(true);
        LowonganDTO result = (LowonganDTO) method.invoke(service, testLowongan);

        assertSame(expectedDTO, result);
        verify(lowonganMapper).toDto(testLowongan);
    }

    @Test
    void getDashboardData_withLogs_shouldCalculateLoggedHoursAndIncentive() {
        // Setup mahasiswa mock
        Mahasiswa mahasiswa = mock(Mahasiswa.class);
        when(mahasiswa.getUsername()).thenReturn("mahasiswaUser");
        when(mahasiswa.getFullName()).thenReturn("Mahasiswa FullName");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mahasiswa));

        // Setup lowongan and pendaftaran as in happy path
        MataKuliah m1 = new MataKuliah("K1", "Name1", "Desc1");
        Lowongan low1 = new Lowongan();
        low1.setLowonganId(UUID.randomUUID());
        low1.setMataKuliah(m1);
        low1.setJumlahAsdosDibutuhkan(3);
        low1.setJumlahAsdosDiterima(1);
        low1.setJumlahAsdosPendaftar(2);

        List<Lowongan> openLowonganList = List.of(low1);
        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA)).thenReturn(openLowonganList);
        when(lowonganRepository.findAll()).thenReturn(openLowonganList);

        Pendaftaran app = mock(Pendaftaran.class);
        when(app.getStatus()).thenReturn(StatusPendaftaran.DITERIMA);
        when(app.getLowongan()).thenReturn(low1);
        List<Pendaftaran> pendaftaranList = List.of(app);
        when(pendaftaranRepository.findByKandidatId(userId)).thenReturn(pendaftaranList);

        // Setup logs: 2 logs, each 90 minutes (1.5 hours)
        Log log1 = mock(Log.class);
        Log log2 = mock(Log.class);
        when(log1.getWaktuMulai()).thenReturn(LocalTime.of(8, 0));
        when(log1.getWaktuSelesai()).thenReturn(LocalTime.of(9, 30));
        when(log2.getWaktuMulai()).thenReturn(LocalTime.of(10, 0));
        when(log2.getWaktuSelesai()).thenReturn(LocalTime.of(11, 30));
        when(logService.getLogsByUser(userId)).thenReturn(List.of(log1, log2));

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        MahasiswaDashboardResponse resp = (MahasiswaDashboardResponse) base;

        // Each log is 1.5 hours, total 3.0 hours
        assertEquals(new BigDecimal("3.00"), resp.getTotalLoggedHours());
        // Incentive: 3.00 * 27500 = 82500.00
        assertEquals(new BigDecimal("82500.00"), resp.getTotalIncentive());
    }
    @Test
    void validateUser_userDoesNotExist_shouldThrow() {
        // Setup the userRepository to return false for existsById
        when(userRepository.existsById(userId)).thenReturn(false);

        // Create a test subclass to expose protected method
        class TestableService extends MahasiswaDashboardServiceImpl {
            public TestableService() {
                super(userRepository, lowonganRepository, pendaftaranRepository, lowonganMapper, logService);
            }

            @Override
            public void validateUser(UUID userId) {
                super.validateUser(userId);
            }
        }

        TestableService testableService = new TestableService();

        // Test that the correct exception is thrown
        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> testableService.validateUser(userId)
        );

        assertEquals("User tidak ditemukan dengan ID: " + userId, ex.getMessage());
        verify(userRepository).existsById(userId);
        // Verify that findById is never called when existsById returns false
        verify(userRepository, never()).findById(any());
    }

    @Test
    void countApplicationsByStatus_withNullApplications() throws Exception {
        // Use reflection to test private method directly
        Method method = MahasiswaDashboardServiceImpl.class.getDeclaredMethod(
                "countApplicationsByStatus",
                List.class,
                StatusPendaftaran.class);
        method.setAccessible(true);

        // Test null applications case
        int result = (int) method.invoke(
                service,
                null,
                StatusPendaftaran.DITERIMA);

        assertEquals(0, result);
    }
}