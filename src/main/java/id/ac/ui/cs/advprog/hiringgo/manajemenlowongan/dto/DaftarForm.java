package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class DaftarForm {
    @NotNull(message = "IPK tidak boleh kosong")
    @DecimalMin(value = "0.0", message = "IPK minimum 0.0")
    @DecimalMax(value = "4.0", message = "IPK maksimum 4.0")
    private BigDecimal ipk;

    @NotNull(message = "SKS tidak boleh kosong")
    @Min(value = 0, message = "SKS tidak boleh negatif")
    private Integer sks;

    public BigDecimal getIpk() {
        return ipk;
    }

    public void setIpk(BigDecimal ipk) {
        this.ipk = ipk;
    }

    public Integer getSks() {
        return sks;
    }

    public void setSks(Integer sks) {
        this.sks = sks;
    }
}

