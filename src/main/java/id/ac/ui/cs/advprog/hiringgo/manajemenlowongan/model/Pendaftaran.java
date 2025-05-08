package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Pendaftaran {

    @Id
    @GeneratedValue
    private UUID pendaftaranId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lowongan_id", nullable = false)
    private Lowongan lowongan;

    private String kandidatId;
    private BigDecimal ipk;
    private int sks;
    private LocalDateTime waktuDaftar;

    public Pendaftaran() {
        // diperlukan oleh JPA
    }

    public Pendaftaran(Lowongan lowongan, String kandidatId, BigDecimal ipk, int sks, LocalDateTime waktuDaftar) {
        this.lowongan = lowongan;
        this.kandidatId = kandidatId;
        this.ipk = ipk;
        this.sks = sks;
        this.waktuDaftar = waktuDaftar;
    }
}