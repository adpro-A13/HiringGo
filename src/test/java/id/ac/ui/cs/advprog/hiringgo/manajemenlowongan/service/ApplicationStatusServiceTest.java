package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
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

    private Mahasiswa mahasiswa;
    private UUID mahasiswaId;
    private UUID lowonganId;
    private Pendaftaran pendaftaran;

    @BeforeEach
    void setUp() {
        mahasiswaId = UUID.randomUUID();
        lowonganId = UUID.randomUUID();

        mahasiswa = new Mahasiswa();
        mahasiswa.setId(mahasiswaId);
        mahasiswa.setUsername("mahasiswa@test.com");
        mahasiswa.setFullName("Test Mahasiswa");

        pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(UUID.randomUUID());
        pendaftaran.setKandidat(mahasiswa);
        pendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);
    }

    @Test
    void testGetApplicationStatusWhenUserHasNotApplied() {
        // Arrange
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, mahasiswa);

        // Assert
        assertFalse((Boolean) result.get("hasApplied"));
        assertEquals("BELUM_DAFTAR", result.get("status"));
        assertNull(result.get("pendaftaranId"));

        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testGetApplicationStatusWhenUserHasApplied() {
        // Arrange
        List<Pendaftaran> pendaftaranList = Arrays.asList(pendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // Act
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, mahasiswa);

        // Assert
        assertTrue((Boolean) result.get("hasApplied"));
        assertEquals("BELUM_DIPROSES", result.get("status"));
        assertEquals(pendaftaran.getPendaftaranId(), result.get("pendaftaranId"));

        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testGetApplicationStatusWithAcceptedApplication() {
        // Arrange
        pendaftaran.setStatus(StatusPendaftaran.DITERIMA);
        List<Pendaftaran> pendaftaranList = Arrays.asList(pendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // Act
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, mahasiswa);

        // Assert
        assertTrue((Boolean) result.get("hasApplied"));
        assertEquals("DITERIMA", result.get("status"));
        assertEquals(pendaftaran.getPendaftaranId(), result.get("pendaftaranId"));

        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testGetApplicationStatusWithRejectedApplication() {
        // Arrange
        pendaftaran.setStatus(StatusPendaftaran.DITOLAK);
        List<Pendaftaran> pendaftaranList = Arrays.asList(pendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // Act
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, mahasiswa);

        // Assert
        assertTrue((Boolean) result.get("hasApplied"));
        assertEquals("DITOLAK", result.get("status"));
        assertEquals(pendaftaran.getPendaftaranId(), result.get("pendaftaranId"));

        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testHasUserAlreadyAppliedReturnsFalse() {
        // Arrange
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(Collections.emptyList());

        // Act
        boolean result = applicationStatusService.hasUserAlreadyApplied(lowonganId, mahasiswa);

        // Assert
        assertFalse(result);
        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testHasUserAlreadyAppliedReturnsTrue() {
        // Arrange
        List<Pendaftaran> pendaftaranList = Arrays.asList(pendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // Act
        boolean result = applicationStatusService.hasUserAlreadyApplied(lowonganId, mahasiswa);

        // Assert
        assertTrue(result);
        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testGetApplicationStatusWithMultipleApplications() {
        // Arrange - Create multiple applications, should return the first one
        Pendaftaran secondPendaftaran = new Pendaftaran();
        secondPendaftaran.setPendaftaranId(UUID.randomUUID());
        secondPendaftaran.setKandidat(mahasiswa);
        secondPendaftaran.setStatus(StatusPendaftaran.DITERIMA);

        List<Pendaftaran> pendaftaranList = Arrays.asList(pendaftaran, secondPendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // Act
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, mahasiswa);

        // Assert
        assertTrue((Boolean) result.get("hasApplied"));
        assertEquals("BELUM_DIPROSES", result.get("status")); // Should return first application's status
        assertEquals(pendaftaran.getPendaftaranId(), result.get("pendaftaranId"));

        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testHasUserAlreadyAppliedWithMultipleApplications() {
        // Arrange
        Pendaftaran secondPendaftaran = new Pendaftaran();
        secondPendaftaran.setPendaftaranId(UUID.randomUUID());

        List<Pendaftaran> pendaftaranList = Arrays.asList(pendaftaran, secondPendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // Act
        boolean result = applicationStatusService.hasUserAlreadyApplied(lowonganId, mahasiswa);

        // Assert
        assertTrue(result);
        verify(pendaftaranRepository).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
    }

    @Test
    void testGetApplicationStatusVerifyRepositoryCall() {
        // Arrange
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(Collections.emptyList());

        // Act
        applicationStatusService.getApplicationStatus(lowonganId, mahasiswa);

        // Assert
        verify(pendaftaranRepository, times(1)).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
        verifyNoMoreInteractions(pendaftaranRepository);
    }

    @Test
    void testHasUserAlreadyAppliedVerifyRepositoryCall() {
        // Arrange
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(Collections.emptyList());

        // Act
        applicationStatusService.hasUserAlreadyApplied(lowonganId, mahasiswa);

        // Assert
        verify(pendaftaranRepository, times(1)).findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId);
        verifyNoMoreInteractions(pendaftaranRepository);
    }

    @Test
    void testBuildStatusDataWithEmptyList() {
        // Arrange
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, mahasiswa);

        // Assert
        assertEquals(2, result.size()); // Should contain hasApplied and status
        assertFalse((Boolean) result.get("hasApplied"));
        assertEquals("BELUM_DAFTAR", result.get("status"));
        assertFalse(result.containsKey("pendaftaranId")); // Should not contain pendaftaranId
    }

    @Test
    void testBuildStatusDataWithNonEmptyList() {
        // Arrange
        List<Pendaftaran> pendaftaranList = Arrays.asList(pendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(mahasiswaId, lowonganId))
                .thenReturn(pendaftaranList);

        // Act
        Map<String, Object> result = applicationStatusService.getApplicationStatus(lowonganId, mahasiswa);

        // Assert
        assertEquals(3, result.size()); // Should contain hasApplied, status, and pendaftaranId
        assertTrue((Boolean) result.get("hasApplied"));
        assertEquals("BELUM_DIPROSES", result.get("status"));
        assertEquals(pendaftaran.getPendaftaranId(), result.get("pendaftaranId"));
    }
}