package id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminStatisticsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MataKuliahRepository mataKuliahRepository;

    @Mock
    private LowonganRepository lowonganRepository;

    @InjectMocks
    private AdminStatisticsService service;

    private Dosen dosen1, dosen2;
    private Mahasiswa mahasiswa1, mahasiswa2, mahasiswa3;
    private Admin admin1;
    private MataKuliah course1, course2;
    private Lowongan lowongan1, lowongan2;

    @BeforeEach
    void setup() {
        // Setup users - using correct constructor with 4 parameters: (username, password, fullName, nip/nim)
        dosen1 = new Dosen("dosen1@test.com", "password", "Dr. Dosen One", "12345");
        dosen2 = new Dosen("dosen2@test.com", "password", "Dr. Dosen Two", "12346");
        mahasiswa1 = new Mahasiswa("mhs1@test.com", "password", "Mahasiswa One", "2021001");
        mahasiswa2 = new Mahasiswa("mhs2@test.com", "password", "Mahasiswa Two", "2021002");
        mahasiswa3 = new Mahasiswa("mhs3@test.com", "password", "Mahasiswa Three", "2021003");
        admin1 = new Admin("admin@test.com", "password");

        // Setup courses
        course1 = new MataKuliah("CS101", "Programming", "Basic programming");
        course2 = new MataKuliah("CS102", "Data Structure", "Advanced data structures");

        // Setup lowongan
        lowongan1 = new Lowongan();
        lowongan1.setMataKuliah(course1);
        lowongan2 = new Lowongan();
        lowongan2.setMataKuliah(course2);
    }

    @Test
    void countDosen_withMultipleDosen_shouldReturnCorrectCount() {
        // Setup
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                dosen1, dosen2, mahasiswa1, mahasiswa2, admin1
        ));

        // Execute
        int result = service.countDosen();

        // Verify
        assertEquals(2, result);
        verify(userRepository).findAll();
    }

    @Test
    void countDosen_withNoDosen_shouldReturnZero() {
        // Setup
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                mahasiswa1, mahasiswa2, admin1
        ));

        // Execute
        int result = service.countDosen();

        // Verify
        assertEquals(0, result);
        verify(userRepository).findAll();
    }

    @Test
    void countMahasiswa_withMultipleMahasiswa_shouldReturnCorrectCount() {
        // Setup
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                dosen1, mahasiswa1, mahasiswa2, mahasiswa3, admin1
        ));

        // Execute
        int result = service.countMahasiswa();

        // Verify
        assertEquals(3, result);
        verify(userRepository).findAll();
    }

    @Test
    void countMahasiswa_withNoMahasiswa_shouldReturnZero() {
        // Setup
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                dosen1, dosen2, admin1
        ));

        // Execute
        int result = service.countMahasiswa();

        // Verify
        assertEquals(0, result);
        verify(userRepository).findAll();
    }

    @Test
    void countCourses_withMultipleCourses_shouldReturnCorrectCount() {
        // Setup
        when(mataKuliahRepository.count()).thenReturn(5L);

        // Execute
        int result = service.countCourses();

        // Verify
        assertEquals(5, result);
        verify(mataKuliahRepository).count();
    }

    @Test
    void countCourses_withNoCourses_shouldReturnZero() {
        // Setup
        when(mataKuliahRepository.count()).thenReturn(0L);

        // Execute
        int result = service.countCourses();

        // Verify
        assertEquals(0, result);
        verify(mataKuliahRepository).count();
    }

    @Test
    void countLowongan_withMultipleLowongan_shouldReturnCorrectCount() {
        // Setup
        when(lowonganRepository.count()).thenReturn(8L);

        // Execute
        int result = service.countLowongan();

        // Verify
        assertEquals(8, result);
        verify(lowonganRepository).count();
    }

    @Test
    void countLowongan_withNoLowongan_shouldReturnZero() {
        // Setup
        when(lowonganRepository.count()).thenReturn(0L);

        // Execute
        int result = service.countLowongan();

        // Verify
        assertEquals(0, result);
        verify(lowonganRepository).count();
    }

    @Test
    void countDosenAsync_shouldReturnCompletableFuture() throws ExecutionException, InterruptedException {
        // Setup
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                dosen1, dosen2, mahasiswa1
        ));

        // Execute
        CompletableFuture<Integer> future = service.countDosenAsync();

        // Verify
        assertNotNull(future);
        assertEquals(2, future.get());
        verify(userRepository).findAll();
    }

    @Test
    void countMahasiswaAsync_shouldReturnCompletableFuture() throws ExecutionException, InterruptedException {
        // Setup
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                dosen1, mahasiswa1, mahasiswa2
        ));

        // Execute
        CompletableFuture<Integer> future = service.countMahasiswaAsync();

        // Verify
        assertNotNull(future);
        assertEquals(2, future.get());
        verify(userRepository).findAll();
    }

    @Test
    void countCoursesAsync_shouldReturnCompletableFuture() throws ExecutionException, InterruptedException {
        // Setup
        when(mataKuliahRepository.count()).thenReturn(10L);

        // Execute
        CompletableFuture<Integer> future = service.countCoursesAsync();

        // Verify
        assertNotNull(future);
        assertEquals(10, future.get());
        verify(mataKuliahRepository).count();
    }

    @Test
    void countLowonganAsync_shouldReturnCompletableFuture() throws ExecutionException, InterruptedException {
        // Setup
        when(lowonganRepository.count()).thenReturn(15L);

        // Execute
        CompletableFuture<Integer> future = service.countLowonganAsync();

        // Verify
        assertNotNull(future);
        assertEquals(15, future.get());
        verify(lowonganRepository).count();
    }

    @Test
    void countDosenAsync_withRepositoryException_shouldThrowException() {
        // Setup
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Execute & Verify
        CompletableFuture<Integer> future = service.countDosenAsync();
        assertThrows(RuntimeException.class, future::join);
    }

    @Test
    void countMahasiswaAsync_withRepositoryException_shouldThrowException() {
        // Setup
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Execute & Verify
        CompletableFuture<Integer> future = service.countMahasiswaAsync();
        assertThrows(RuntimeException.class, future::join);
    }

    @Test
    void countCoursesAsync_withRepositoryException_shouldThrowException() {
        // Setup
        when(mataKuliahRepository.count()).thenThrow(new RuntimeException("Database error"));

        // Execute & Verify
        CompletableFuture<Integer> future = service.countCoursesAsync();
        assertThrows(RuntimeException.class, future::join);
    }

    @Test
    void countLowonganAsync_withRepositoryException_shouldThrowException() {
        // Setup
        when(lowonganRepository.count()).thenThrow(new RuntimeException("Database error"));

        // Execute & Verify
        CompletableFuture<Integer> future = service.countLowonganAsync();
        assertThrows(RuntimeException.class, future::join);
    }

    @Test
    void allAsyncMethods_performanceTest() throws ExecutionException, InterruptedException {
        // Setup
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                dosen1, dosen2, mahasiswa1, mahasiswa2, mahasiswa3
        ));
        when(mataKuliahRepository.count()).thenReturn(10L);
        when(lowonganRepository.count()).thenReturn(20L);

        long startTime = System.currentTimeMillis();

        // Execute all async methods
        CompletableFuture<Integer> dosenFuture = service.countDosenAsync();
        CompletableFuture<Integer> mahasiswaFuture = service.countMahasiswaAsync();
        CompletableFuture<Integer> coursesFuture = service.countCoursesAsync();
        CompletableFuture<Integer> lowonganFuture = service.countLowonganAsync();

        // Wait for all to complete
        CompletableFuture.allOf(dosenFuture, mahasiswaFuture, coursesFuture, lowonganFuture).get();

        long duration = System.currentTimeMillis() - startTime;

        // Verify results
        assertEquals(2, dosenFuture.get());
        assertEquals(3, mahasiswaFuture.get());
        assertEquals(10, coursesFuture.get());
        assertEquals(20, lowonganFuture.get());

        // Async should be reasonably fast
        assertTrue(duration < 1000, "Async operations should complete quickly");
    }

    @Test
    void countUsers_withEmptyRepository_shouldReturnZero() {
        // Setup
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Execute
        int dosenCount = service.countDosen();
        int mahasiswaCount = service.countMahasiswa();

        // Verify
        assertEquals(0, dosenCount);
        assertEquals(0, mahasiswaCount);
        verify(userRepository, times(2)).findAll();
    }

    @Test
    void countUsers_withMixedUserTypes_shouldFilterCorrectly() {
        // Setup - create users with different types
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                dosen1, dosen2,           // 2 dosen
                mahasiswa1, mahasiswa2,   // 2 mahasiswa
                admin1                    // 1 admin (should not be counted)
        ));

        // Execute
        int dosenCount = service.countDosen();
        int mahasiswaCount = service.countMahasiswa();

        // Verify
        assertEquals(2, dosenCount);
        assertEquals(2, mahasiswaCount);
        verify(userRepository, times(2)).findAll();
    }
}