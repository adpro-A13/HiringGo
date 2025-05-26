package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.validation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.ApplicationStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DuplicateApplicationValidator implements ApplicationValidator {

    private final ApplicationStatusService applicationStatusService;

    @Autowired
    public DuplicateApplicationValidator(ApplicationStatusService applicationStatusService) {
        this.applicationStatusService = applicationStatusService;
    }

    @Override
    public void validate(UUID lowonganId, DaftarForm form, Mahasiswa mahasiswa) {
        if (applicationStatusService.hasUserAlreadyApplied(lowonganId, mahasiswa)) {
            throw new IllegalArgumentException("Anda sudah mendaftar untuk lowongan ini");
        }
    }
}