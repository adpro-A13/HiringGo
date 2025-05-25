package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.validation;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DaftarFormBusinessValidatorTest {

    @InjectMocks
    private DaftarFormBusinessValidator daftarFormBusinessValidator;

    private DaftarForm daftarForm;

    @BeforeEach
    void setUp() {
        daftarForm = new DaftarForm();
    }

    @Test
    void testValidateBusinessRulesSuccess() {
        // Arrange
        daftarForm.setIpk(3.5);
        daftarForm.setSks(18);

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> {
            daftarFormBusinessValidator.validateBusinessRules(daftarForm);
        });
    }

    @Test
    void testValidateBusinessRulesWithNullValues() {
        // Arrange
        daftarForm.setIpk(null);
        daftarForm.setSks(null);

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> {
            daftarFormBusinessValidator.validateBusinessRules(daftarForm);
        });
    }

    @Test
    void testValidateIpkTooLow() {
        // Arrange
        daftarForm.setIpk(2.0);
        daftarForm.setSks(18);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            daftarFormBusinessValidator.validateBusinessRules(daftarForm);
        });

        assertEquals("IPK minimal 2.5 untuk mendaftar lowongan", exception.getMessage());
    }

    @Test
    void testValidateSksTooLow() {
        // Arrange
        daftarForm.setIpk(3.0);
        daftarForm.setSks(10);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            daftarFormBusinessValidator.validateBusinessRules(daftarForm);
        });

        assertEquals("SKS minimal 12 untuk bisa mendaftar lowongan", exception.getMessage());
    }

    @Test
    void testValidateSksTooHigh() {
        // Arrange
        daftarForm.setIpk(3.0);
        daftarForm.setSks(25);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            daftarFormBusinessValidator.validateBusinessRules(daftarForm);
        });

        assertEquals("SKS maksimal 24 per semester", exception.getMessage());
    }
}