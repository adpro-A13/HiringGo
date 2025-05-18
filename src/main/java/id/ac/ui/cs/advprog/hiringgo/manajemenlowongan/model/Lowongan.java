package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Lowongan {

    @Id
    @GeneratedValue
    private UUID lowonganId;

    @ManyToOne
    @JoinColumn(name = "mata_kuliah_kode")
    private MataKuliah mataKuliah;
    private String tahunAjaran;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    @Enumerated(EnumType.STRING)
    private StatusLowongan statusLowongan;

    private int jumlahAsdosDibutuhkan;
    private int jumlahAsdosDiterima;
    private int jumlahAsdosPendaftar;

    @OneToMany(mappedBy = "lowongan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pendaftaran> daftarPendaftaran = new ArrayList<>();


    public Lowongan() {}

    public Lowongan(String lowonganId, MataKuliah mataKuliah, String tahunAjaran, String semester,
                    int jumlahAsdosDibutuhkan) {
        if (!Semester.contains(semester)) {
            throw new IllegalArgumentException("Semester tidak valid: " + semester);
        }

        this.lowonganId = UUID.fromString(lowonganId);
        this.mataKuliah = mataKuliah;
        this.tahunAjaran = tahunAjaran;
        this.semester = Semester.valueOf(semester.toUpperCase());
        this.statusLowongan = StatusLowongan.DIBUKA;
        this.jumlahAsdosDibutuhkan = jumlahAsdosDibutuhkan;
        this.jumlahAsdosDiterima = 0;
        this.jumlahAsdosPendaftar = 0;
    }

    public void setStatusLowongan(String statusLowongan) {
        if (!StatusLowongan.contains(statusLowongan)) {
            throw new IllegalArgumentException("Status lowongan tidak valid: " + statusLowongan);
        }
        this.statusLowongan = StatusLowongan.valueOf(statusLowongan.toUpperCase());
    }

    public void setSemester(String semester) {
        if (!Semester.contains(semester)) {
            throw new IllegalArgumentException("Semester tidak valid: " + semester);
        }
        this.semester = Semester.valueOf(semester.toUpperCase());
    }
}
