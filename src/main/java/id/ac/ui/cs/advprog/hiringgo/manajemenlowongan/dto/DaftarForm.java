package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for Asisten Dosen application form.
 * Simplified to match existing UI implementation.
 */
@Getter @Setter
public class DaftarForm {

    @NotNull(message = "IPK harus diisi")
    @DecimalMin(value = "0.0", message = "IPK minimal 0.0")
    @DecimalMax(value = "4.0", message = "IPK maksimal 4.0")
    @Digits(integer = 1, fraction = 2, message = "Format IPK tidak valid (contoh: 3.75)")
    private Double ipk;

    @NotNull(message = "Jumlah SKS harus diisi")
    @Min(value = 0, message = "SKS tidak boleh negatif")
    private Integer sks;

    public DaftarForm() {
        // Default constructor
    }

    public DaftarForm(Double ipk, Integer sks) {
        this.ipk = ipk;
        this.sks = sks;
    }
}