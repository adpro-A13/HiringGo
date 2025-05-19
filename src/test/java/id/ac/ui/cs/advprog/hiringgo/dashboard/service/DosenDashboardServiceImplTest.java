package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DosenDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
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
class DosenDashboardServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MataKuliahRepository mataKuliahRepository;

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private MataKuliahMapper mataKuliahMapper;

    @InjectMocks
    private DosenDashboardServiceImpl service;

    private UUID userId;
    private Dosen dosen;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        dosen = mock(Dosen.class);
        when(dosen.getId()).thenReturn(userId);
        when(dosen.getUsername()).thenReturn("dosenUser");
        when(dosen.getFullName()).thenReturn("Dosen FullName");
    }

    @Test
    void getDashboardData_happyPath() {
        // validateUser
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosen));

        // courses
        MataKuliah m1 = new MataKuliah("K1","Name1","Desc1");
        MataKuliah m2 = new MataKuliah("K2","Name2","Desc2");
        List<MataKuliah> courses = Arrays.asList(m1, m2);
        when(mataKuliahRepository.findByDosenPengampu(dosen)).thenReturn(courses);
        MataKuliahDTO dto1 = mock(MataKuliahDTO.class);
        MataKuliahDTO dto2 = mock(MataKuliahDTO.class);
        List<MataKuliahDTO> dtos = Arrays.asList(dto1, dto2);
        when(mataKuliahMapper.toDtoList(courses)).thenReturn(dtos);

        // lowongan
        Lowongan low1 = new Lowongan();
        low1.setLowonganId(UUID.randomUUID());
        low1.setMataKuliah(m1);
        low1.setJumlahAsdosDibutuhkan(3);
        low1.setJumlahAsdosDiterima(1);

        Lowongan low2 = new Lowongan();
        low2.setLowonganId(UUID.randomUUID());
        low2.setMataKuliah(m1);
        low2.setJumlahAsdosDibutuhkan(2);
        low2.setJumlahAsdosDiterima(2);

        Lowongan low3 = new Lowongan();
        low3.setLowonganId(UUID.randomUUID());
        low3.setMataKuliah(m2);
        low3.setJumlahAsdosDibutuhkan(5);
        low3.setJumlahAsdosDiterima(0);

        List<Lowongan> allOpen = Arrays.asList(low1, low2, low3);
        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA)).thenReturn(allOpen);

        // execute
        DashboardResponse base = service.getDashboardData(userId);

        // Verify response type
        assertTrue(base instanceof DosenDashboardResponse);
        DosenDashboardResponse resp = (DosenDashboardResponse) base;

        // Verify common data
        assertEquals("DOSEN", resp.getUserRole());
        assertEquals("dosenUser", resp.getUsername());
        assertEquals("Dosen FullName", resp.getFullName());

        // Verify features
        Map<String,String> feats = resp.getAvailableFeatures();
        assertTrue(feats.size() > 0);
        assertTrue(feats.containsKey("manajemenlowongan"));

        // Verify role-specific data
        assertEquals(2, resp.getCourseCount());
        assertSame(dtos, resp.getCourses());

        // Verify repository interactions
        verify(lowonganRepository).findByStatusLowongan(StatusLowongan.DIBUKA);
        verify(mataKuliahRepository).findByDosenPengampu(dosen);
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
    void getDashboardData_notADosen_shouldThrow() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("bukan dosen"));
    }

    @Test
    void testDashboardResponseContainsCorrectUserRole() {
        // Set up minimum requirements for getDashboardData method
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosen));
        when(mataKuliahRepository.findByDosenPengampu(dosen)).thenReturn(Collections.emptyList());
        when(mataKuliahMapper.toDtoList(any())).thenReturn(Collections.emptyList());
        when(lowonganRepository.findByStatusLowongan(any())).thenReturn(Collections.emptyList());

        // Call the public method that uses protected getUserRole
        DashboardResponse response = service.getDashboardData(userId);

        // Verify the role is correctly set
        assertEquals("DOSEN", response.getUserRole());
    }

    @Test
    void testDashboardResponseContainsFeatures() {
        // Set up minimum requirements for getDashboardData method
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosen));
        when(mataKuliahRepository.findByDosenPengampu(dosen)).thenReturn(Collections.emptyList());
        when(mataKuliahMapper.toDtoList(any())).thenReturn(Collections.emptyList());
        when(lowonganRepository.findByStatusLowongan(any())).thenReturn(Collections.emptyList());

        // Call the public method that uses protected getAvailableFeatures
        DashboardResponse response = service.getDashboardData(userId);

        // Verify features are correctly set
        Map<String, String> features = response.getAvailableFeatures();
        assertNotNull(features);
        assertEquals("/api/manajemenlowongan", features.get("manajemenlowongan"));
        assertEquals("/api/asdos", features.get("manajemenAsdos"));
        assertEquals("/api/profile", features.get("profile"));
        assertEquals("/api/log", features.get("periksaLog"));
    }

    @Test
    void testDashboardResponseContainsUserFullName() {
        // Set up minimum requirements for getDashboardData method
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosen));
        when(mataKuliahRepository.findByDosenPengampu(dosen)).thenReturn(Collections.emptyList());
        when(mataKuliahMapper.toDtoList(any())).thenReturn(Collections.emptyList());
        when(lowonganRepository.findByStatusLowongan(any())).thenReturn(Collections.emptyList());

        // Call the public method
        DashboardResponse response = service.getDashboardData(userId);

        // Verify full name is correctly set
        assertEquals("Dosen FullName", response.getFullName());
        verify(userRepository, atLeastOnce()).findById(userId);
    }

    @Test
    void testDashboardResponseContainsUsername() {
        // Set up minimum requirements for getDashboardData method
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosen));
        when(mataKuliahRepository.findByDosenPengampu(dosen)).thenReturn(Collections.emptyList());
        when(mataKuliahMapper.toDtoList(any())).thenReturn(Collections.emptyList());
        when(lowonganRepository.findByStatusLowongan(any())).thenReturn(Collections.emptyList());

        // Call the public method
        DashboardResponse response = service.getDashboardData(userId);

        // Verify username is correctly set
        assertEquals("dosenUser", response.getUsername());
        verify(userRepository, atLeastOnce()).findById(userId);
    }
}