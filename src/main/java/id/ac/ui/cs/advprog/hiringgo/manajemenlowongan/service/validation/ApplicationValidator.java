package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.validation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;

import java.util.UUID;

public interface ApplicationValidator {
    void validate(UUID lowonganId, DaftarForm form, Mahasiswa mahasiswa);
}