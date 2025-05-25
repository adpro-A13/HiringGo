package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DaftarForm {

    @NotNull(message = "IPK harus diisi")
    @DecimalMin(value = "0.0", message = "IPK minimal 0.0")
    @DecimalMax(value = "4.0", message = "IPK maksimal 4.0")
    @Digits(integer = 1, fraction = 2, message = "Format IPK tidak valid (contoh: 3.75)")
    private Double ipk;

    @NotNull(message = "Jumlah SKS harus diisi")
    @Min(value = 0, message = "SKS tidak boleh negatif")
    @Max(value = 24, message = "SKS maksimal 24 per semester")
    private Integer sks;
}