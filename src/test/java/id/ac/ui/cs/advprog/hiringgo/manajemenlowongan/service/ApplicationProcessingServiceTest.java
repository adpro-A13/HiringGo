package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.validation.CompositeApplicationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationProcessingServiceTest {

    @Mock
    private CompositeApplicationValidator validator;

    @Mock
    private PendaftaranService pendaftaranService;

    @InjectMocks
    private ApplicationProcessingService applicationProcessingService;

    private UUID lowonganId;
    private DaftarForm daftarForm;
    private Mahasiswa mahasiswa;
    private Pendaftaran expectedPendaftaran;

    @BeforeEach
    void setUp() {
        lowonganId = UUID.randomUUID();

        daftarForm = new DaftarForm();
        daftarForm.setIpk(3.75);
        daftarForm.setSks(20);

        mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername("mahasiswa@test.com");

        expectedPendaftaran = new Pendaftaran();
        expectedPendaftaran.setPendaftaranId(UUID.randomUUID());
        expectedPendaftaran.setKandidat(mahasiswa);
    }

    @Test
    void testProcessApplicationSuccess() {
        // Arrange
        doNothing().when(validator).validate(lowonganId, daftarForm, mahasiswa);
        when(pendaftaranService.daftar(
                lowonganId,
                mahasiswa,
                BigDecimal.valueOf(3.75),
                20
        )).thenReturn(expectedPendaftaran);

        // Act
        Pendaftaran result = applicationProcessingService.processApplication(lowonganId, daftarForm, mahasiswa);

        // Assert
        assertNotNull(result);
        assertEquals(expectedPendaftaran, result);

        // Verify interactions
        verify(validator).validate(lowonganId, daftarForm, mahasiswa);
        verify(pendaftaranService).daftar(
                lowonganId,
                mahasiswa,
                BigDecimal.valueOf(3.75),
                20
        );
    }

    @Test
    void testProcessApplicationValidationFailure() {
        // Arrange
        doThrow(new IllegalArgumentException("Validation failed"))
                .when(validator).validate(lowonganId, daftarForm, mahasiswa);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            applicationProcessingService.processApplication(lowonganId, daftarForm, mahasiswa);
        });

        assertEquals("Validation failed", exception.getMessage());

        // Verify validator was called but pendaftaranService was not
        verify(validator).validate(lowonganId, daftarForm, mahasiswa);
        verify(pendaftaranService, never()).daftar(any(), any(), any(), anyInt());
    }

    @Test
    void testProcessApplicationSubmissionFailure() {
        // Arrange
        doNothing().when(validator).validate(lowonganId, daftarForm, mahasiswa);
        when(pendaftaranService.daftar(
                lowonganId,
                mahasiswa,
                BigDecimal.valueOf(3.75),
                20
        )).thenThrow(new RuntimeException("Submission failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationProcessingService.processApplication(lowonganId, daftarForm, mahasiswa);
        });

        assertEquals("Submission failed", exception.getMessage());

        // Verify both validator and pendaftaranService were called
        verify(validator).validate(lowonganId, daftarForm, mahasiswa);
        verify(pendaftaranService).daftar(
                lowonganId,
                mahasiswa,
                BigDecimal.valueOf(3.75),
                20
        );
    }
}