package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.validation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompositeApplicationValidator implements ApplicationValidator {

    private final List<ApplicationValidator> validators;

    @Autowired
    public CompositeApplicationValidator(List<ApplicationValidator> validators) {
        this.validators = validators.stream()
                .filter(validator -> !(validator instanceof CompositeApplicationValidator))
                .toList();
    }

    @Override
    public void validate(UUID lowonganId, DaftarForm form, Mahasiswa mahasiswa) {
        for (ApplicationValidator validator : validators) {
            validator.validate(lowonganId, form, mahasiswa);
        }
    }
}