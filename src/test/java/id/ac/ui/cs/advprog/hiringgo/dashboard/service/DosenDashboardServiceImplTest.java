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
import static org.mockito.Mockito.lenient;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.*;

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
    private MataKuliah mataKuliah1;
    private MataKuliah mataKuliah2;
    private Lowongan lowongan1;
    private Lowongan lowongan2;
    private LowonganDTO lowonganDTO1;
    private LowonganDTO lowonganDTO2;
    private MataKuliahDTO mataKuliahDTO1;
    private MataKuliahDTO mataKuliahDTO2;


    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        dosenMock = mock(Dosen.class);

        // Setup course data
        mataKuliah1 = new MataKuliah("CS101", "Advanced Programming", "Object-oriented programming");
        mataKuliah2 = new MataKuliah("CS102", "Data Structures", "Collection of data structures");

        // Setup lowongan data
        lowongan1 = new Lowongan();
        lowongan1.setLowonganId(UUID.randomUUID());
        lowongan1.setMataKuliah(mataKuliah1);
        lowongan1.setJumlahAsdosDibutuhkan(3);
        lowongan1.setJumlahAsdosDiterima(2);

        lowongan2 = new Lowongan();
        lowongan2.setLowonganId(UUID.randomUUID());
        lowongan2.setMataKuliah(mataKuliah2);
        lowongan2.setJumlahAsdosDibutuhkan(4);
        lowongan2.setJumlahAsdosDiterima(1);

        // Setup DTOs
        lowonganDTO1 = mock(LowonganDTO.class);
        lowonganDTO2 = mock(LowonganDTO.class);
        mataKuliahDTO1 = mock(MataKuliahDTO.class);
        mataKuliahDTO2 = mock(MataKuliahDTO.class);

        // Use lenient() for mocks that won't be used in every test
        lenient().when(dosenMock.getUsername()).thenReturn("dosen@example.com");
        lenient().when(dosenMock.getFullName()).thenReturn("Dr. Dosen");
        lenient().when(lowonganMapper.toDto(lowongan1)).thenReturn(lowonganDTO1);
        lenient().when(lowonganMapper.toDto(lowongan2)).thenReturn(lowonganDTO2);
        lenient().when(mataKuliahMapper.toDto(mataKuliah1)).thenReturn(mataKuliahDTO1);
        lenient().when(mataKuliahMapper.toDto(mataKuliah2)).thenReturn(mataKuliahDTO2);
    }

    @Test
    void getDashboardData_happyPath() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));
        List<MataKuliah> courses = Arrays.asList(mataKuliah1, mataKuliah2);
        when(mataKuliahRepository.findByDosenPengampu(dosenMock)).thenReturn(courses);

        when(lowonganRepository.findByMataKuliah(mataKuliah1)).thenReturn(Collections.singletonList(lowongan1));
        when(lowonganRepository.findByMataKuliah(mataKuliah2)).thenReturn(Collections.singletonList(lowongan2));

        // Act
        DashboardResponse response = service.getDashboardData(userId);

        // Assert
        assertTrue(response instanceof DosenDashboardResponse);
        DosenDashboardResponse dosenResponse = (DosenDashboardResponse) response;

        assertEquals("DOSEN", dosenResponse.getUserRole());
        assertEquals("dosen@example.com", dosenResponse.getUsername());
        assertEquals("Dr. Dosen", dosenResponse.getFullName());

        assertNotNull(dosenResponse.getAvailableFeatures());
        assertEquals(4, dosenResponse.getAvailableFeatures().size());

        // Check new functionality
        Map<String, Integer> assistantCounts = dosenResponse.getAcceptedAssistantsPerCourse();
        assertNotNull(assistantCounts);
        assertEquals(2, assistantCounts.size());
        assertEquals(Integer.valueOf(2), assistantCounts.get("CS101"));
        assertEquals(Integer.valueOf(1), assistantCounts.get("CS102"));
    }

    @Test
    void validateUser_withDosenUser_noException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));

        // Act & Assert
        assertDoesNotThrow(() -> service.validateUser(userId));
    }

    @Test
    void validateUser_withNonExistentUser_shouldThrow() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.validateUser(userId)
        );
        assertEquals("User tidak ditemukan dengan ID: " + userId, ex.getMessage());
    }

    @Test
    void validateUser_withNonDosenUser_shouldThrow() {
        // Arrange
        User regularUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(regularUser));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateUser(userId)
        );
        assertEquals("User dengan ID: " + userId + " bukan seorang Dosen", ex.getMessage());
    }

    @Test
    void createDashboardResponse_shouldReturnDosenResponse() {
        // Act
        DashboardResponse response = service.createDashboardResponse();

        // Assert
        assertTrue(response instanceof DosenDashboardResponse);
    }

    @Test
    void populateCommonData_shouldSetBaseFields() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));
        DashboardResponse response = new DosenDashboardResponse();

        // Act
        service.populateCommonData(userId, response);

        // Assert
        assertEquals("DOSEN", response.getUserRole());
        assertEquals("dosen@example.com", response.getUsername());
        assertEquals("Dr. Dosen", response.getFullName());

        Map<String, String> features = response.getAvailableFeatures();
        assertEquals(4, features.size());
        assertTrue(features.containsKey("manajemenlowongan"));
        assertTrue(features.containsKey("manajemenAsdos"));
        assertTrue(features.containsKey("profile"));
        assertTrue(features.containsKey("periksaLog"));
    }

    @Test
    void testConvertToLowonganDTO() {
        // Create a test lowongan
        Lowongan testLowongan = new Lowongan();
        testLowongan.setLowonganId(UUID.randomUUID());
        testLowongan.setMataKuliah(mataKuliah1);
        testLowongan.setJumlahAsdosDibutuhkan(3);
        testLowongan.setJumlahAsdosDiterima(2);

        // Create expected DTO
        LowonganDTO expectedDTO = mock(LowonganDTO.class);

        // Setup mock response
        when(lowonganMapper.toDto(testLowongan)).thenReturn(expectedDTO);

        // Call the method directly (via reflection since it's private)
        LowonganDTO result = null;
        try {
            Method method = DosenDashboardServiceImpl.class.getDeclaredMethod("convertToLowonganDTO", Lowongan.class);
            method.setAccessible(true);
            result = (LowonganDTO) method.invoke(service, testLowongan);
        } catch (Exception e) {
            fail("Failed to invoke private method: " + e.getMessage());
        }

        // Verify results
        assertNotNull(result);
        assertSame(expectedDTO, result);
        verify(lowonganMapper).toDto(testLowongan);
    }

    @Test
    void populateCommonData_withNonExistentUser_shouldThrow() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        DashboardResponse response = new DosenDashboardResponse();

        // Act & Assert
        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.populateCommonData(userId, response)
        );
        assertEquals("Dosen tidak ditemukan dengan ID: " + userId, ex.getMessage());
    }

    @Test
    void populateRoleSpecificData_withEmptyCoursesList() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));
        when(mataKuliahRepository.findByDosenPengampu(dosenMock)).thenReturn(Collections.emptyList());

        DosenDashboardResponse response = new DosenDashboardResponse();

        // Act
        service.populateRoleSpecificData(userId, response);

        // Assert
        assertTrue(response.getCoursesTaught().isEmpty());
        assertTrue(response.getLowonganPerCourse().isEmpty());
        assertTrue(response.getAcceptedAssistantsPerCourse().isEmpty());
    }

    @Test
    void populateRoleSpecificData_withNormalData() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));
        List<MataKuliah> courses = Arrays.asList(mataKuliah1, mataKuliah2);
        when(mataKuliahRepository.findByDosenPengampu(dosenMock)).thenReturn(courses);

        when(lowonganRepository.findByMataKuliah(mataKuliah1)).thenReturn(Collections.singletonList(lowongan1));
        when(lowonganRepository.findByMataKuliah(mataKuliah2)).thenReturn(Collections.singletonList(lowongan2));

        DosenDashboardResponse response = new DosenDashboardResponse();

        // Act
        service.populateRoleSpecificData(userId, response);

        // Assert
        assertEquals(2, response.getCoursesTaught().size());
        assertEquals(2, response.getLowonganPerCourse().size());
        assertEquals(2, response.getAcceptedAssistantsPerCourse().size());
        assertEquals(Integer.valueOf(2), response.getAcceptedAssistantsPerCourse().get("CS101"));
        assertEquals(Integer.valueOf(1), response.getAcceptedAssistantsPerCourse().get("CS102"));
    }

    @Test
    void populateRoleSpecificData_withRepositoryException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));
        when(mataKuliahRepository.findByDosenPengampu(dosenMock)).thenThrow(new RuntimeException("Database error"));

        DosenDashboardResponse response = new DosenDashboardResponse();

        // Act
        service.populateRoleSpecificData(userId, response);

        // Assert - should handle the exception gracefully and set empty data
        assertTrue(response.getCoursesTaught().isEmpty());
        assertTrue(response.getLowonganPerCourse().isEmpty());
        assertTrue(response.getAcceptedAssistantsPerCourse().isEmpty());
    }

    @Test
    void populateRoleSpecificData_withNullLowonganList() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));
        List<MataKuliah> courses = Arrays.asList(mataKuliah1, mataKuliah2);
        when(mataKuliahRepository.findByDosenPengampu(dosenMock)).thenReturn(courses);

        // First course has null lowongan list
        when(lowonganRepository.findByMataKuliah(mataKuliah1)).thenReturn(null);
        // Second course has normal lowongan list
        when(lowonganRepository.findByMataKuliah(mataKuliah2)).thenReturn(Collections.singletonList(lowongan2));

        DosenDashboardResponse response = new DosenDashboardResponse();

        // Act
        service.populateRoleSpecificData(userId, response);

        // Assert
        assertEquals(2, response.getCoursesTaught().size());
        // Only second course should be in map (first one had null lowongan list)
        assertEquals(1, response.getLowonganPerCourse().size());
        assertFalse(response.getLowonganPerCourse().containsKey("CS101"));
        assertTrue(response.getLowonganPerCourse().containsKey("CS102"));

        // Both courses should be in the assistants count map
        assertEquals(2, response.getAcceptedAssistantsPerCourse().size());
        assertEquals(Integer.valueOf(0), response.getAcceptedAssistantsPerCourse().get("CS101"));
        assertEquals(Integer.valueOf(1), response.getAcceptedAssistantsPerCourse().get("CS102"));
    }

    @Test
    void populateRoleSpecificData_withEmptyLowonganList() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));
        List<MataKuliah> courses = Arrays.asList(mataKuliah1, mataKuliah2);
        when(mataKuliahRepository.findByDosenPengampu(dosenMock)).thenReturn(courses);

        // First course has empty lowongan list
        when(lowonganRepository.findByMataKuliah(mataKuliah1)).thenReturn(Collections.emptyList());
        // Second course has normal lowongan list
        when(lowonganRepository.findByMataKuliah(mataKuliah2)).thenReturn(Collections.singletonList(lowongan2));

        DosenDashboardResponse response = new DosenDashboardResponse();

        // Act
        service.populateRoleSpecificData(userId, response);

        // Assert
        assertEquals(2, response.getCoursesTaught().size());
        // Only second course should be in map (first one had empty lowongan list)
        assertEquals(1, response.getLowonganPerCourse().size());
        assertFalse(response.getLowonganPerCourse().containsKey("CS101"));
        assertTrue(response.getLowonganPerCourse().containsKey("CS102"));

        // Both courses should be in the assistants count map
        assertEquals(2, response.getAcceptedAssistantsPerCourse().size());
        assertEquals(Integer.valueOf(0), response.getAcceptedAssistantsPerCourse().get("CS101"));
        assertEquals(Integer.valueOf(1), response.getAcceptedAssistantsPerCourse().get("CS102"));
    }

    @Test
    void populateRoleSpecificData_withExceptionInLowonganMapping() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));
        List<MataKuliah> courses = Arrays.asList(mataKuliah1, mataKuliah2);
        when(mataKuliahRepository.findByDosenPengampu(dosenMock)).thenReturn(courses);

        // First course throws exception
        when(lowonganRepository.findByMataKuliah(mataKuliah1)).thenThrow(new RuntimeException("Error accessing lowongan"));
        // Second course has normal lowongan list
        when(lowonganRepository.findByMataKuliah(mataKuliah2)).thenReturn(Collections.singletonList(lowongan2));

        DosenDashboardResponse response = new DosenDashboardResponse();

        // Act
        service.populateRoleSpecificData(userId, response);

        // Assert - should handle the exception gracefully for first course
        assertEquals(2, response.getCoursesTaught().size());
        // Only second course should be in map (first one had exception)
        assertEquals(1, response.getLowonganPerCourse().size());
        assertFalse(response.getLowonganPerCourse().containsKey("CS101"));
        assertTrue(response.getLowonganPerCourse().containsKey("CS102"));

        // Both courses should be in the assistants count map
        assertEquals(2, response.getAcceptedAssistantsPerCourse().size());
        assertEquals(Integer.valueOf(0), response.getAcceptedAssistantsPerCourse().get("CS101"));
        assertEquals(Integer.valueOf(1), response.getAcceptedAssistantsPerCourse().get("CS102"));
    }

    @Test
    void populateRoleSpecificData_withMultipleLowonganForOneCourse() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));
        List<MataKuliah> courses = Collections.singletonList(mataKuliah1);
        when(mataKuliahRepository.findByDosenPengampu(dosenMock)).thenReturn(courses);

        // Create a second lowongan for same course
        Lowongan additionalLowongan = new Lowongan();
        additionalLowongan.setLowonganId(UUID.randomUUID());
        additionalLowongan.setMataKuliah(mataKuliah1);
        additionalLowongan.setJumlahAsdosDibutuhkan(2);
        additionalLowongan.setJumlahAsdosDiterima(1);

        // Course has multiple lowongan
        when(lowonganRepository.findByMataKuliah(mataKuliah1))
                .thenReturn(Arrays.asList(lowongan1, additionalLowongan));

        LowonganDTO additionalLowonganDTO = mock(LowonganDTO.class);
        when(lowonganMapper.toDto(additionalLowongan)).thenReturn(additionalLowonganDTO);

        DosenDashboardResponse response = new DosenDashboardResponse();

        // Act
        service.populateRoleSpecificData(userId, response);

        // Assert
        assertEquals(1, response.getCoursesTaught().size());
        assertEquals(1, response.getLowonganPerCourse().size());

        // Course should have both lowongan
        List<LowonganDTO> lowongans = response.getLowonganPerCourse().get("CS101");
        assertEquals(2, lowongans.size());

        // Total accepted assistants should be sum of both lowongan
        assertEquals(1, response.getAcceptedAssistantsPerCourse().size());
        assertEquals(Integer.valueOf(3), response.getAcceptedAssistantsPerCourse().get("CS101"));
    }

    @Test
    void populateRoleSpecificData_withDosenNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DosenDashboardResponse response = new DosenDashboardResponse();

        // Act & Assert
        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.populateRoleSpecificData(userId, response)
        );
        assertEquals("Dosen tidak ditemukan", ex.getMessage());
    }

    @Test
    void populateRoleSpecificData_withNonDosenUser() {
        // Arrange
        User regularUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(regularUser));

        DosenDashboardResponse response = new DosenDashboardResponse();

        // Act & Assert
        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.populateRoleSpecificData(userId, response)
        );
        assertEquals("Dosen tidak ditemukan", ex.getMessage());
    }
}