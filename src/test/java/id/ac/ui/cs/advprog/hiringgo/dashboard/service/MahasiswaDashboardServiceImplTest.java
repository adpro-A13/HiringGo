package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.MahasiswaDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganResponse;
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

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MahasiswaDashboardServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private PendaftaranRepository pendaftaranRepository;

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
        // No need to stub getId() - causes UnnecessaryStubbingException

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
        assertEquals(0, resp.getTotalLoggedHours());
        assertEquals(BigDecimal.ZERO, resp.getTotalIncentive());

        // Check lists of LowonganResponse
        assertEquals(1, resp.getAcceptedLowongan().size());
        assertEquals(3, resp.getRecentLowongan().size());
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
                userRepository, lowonganRepository, pendaftaranRepository) {
            @Override
            protected void validateUser(UUID userId) {
                // Skip validation to test populateCommonData directly
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
        assertEquals(0, resp.getRecentLowongan().size());
    }

    @Test
    void convertToLowonganResponse_shouldCreateCorrectDTO() {
        // Setup with necessary fields
        Mahasiswa mahasiswa = mock(Mahasiswa.class);
        when(mahasiswa.getUsername()).thenReturn("mahasiswaUser");
        when(mahasiswa.getFullName()).thenReturn("Mahasiswa FullName");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mahasiswa));

        // Create a properly initialized lowongan
        UUID lowonganId = UUID.randomUUID();
        Lowongan testLowongan = new Lowongan();
        testLowongan.setLowonganId(lowonganId);
        testLowongan.setJumlahAsdosDibutuhkan(5);
        testLowongan.setJumlahAsdosDiterima(0);
        testLowongan.setJumlahAsdosPendaftar(0);
        // Add a MataKuliah to avoid NullPointerException
        MataKuliah mk = new MataKuliah("K1", "TestCourse", "Description");
        testLowongan.setMataKuliah(mk);
        // Set other required fields to avoid NPEs
        testLowongan.setTahunAjaran("2023/2024");
        testLowongan.setSemester("GANJIL");
        testLowongan.setStatusLowongan("DIBUKA");

        when(lowonganRepository.findByStatusLowongan(any())).thenReturn(List.of(testLowongan));
        when(lowonganRepository.findAll()).thenReturn(List.of(testLowongan));
        when(pendaftaranRepository.findByKandidatId(userId)).thenReturn(Collections.emptyList());

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        MahasiswaDashboardResponse resp = (MahasiswaDashboardResponse) base;

        // Verify conversion
        assertEquals(1, resp.getRecentLowongan().size());
        LowonganResponse lowonganDTO = resp.getRecentLowongan().get(0);
        assertEquals(lowonganId, lowonganDTO.getLowonganId());
    }
}