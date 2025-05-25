package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.validation.CompositeApplicationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ApplicationProcessingService {

    private final CompositeApplicationValidator validator;
    private final PendaftaranService pendaftaranService;

    @Autowired
    public ApplicationProcessingService(
            CompositeApplicationValidator validator,
            PendaftaranService pendaftaranService) {
        this.validator = validator;
        this.pendaftaranService = pendaftaranService;
    }

    public Pendaftaran processApplication(UUID lowonganId, DaftarForm form, Mahasiswa mahasiswa) {
        validateApplication(lowonganId, form, mahasiswa);
        return submitApplication(lowonganId, form, mahasiswa);
    }

    private void validateApplication(UUID lowonganId, DaftarForm form, Mahasiswa mahasiswa) {
        validator.validate(lowonganId, form, mahasiswa);
    }

    private Pendaftaran submitApplication(UUID lowonganId, DaftarForm form, Mahasiswa mahasiswa) {
        return pendaftaranService.daftar(
                lowonganId,
                mahasiswa,
                BigDecimal.valueOf(form.getIpk()),
                form.getSks()
        );
    }
}