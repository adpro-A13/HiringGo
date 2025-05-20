package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DosenDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
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

    @Mock private UserRepository userRepository;
    @Mock private MataKuliahRepository mataKuliahRepository;
    @Mock private LowonganRepository lowonganRepository;
    @Mock private MataKuliahMapper mataKuliahMapper;
    @Mock private LowonganMapper lowonganMapper;

    @InjectMocks
    private DosenDashboardServiceImpl service;

    private UUID userId;
    private Dosen dosenMock;
    private MataKuliah m1;
    private MataKuliah m2;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        dosenMock = mock(Dosen.class); // Create mock here
        // Stubbings for dosenMock will be moved to specific tests or made lenient if truly global

        m1 = new MataKuliah("K1","Name1","Desc1");
        m2 = new MataKuliah("K2","Name2","Desc2");
    }

    @Test
    void getDashboardData_happyPath() {
        // Moved dosenMock stubbings here
        when(dosenMock.getUsername()).thenReturn("dosenUser");
        when(dosenMock.getFullName()).thenReturn("Dosen FullName");

        // validation
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));

        // stub courses
        List<MataKuliah> courses = Arrays.asList(m1, m2);
        when(mataKuliahRepository.findByDosenPengampu(dosenMock)).thenReturn(courses);
        List<MataKuliahDTO> dtos = Arrays.asList(mock(MataKuliahDTO.class), mock(MataKuliahDTO.class));
        when(mataKuliahMapper.toDtoList(courses)).thenReturn(dtos);

        // stub lowongan
        Lowongan low1 = new Lowongan(); low1.setLowonganId(UUID.randomUUID()); low1.setMataKuliah(m1);
        low1.setJumlahAsdosDibutuhkan(3); low1.setJumlahAsdosDiterima(1); // 2 open
        Lowongan low2 = new Lowongan(); low2.setLowonganId(UUID.randomUUID()); low2.setMataKuliah(m1);
        low2.setJumlahAsdosDibutuhkan(2); low2.setJumlahAsdosDiterima(2); // 0 open
        Lowongan low3 = new Lowongan(); low3.setLowonganId(UUID.randomUUID()); low3.setMataKuliah(m2);
        low3.setJumlahAsdosDibutuhkan(5); low3.setJumlahAsdosDiterima(0); // 5 open

        List<Lowongan> allDosenLowonganOpenStatus = Arrays.asList(low1, low2, low3);
        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA)).thenReturn(allDosenLowonganOpenStatus);

        LowonganDTO dtoLow1 = mock(LowonganDTO.class);
        when(dtoLow1.getLowonganId()).thenReturn(low1.getLowonganId());
        when(lowonganMapper.toDto(low1)).thenReturn(dtoLow1);

        LowonganDTO dtoLow3 = mock(LowonganDTO.class);
        when(dtoLow3.getLowonganId()).thenReturn(low3.getLowonganId());
        when(lowonganMapper.toDto(low3)).thenReturn(dtoLow3);

        // execute
        DashboardResponse base = service.getDashboardData(userId);
        assertTrue(base instanceof DosenDashboardResponse);
        DosenDashboardResponse resp = (DosenDashboardResponse) base;

        // verify common data
        assertEquals("DOSEN", resp.getUserRole());
        assertEquals("dosenUser", resp.getUsername());
        assertEquals("Dosen FullName", resp.getFullName());

        Map<String, String> feats = resp.getAvailableFeatures();
        assertNotNull(feats);
        assertEquals(4, feats.size());
        assertEquals("/api/manajemenlowongan", feats.get("manajemenlowongan"));
        assertEquals("/api/asdos", feats.get("manajemenAsdos"));
        assertEquals("/api/profile", feats.get("profile"));
        assertEquals("/api/log", feats.get("periksaLog"));

        // verify courses
        assertEquals(2, resp.getCourseCount());
        assertSame(dtos, resp.getCourses());

        assertEquals(3, resp.getAcceptedAssistantCount());
        assertEquals(7, resp.getOpenPositionCount());

        List<LowonganDTO> returnedDtos = resp.getOpenPositions();
        assertNotNull(returnedDtos);
        assertEquals(2, returnedDtos.size());
        Set<UUID> returnedDtoIds = returnedDtos.stream()
                .map(LowonganDTO::getLowonganId)
                .collect(Collectors.toSet());
        assertTrue(returnedDtoIds.contains(low1.getLowonganId()));
        assertTrue(returnedDtoIds.contains(low3.getLowonganId()));

        verify(lowonganMapper).toDto(low1);
        verify(lowonganMapper).toDto(low3);
        verify(lowonganMapper, never()).toDto(low2);
    }

    @Test
    void getDashboardData_userNotFound_shouldThrow() {
        when(userRepository.existsById(userId)).thenReturn(false); // This makes validateUser throw
        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("User tidak ditemukan dengan ID: " + userId));
    }

    @Test
    void getDashboardData_notADosen_shouldThrow() {
        User regularUser = mock(User.class);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(regularUser)); // This makes validateUser throw

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDashboardData(userId)
        );
        assertTrue(ex.getMessage().contains("User dengan ID: " + userId + " bukan dosen"));
    }

    @Test
    void populateCommonData_dosenNotFoundByIdInRepo_shouldThrowNoSuchElementException() {
        DosenDashboardServiceImpl testService = new DosenDashboardServiceImpl(
                userRepository, mataKuliahRepository, lowonganRepository, mataKuliahMapper, lowonganMapper) {
            @Override
            protected void validateUser(UUID userIdToValidate) {
                // Do nothing, bypass validation
            }
        };
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> testService.getDashboardData(userId)
        );
        assertEquals("Dosen tidak ditemukan dengan ID: " + userId, ex.getMessage());
    }

    @Test
    void populateCommonData_userFoundIsNotDosenInstance_shouldThrowNoSuchElementException() {
        DosenDashboardServiceImpl testService = new DosenDashboardServiceImpl(
                userRepository, mataKuliahRepository, lowonganRepository, mataKuliahMapper, lowonganMapper) {
            @Override
            protected void validateUser(UUID userIdToValidate) {
                // Bypass validation
            }
        };
        User nonDosenUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(nonDosenUser));

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> testService.getDashboardData(userId)
        );
        assertEquals("Dosen tidak ditemukan dengan ID: " + userId, ex.getMessage());
    }


    @Test
    void populateRoleSpecificData_filtersLowonganWithNullMataKuliah() {
        // Stubs for dosenMock needed if populateCommonData is called internally by getDashboardData
        when(dosenMock.getUsername()).thenReturn("dosenUser"); // Or use lenient()
        when(dosenMock.getFullName()).thenReturn("Dosen FullName"); // Or use lenient()

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));
        when(mataKuliahRepository.findByDosenPengampu(dosenMock)).thenReturn(Collections.singletonList(m1));
        when(mataKuliahMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

        Lowongan lowonganWithNullMk = new Lowongan();
        lowonganWithNullMk.setLowonganId(UUID.randomUUID());
        lowonganWithNullMk.setMataKuliah(null);
        lowonganWithNullMk.setJumlahAsdosDibutuhkan(5);
        lowonganWithNullMk.setJumlahAsdosDiterima(0);

        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA))
                .thenReturn(Collections.singletonList(lowonganWithNullMk));

        DosenDashboardResponse resp = (DosenDashboardResponse) service.getDashboardData(userId);

        assertTrue(resp.getOpenPositions().isEmpty(), "Open positions should be empty when lowongan's matakuliah is null");
        assertEquals(0, resp.getAcceptedAssistantCount(), "Accepted count should be 0");
        assertEquals(0, resp.getOpenPositionCount(), "Open position count should be 0");
        verify(lowonganMapper, never()).toDto(any(Lowongan.class));
    }

    @Test
    void populateRoleSpecificData_filtersLowonganNotInDosenCourses() {
        // Stubs for dosenMock needed if populateCommonData is called internally by getDashboardData
        when(dosenMock.getUsername()).thenReturn("dosenUser"); // Or use lenient()
        when(dosenMock.getFullName()).thenReturn("Dosen FullName"); // Or use lenient()

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));

        // m1 is a real object, its getKode() will return "K1"
        // when(m1.getKode()).thenReturn("K1"); // REMOVE THIS - m1 is not a mock

        List<MataKuliah> dosenCourses = Collections.singletonList(m1);
        when(mataKuliahRepository.findByDosenPengampu(dosenMock)).thenReturn(dosenCourses);
        when(mataKuliahMapper.toDtoList(dosenCourses)).thenReturn(Collections.singletonList(mock(MataKuliahDTO.class)));

        MataKuliah otherMk = mock(MataKuliah.class);
        when(otherMk.getKode()).thenReturn("K_OTHER"); // This is correct as otherMk is a mock

        Lowongan lowonganForOtherMk = new Lowongan();
        lowonganForOtherMk.setLowonganId(UUID.randomUUID());
        lowonganForOtherMk.setMataKuliah(otherMk);
        lowonganForOtherMk.setJumlahAsdosDibutuhkan(5);
        lowonganForOtherMk.setJumlahAsdosDiterima(0);

        when(lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA))
                .thenReturn(Collections.singletonList(lowonganForOtherMk));

        DosenDashboardResponse resp = (DosenDashboardResponse) service.getDashboardData(userId);

        assertTrue(resp.getOpenPositions().isEmpty(), "Open positions should be empty for lowongan of other courses");
        assertEquals(0, resp.getAcceptedAssistantCount(), "Accepted count should be 0 for lowongan of other courses");
        assertEquals(0, resp.getOpenPositionCount(), "Open position count should be 0 for lowongan of other courses");
        verify(lowonganMapper, never()).toDto(any(Lowongan.class));
    }
}