package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
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
    @JsonBackReference
    private Lowongan lowongan;

    @ManyToOne
    @JoinColumn(name = "kandidat_id")
    private Mahasiswa kandidat;
    private BigDecimal ipk;
    private int sks;
    private LocalDateTime waktuDaftar;

    @Enumerated(EnumType.STRING)
    private StatusPendaftaran status = StatusPendaftaran.BELUM_DIPROSES;

    public Pendaftaran() {
        // diperlukan oleh JPA
    }

    public Pendaftaran(Lowongan lowongan, Mahasiswa kandidat, BigDecimal ipk, int sks, LocalDateTime waktuDaftar) {
        this.lowongan = lowongan;
        this.kandidat = kandidat;
        this.ipk = ipk;
        this.sks = sks;
        this.waktuDaftar = waktuDaftar;
    }
}