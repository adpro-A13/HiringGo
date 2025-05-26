package id.ac.ui.cs.advprog.hiringgo.dashboard.service.data;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DosenCourseDataServiceTest {

    @Mock
    private MataKuliahRepository mataKuliahRepository;

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private MataKuliahMapper mataKuliahMapper;

    @Mock
    private LowonganMapper lowonganMapper;

    @InjectMocks
    private DosenCourseDataService service;

    private Dosen dosen;
    private List<MataKuliah> courses;

    @BeforeEach
    void setup() {
        dosen = mock(Dosen.class);
        courses = createCourses();
    }

    private List<MataKuliah> createCourses() {
        MataKuliah course1 = new MataKuliah("CS101", "Programming", "Basic programming");
        MataKuliah course2 = new MataKuliah("CS102", "Data Structure", "Advanced data structures");
        return Arrays.asList(course1, course2);
    }

    @Test
    void constructor_shouldInitializeCorrectly() {
        DosenCourseDataService newService = new DosenCourseDataService(
                mataKuliahRepository, lowonganRepository, mataKuliahMapper, lowonganMapper);
        assertNotNull(newService);
    }

    @Test
    void getCoursesTaughtByDosen_withValidDosen_shouldReturnCourses() {
        when(mataKuliahRepository.findByDosenPengampu(dosen)).thenReturn(courses);

        List<MataKuliah> result = service.getCoursesTaughtByDosen(dosen);

        assertEquals(2, result.size());
        verify(mataKuliahRepository).findByDosenPengampu(dosen);
    }

    @Test
    void getCoursesTaughtByDosen_withException_shouldReturnEmptyList() {
        when(mataKuliahRepository.findByDosenPengampu(dosen)).thenThrow(new RuntimeException("DB Error"));

        List<MataKuliah> result = service.getCoursesTaughtByDosen(dosen);

        assertTrue(result.isEmpty());
        verify(mataKuliahRepository).findByDosenPengampu(dosen);
    }

    @Test
    void convertCoursesToDTO_withValidCourses_shouldReturnDTOs() {
        MataKuliahDTO dto1 = mock(MataKuliahDTO.class);
        MataKuliahDTO dto2 = mock(MataKuliahDTO.class);
        when(mataKuliahMapper.toDto(courses.get(0))).thenReturn(dto1);
        when(mataKuliahMapper.toDto(courses.get(1))).thenReturn(dto2);

        List<MataKuliahDTO> result = service.convertCoursesToDTO(courses);

        assertEquals(2, result.size());
        verify(mataKuliahMapper, times(2)).toDto(any());
    }

    @Test
    void convertCoursesToDTO_withEmptyList_shouldReturnEmptyList() {
        List<MataKuliahDTO> result = service.convertCoursesToDTO(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void getLowonganForCourse_withValidCourse_shouldReturnLowonganDTOs() {
        MataKuliah course = courses.get(0);
        Lowongan lowongan1 = mock(Lowongan.class);
        Lowongan lowongan2 = mock(Lowongan.class);
        List<Lowongan> lowonganList = Arrays.asList(lowongan1, lowongan2);

        LowonganDTO dto1 = mock(LowonganDTO.class);
        LowonganDTO dto2 = mock(LowonganDTO.class);

        when(lowonganRepository.findByMataKuliah(course)).thenReturn(lowonganList);
        when(lowonganMapper.toDto(lowongan1)).thenReturn(dto1);
        when(lowonganMapper.toDto(lowongan2)).thenReturn(dto2);

        List<LowonganDTO> result = service.getLowonganForCourse(course);

        assertEquals(2, result.size());
        verify(lowonganRepository).findByMataKuliah(course);
        verify(lowonganMapper, times(2)).toDto(any());
    }

    @Test
    void getLowonganForCourse_withNullLowongan_shouldReturnEmptyList() {
        MataKuliah course = courses.get(0);
        when(lowonganRepository.findByMataKuliah(course)).thenReturn(null);

        List<LowonganDTO> result = service.getLowonganForCourse(course);

        assertTrue(result.isEmpty());
        verify(lowonganRepository).findByMataKuliah(course);
    }

    @Test
    void getLowonganForCourse_withEmptyLowongan_shouldReturnEmptyList() {
        MataKuliah course = courses.get(0);
        when(lowonganRepository.findByMataKuliah(course)).thenReturn(Collections.emptyList());

        List<LowonganDTO> result = service.getLowonganForCourse(course);

        assertTrue(result.isEmpty());
        verify(lowonganRepository).findByMataKuliah(course);
    }

    @Test
    void getLowonganForCourse_withException_shouldReturnEmptyList() {
        MataKuliah course = courses.get(0);
        when(lowonganRepository.findByMataKuliah(course)).thenThrow(new RuntimeException("DB Error"));

        List<LowonganDTO> result = service.getLowonganForCourse(course);

        assertTrue(result.isEmpty());
        verify(lowonganRepository).findByMataKuliah(course);
    }

    @Test
    void mapLowonganToCoursesAsync_withValidCourses_shouldReturnMap() throws ExecutionException, InterruptedException {
        Lowongan lowongan1 = mock(Lowongan.class);
        Lowongan lowongan2 = mock(Lowongan.class);
        LowonganDTO dto1 = mock(LowonganDTO.class);
        LowonganDTO dto2 = mock(LowonganDTO.class);

        when(lowonganRepository.findByMataKuliah(courses.get(0))).thenReturn(Arrays.asList(lowongan1));
        when(lowonganRepository.findByMataKuliah(courses.get(1))).thenReturn(Arrays.asList(lowongan2));
        when(lowonganMapper.toDto(lowongan1)).thenReturn(dto1);
        when(lowonganMapper.toDto(lowongan2)).thenReturn(dto2);

        CompletableFuture<Map<String, List<LowonganDTO>>> future = service.mapLowonganToCoursesAsync(courses);
        Map<String, List<LowonganDTO>> result = future.get();

        assertEquals(2, result.size());
        assertTrue(result.containsKey("CS101"));
        assertTrue(result.containsKey("CS102"));
        assertEquals(1, result.get("CS101").size());
        assertEquals(1, result.get("CS102").size());
    }

    @Test
    void mapLowonganToCoursesAsync_withEmptyLowongan_shouldReturnEmptyMap() throws ExecutionException, InterruptedException {
        when(lowonganRepository.findByMataKuliah(any())).thenReturn(Collections.emptyList());

        CompletableFuture<Map<String, List<LowonganDTO>>> future = service.mapLowonganToCoursesAsync(courses);
        Map<String, List<LowonganDTO>> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void mapLowonganToCoursesAsync_withEmptyCourseList_shouldReturnEmptyMap() throws ExecutionException, InterruptedException {
        CompletableFuture<Map<String, List<LowonganDTO>>> future = service.mapLowonganToCoursesAsync(Collections.emptyList());
        Map<String, List<LowonganDTO>> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void mapLowonganToCoursesAsync_withException_shouldNotAddToResult() throws ExecutionException, InterruptedException {
        // First course succeeds, second course fails
        Lowongan lowongan1 = mock(Lowongan.class);
        LowonganDTO dto1 = mock(LowonganDTO.class);

        when(lowonganRepository.findByMataKuliah(courses.get(0))).thenReturn(Arrays.asList(lowongan1));
        when(lowonganRepository.findByMataKuliah(courses.get(1))).thenThrow(new RuntimeException("DB Error"));
        when(lowonganMapper.toDto(lowongan1)).thenReturn(dto1);

        CompletableFuture<Map<String, List<LowonganDTO>>> future = service.mapLowonganToCoursesAsync(courses);
        Map<String, List<LowonganDTO>> result = future.get();

        // Only the successful course should be in the result
        assertEquals(1, result.size());
        assertTrue(result.containsKey("CS101"));
        assertFalse(result.containsKey("CS102"));
    }
}