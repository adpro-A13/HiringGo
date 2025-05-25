package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.validation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.ApplicationStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DuplicateApplicationValidatorTest {

    @Mock
    private ApplicationStatusService applicationStatusService;

    @InjectMocks
    private DuplicateApplicationValidator duplicateApplicationValidator;

    private UUID lowonganId;
    private DaftarForm daftarForm;
    private Mahasiswa mahasiswa;

    @BeforeEach
    void setUp() {
        lowonganId = UUID.randomUUID();
        daftarForm = new DaftarForm();
        mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
    }

    @Test
    void testValidateWhenUserHasNotApplied() {
        // Arrange
        when(applicationStatusService.hasUserAlreadyApplied(lowonganId, mahasiswa))
                .thenReturn(false);

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> {
            duplicateApplicationValidator.validate(lowonganId, daftarForm, mahasiswa);
        });

        verify(applicationStatusService).hasUserAlreadyApplied(lowonganId, mahasiswa);
    }

    @Test
    void testValidateWhenUserHasAlreadyApplied() {
        // Arrange
        when(applicationStatusService.hasUserAlreadyApplied(lowonganId, mahasiswa))
                .thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            duplicateApplicationValidator.validate(lowonganId, daftarForm, mahasiswa);
        });

        assertEquals("Anda sudah mendaftar untuk lowongan ini", exception.getMessage());
        verify(applicationStatusService).hasUserAlreadyApplied(lowonganId, mahasiswa);
    }
}