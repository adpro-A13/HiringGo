package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.validation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompositeApplicationValidatorTest {

    @Mock
    private ApplicationValidator validator1;

    @Mock
    private ApplicationValidator validator2;

    @Mock
    private CompositeApplicationValidator anotherCompositeValidator;

    private CompositeApplicationValidator compositeApplicationValidator;

    private UUID lowonganId;
    private DaftarForm daftarForm;
    private Mahasiswa mahasiswa;

    @BeforeEach
    void setUp() {
        lowonganId = UUID.randomUUID();
        daftarForm = new DaftarForm();
        mahasiswa = new Mahasiswa();
    }

    @Test
    void testValidateWithMultipleValidators() {
        // Arrange
        List<ApplicationValidator> validators = Arrays.asList(validator1, validator2);
        compositeApplicationValidator = new CompositeApplicationValidator(validators);

        doNothing().when(validator1).validate(lowonganId, daftarForm, mahasiswa);
        doNothing().when(validator2).validate(lowonganId, daftarForm, mahasiswa);

        // Act
        compositeApplicationValidator.validate(lowonganId, daftarForm, mahasiswa);

        // Assert
        verify(validator1).validate(lowonganId, daftarForm, mahasiswa);
        verify(validator2).validate(lowonganId, daftarForm, mahasiswa);
    }

    @Test
    void testValidateWithCompositeValidatorFiltered() {
        // Arrange - includes another CompositeApplicationValidator which should be filtered out
        List<ApplicationValidator> validators = Arrays.asList(validator1, anotherCompositeValidator, validator2);
        compositeApplicationValidator = new CompositeApplicationValidator(validators);

        doNothing().when(validator1).validate(lowonganId, daftarForm, mahasiswa);
        doNothing().when(validator2).validate(lowonganId, daftarForm, mahasiswa);

        // Act
        compositeApplicationValidator.validate(lowonganId, daftarForm, mahasiswa);

        // Assert
        verify(validator1).validate(lowonganId, daftarForm, mahasiswa);
        verify(validator2).validate(lowonganId, daftarForm, mahasiswa);
        verify(anotherCompositeValidator, never()).validate(any(), any(), any());
    }

    @Test
    void testValidateWhenValidatorThrowsException() {
        // Arrange
        List<ApplicationValidator> validators = Arrays.asList(validator1, validator2);
        compositeApplicationValidator = new CompositeApplicationValidator(validators);

        doThrow(new IllegalArgumentException("Validation failed"))
                .when(validator1).validate(lowonganId, daftarForm, mahasiswa);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            compositeApplicationValidator.validate(lowonganId, daftarForm, mahasiswa);
        });

        assertEquals("Validation failed", exception.getMessage());
        verify(validator1).validate(lowonganId, daftarForm, mahasiswa);
        verify(validator2, never()).validate(any(), any(), any());
    }
}