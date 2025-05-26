package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
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
class ApplicationStatusServiceTest {

    @Mock
    private PendaftaranRepository pendaftaranRepository;

    @InjectMocks
    private ApplicationStatusService applicationStatusService;

    private Mahasiswa testMahasiswa;
    private Lowongan testLowongan;
    private Pendaftaran testPendaftaran;
    private UUID lowonganId;
    private UUID mahasiswaId;
    private UUID pendaftaranId;

    @BeforeEach
    void setUp() {
        // Setup IDs
        lowonganId = UUID.randomUUID();
        mahasiswaId = UUID.randomUUID();
        pendaftaranId = UUID.randomUUID();

        // Setup Mahasiswa
        testMahasiswa = new Mahasiswa();
        testMahasiswa.setId(mahasiswaId);
        testMahasiswa.setFullName("Test Mahasiswa");
        testMahasiswa.setUsername("test@ui.ac.id");

        // Setup Lowongan
        testLowongan = new Lowongan();
        testLowongan.setLowonganId(lowonganId);
        // Remove the problematic line - use a valid setter or no setter if not needed

        // Setup Pendaftaran
        testPendaftaran = new Pendaftaran();
        testPendaftaran.setPendaftaranId(pendaftaranId);
        testPendaftaran.setKandidat(testMahasiswa);
        testPendaftaran.setLowongan(testLowongan);
        testPendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);
    }

    @Test
    void testGetApplicationStatus_userHasNotApplied_returnsCorrectStatus() {
        // Given
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(Collections.emptyList());

        // When
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, testMahasiswa);

        // Then
        assertNotNull(result);
        assertEquals(false, result.get("hasApplied"));
        assertEquals("BELUM_DAFTAR", result.get("status"));
        assertFalse(result.containsKey("pendaftaranId"));

        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testGetApplicationStatus_userHasApplied_returnsCorrectStatus() {
        // Given
        List<Pendaftaran> pendaftaranList = Arrays.asList(testPendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // When
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, testMahasiswa);

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("hasApplied"));
        assertEquals("BELUM_DIPROSES", result.get("status"));
        assertEquals(pendaftaranId, result.get("pendaftaranId"));

        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testGetApplicationStatus_userAppliedWithDiterimaStatus_returnsCorrectStatus() {
        // Given
        testPendaftaran.setStatus(StatusPendaftaran.DITERIMA);
        List<Pendaftaran> pendaftaranList = Arrays.asList(testPendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // When
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, testMahasiswa);

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("hasApplied"));
        assertEquals("DITERIMA", result.get("status"));
        assertEquals(pendaftaranId, result.get("pendaftaranId"));
    }

    @Test
    void testGetApplicationStatus_userAppliedWithDitolakStatus_returnsCorrectStatus() {
        // Given
        testPendaftaran.setStatus(StatusPendaftaran.DITOLAK);
        List<Pendaftaran> pendaftaranList = Arrays.asList(testPendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // When
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, testMahasiswa);

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("hasApplied"));
        assertEquals("DITOLAK", result.get("status"));
        assertEquals(pendaftaranId, result.get("pendaftaranId"));
    }

    @Test
    void testHasUserAlreadyApplied_userHasNotApplied_returnsFalse() {
        // Given
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(Collections.emptyList());

        // When
        boolean result = applicationStatusService.hasUserAlreadyApplied(lowonganId, testMahasiswa);

        // Then
        assertFalse(result);
        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testHasUserAlreadyApplied_userHasApplied_returnsTrue() {
        // Given
        List<Pendaftaran> pendaftaranList = Arrays.asList(testPendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // When
        boolean result = applicationStatusService.hasUserAlreadyApplied(lowonganId, testMahasiswa);

        // Then
        assertTrue(result);
        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testHasUserAlreadyApplied_multipleApplications_returnsTrue() {
        // Given
        Pendaftaran secondPendaftaran = new Pendaftaran();
        secondPendaftaran.setPendaftaranId(UUID.randomUUID());
        secondPendaftaran.setKandidat(testMahasiswa);
        secondPendaftaran.setLowongan(testLowongan);
        secondPendaftaran.setStatus(StatusPendaftaran.DITERIMA);

        List<Pendaftaran> pendaftaranList = Arrays.asList(testPendaftaran, secondPendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // When
        boolean result = applicationStatusService.hasUserAlreadyApplied(lowonganId, testMahasiswa);

        // Then
        assertTrue(result);
        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testGetApplicationStatus_multipleApplications_returnsFirstApplication() {
        // Given
        Pendaftaran secondPendaftaran = new Pendaftaran();
        secondPendaftaran.setPendaftaranId(UUID.randomUUID());
        secondPendaftaran.setKandidat(testMahasiswa);
        secondPendaftaran.setLowongan(testLowongan);
        secondPendaftaran.setStatus(StatusPendaftaran.DITERIMA);

        List<Pendaftaran> pendaftaranList = Arrays.asList(testPendaftaran, secondPendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // When
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, testMahasiswa);

        // Then
        assertNotNull(result);
        assertEquals(true, result.get("hasApplied"));
        assertEquals("BELUM_DIPROSES", result.get("status")); // Should return first application's status
        assertEquals(pendaftaranId, result.get("pendaftaranId")); // Should return first application's ID
    }
}