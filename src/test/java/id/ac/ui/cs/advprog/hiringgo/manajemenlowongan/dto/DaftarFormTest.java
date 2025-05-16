package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DaftarFormTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test DaftarForm valid data")
    void testValidDaftarForm() {
        DaftarForm form = new DaftarForm();
        form.setIpk(3.5);
        form.setSks(20);

        Set<ConstraintViolation<DaftarForm>> violations = validator.validate(form);
        assertTrue(violations.isEmpty(), "Form should not have validation errors");

        System.out.println("Expected IPK: 3.5");
        System.out.println("Actual IPK: " + form.getIpk());

        assertTrue(form.getIpk() > 3.4 && form.getIpk() < 3.6,
                "IPK should be approximately 3.5 but was " + form.getIpk());

        assertEquals(20, form.getSks(), "SKS value mismatch");
    }

    @Test
    @DisplayName("Test DaftarForm IPK validation - too high")
    void testIpkTooHigh() {
        DaftarForm form = new DaftarForm();
        form.setIpk(5.0); // IPK should be <= 4.0
        form.setSks(100);

        Set<ConstraintViolation<DaftarForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty(), "IPK > 4.0 should trigger validation error");
    }

    @Test
    @DisplayName("Test DaftarForm IPK validation - negative")
    void testIpkNegative() {
        DaftarForm form = new DaftarForm();
        form.setIpk(-1.0); // IPK should be >= 0
        form.setSks(100);

        Set<ConstraintViolation<DaftarForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty(), "Negative IPK should trigger validation error");
    }

    @Test
    @DisplayName("Test DaftarForm SKS validation - negative")
    void testSksNegative() {
        DaftarForm form = new DaftarForm();
        form.setIpk(3.5);
        form.setSks(-10); // SKS should be >= 0

        Set<ConstraintViolation<DaftarForm>> violations = validator.validate(form);
        assertFalse(violations.isEmpty(), "Negative SKS should trigger validation error");
    }
}