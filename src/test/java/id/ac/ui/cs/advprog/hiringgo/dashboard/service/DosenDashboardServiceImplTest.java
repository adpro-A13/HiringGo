package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DosenDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganResponse;
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
import java.util.stream.Collectors;

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

    @BeforeEach
    void setup() {
        // Only initialize userId, don't create any mocks or stubbing here
        userId = UUID.randomUUID();
    }

    @Test
    void getDashboardData_happyPath() {
        // Setup dosen mock specifically for this test
        Dosen dosen = mock(Dosen.class);
        // REMOVED: when(dosen.getId()).thenReturn(userId); - This was causing UnnecessaryStubbingException
        when(dosen.getUsername()).thenReturn("dosenUser");
        when(dosen.getFullName()).thenReturn("Dosen FullName");

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
        assertTrue(base instanceof DosenDashboardResponse);
        DosenDashboardResponse resp = (DosenDashboardResponse) base;

        // common data
        assertEquals("DOSEN", resp.getUserRole());
        assertEquals("dosenUser", resp.getUsername());
        assertEquals("Dosen FullName", resp.getFullName());
        Map<String,String> feats = resp.getAvailableFeatures();
        assertEquals(4, feats.size());
        assertEquals("/api/manajemenlowongan", feats.get("manajemenlowongan"));

        // role specific
        assertEquals(2, resp.getCourseCount());
        assertSame(dtos, resp.getCourses());

        // accepted assistants across all positions: 1 + 2 + 0 = 3
        assertEquals(3, resp.getAcceptedAssistantCount());

        // open positions: (3-1)+(5-0)=2+5=7
        assertEquals(7, resp.getOpenPositionCount());

        // open positions list: should include low1 and low3
        Set<UUID> ids = resp.getOpenPositions().stream()
                .map(LowonganResponse::getLowonganId)
                .collect(Collectors.toSet());
        assertTrue(ids.contains(low1.getLowonganId()));
        assertTrue(ids.contains(low3.getLowonganId()));
    }

    @Test
    void getDashboardData_userNotFound_shouldThrow() {
        // Only setup what's needed for this specific test
        when(userRepository.existsById(userId)).thenReturn(false);

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains(userId.toString()));
    }

    @Test
    void getDashboardData_notADosen_shouldThrow() {
        // Only setup what's needed for this specific test
        User regularUser = mock(User.class);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(regularUser));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("bukan dosen"));
    }
}