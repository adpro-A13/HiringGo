package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.validation;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import org.springframework.stereotype.Component;

@Component
public class BusinessRuleValidator {

    private static final double MIN_IPK = 2.5;
    private static final int MIN_SKS = 12;
    private static final int MAX_SKS = 24;

    public void validate(DaftarForm form) {
        validateIpk(form.getIpk());
        validateSks(form.getSks());
    }

    private void validateIpk(Double ipk) {
        if (ipk != null && ipk < MIN_IPK) {
            throw new IllegalArgumentException("IPK minimal " + MIN_IPK + " untuk mendaftar lowongan");
        }
    }

    private void validateSks(Integer sks) {
        if (sks != null && sks < MIN_SKS) {
            throw new IllegalArgumentException("SKS minimal " + MIN_SKS + " untuk bisa mendaftar lowongan");
        }

        if (sks != null && sks > MAX_SKS) {
            throw new IllegalArgumentException("SKS maksimal " + MAX_SKS + " per semester");
        }
    }
}