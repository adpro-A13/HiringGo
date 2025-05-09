package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DaftarResponse {
    private boolean success;
    private String message;
    private UUID pendaftaranId;
    private UUID lowonganId;
    private String kandidatId;
    private BigDecimal ipk;
    private int sks;
    private LocalDateTime waktuDaftar;

    public DaftarResponse(boolean success, String message, Pendaftaran pendaftaran) {
        this.success = success;
        this.message = message;
        if (pendaftaran != null) {
            this.pendaftaranId = pendaftaran.getPendaftaranId();
            this.lowonganId = pendaftaran.getLowongan().getLowonganId();
            this.kandidatId = pendaftaran.getKandidatId();
            this.ipk = pendaftaran.getIpk();
            this.sks = pendaftaran.getSks();
            this.waktuDaftar = pendaftaran.getWaktuDaftar();
        }
    }

}