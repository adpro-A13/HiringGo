package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.AdminDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
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
class AdminDashboardServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MataKuliahRepository mataKuliahRepository;

    @Mock
    private LowonganRepository lowonganRepository;

    @InjectMocks
    private AdminDashboardServiceImpl service;

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
    }

    @Test
    void getDashboardData_happyPath() {
        // Setup admin mock
        Admin admin = mock(Admin.class);
        when(admin.getUsername()).thenReturn("adminUser");

        // User validation
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));

        // Setup user repository to return lists of users
        List<User> allUsers = new ArrayList<>();
        allUsers.add(admin);

        // Add some dosens and mahasiswas to the list
        Dosen dosen1 = mock(Dosen.class);
        Dosen dosen2 = mock(Dosen.class);
        allUsers.add(dosen1);
        allUsers.add(dosen2);

        Mahasiswa mhs1 = mock(Mahasiswa.class);
        Mahasiswa mhs2 = mock(Mahasiswa.class);
        Mahasiswa mhs3 = mock(Mahasiswa.class);
        allUsers.add(mhs1);
        allUsers.add(mhs2);
        allUsers.add(mhs3);

        when(userRepository.findAll()).thenReturn(allUsers);

        // Setup repository counts
        when(mataKuliahRepository.count()).thenReturn(10L);
        when(lowonganRepository.count()).thenReturn(5L);

        // Execute
        DashboardResponse base = service.getDashboardData(userId);

        // Verify response type
        assertTrue(base instanceof AdminDashboardResponse);
        AdminDashboardResponse resp = (AdminDashboardResponse) base;

        // Verify common data
        assertEquals("ADMIN", resp.getUserRole());
        assertEquals("adminUser", resp.getUsername());
        assertEquals("adminUser", resp.getFullName()); // For Admin, username is used as fullName

        // Verify features
        Map<String, String> feats = resp.getAvailableFeatures();
        assertEquals(4, feats.size());
        assertEquals("/api/admin/accounts", feats.get("manajemenAkun"));
        assertEquals("/api/admin/matakuliah", feats.get("manajemenMataKuliah"));
        assertEquals("/api/admin/lowongan", feats.get("manajemenLowongan"));
        assertEquals("/api/profile", feats.get("profile"));

        // Verify role-specific data
        assertEquals(2, resp.getDosenCount());
        assertEquals(3, resp.getMahasiswaCount());
        assertEquals(10, resp.getCourseCount());
        assertEquals(5, resp.getLowonganCount());
    }

    @Test
    void getDashboardData_withRealAdmin_shouldReturnCorrectData() {
        // Create a real Admin instance
        Admin admin = new Admin("admin@example.com", "password");

        // User validation
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));

        // Return empty list/zero counts for simplicity
        when(userRepository.findAll()).thenReturn(Collections.singletonList(admin));
        when(mataKuliahRepository.count()).thenReturn(0L);
        when(lowonganRepository.count()).thenReturn(0L);

        // Execute
        DashboardResponse base = service.getDashboardData(userId);

        // Verify basic data
        assertTrue(base instanceof AdminDashboardResponse);
        AdminDashboardResponse resp = (AdminDashboardResponse) base;
        assertEquals("ADMIN", resp.getUserRole());
        assertEquals("admin@example.com", resp.getUsername());
    }

    @Test
    void getDashboardData_userNotFound_shouldThrow() {
        when(userRepository.existsById(userId)).thenReturn(false);

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains(userId.toString()));
    }

    @Test
    void getDashboardData_notAnAdmin_shouldThrow() {
        User regularUser = mock(User.class);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(regularUser));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("bukan admin"));
    }

    @Test
    void populateRoleSpecificData_withEmptyRepository_shouldReturnZeroCounts() {
        // Setup admin mock
        Admin admin = mock(Admin.class);
        when(admin.getUsername()).thenReturn("adminUser");

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));

        // Return empty list for findAll
        when(userRepository.findAll()).thenReturn(Collections.singletonList(admin));

        // Return zero for counts
        when(mataKuliahRepository.count()).thenReturn(0L);
        when(lowonganRepository.count()).thenReturn(0L);

        // Execute
        DashboardResponse base = service.getDashboardData(userId);
        AdminDashboardResponse resp = (AdminDashboardResponse) base;

        // Verify counts are zero
        assertEquals(0, resp.getDosenCount());
        assertEquals(0, resp.getMahasiswaCount());
        assertEquals(0, resp.getCourseCount());
        assertEquals(0, resp.getLowonganCount());
    }
}