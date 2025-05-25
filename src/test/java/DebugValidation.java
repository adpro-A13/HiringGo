import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.DosenDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;

public class DebugValidation {
    public static void main(String[] args) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        
        DosenDto dosenDto = new DosenDto();
        dosenDto.setEmail("test@example.com");
        dosenDto.setFullName("Test Name");
        dosenDto.setNip("12345");
        // Note: password is null
        
        Set<ConstraintViolation<DosenDto>> violations = validator.validate(dosenDto);
        
        System.out.println("Number of violations: " + violations.size());
        for (ConstraintViolation<DosenDto> violation : violations) {
            System.out.println("Violation: " + violation.getMessage());
        }
    }
}
