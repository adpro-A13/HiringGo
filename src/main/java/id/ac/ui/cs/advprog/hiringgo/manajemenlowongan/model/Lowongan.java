package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Lowongan {

    @Id
    @GeneratedValue
    private UUID lowonganId;

    private String idMataKuliah;
    private String tahunAjaran;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    @Enumerated(EnumType.STRING)
    private StatusLowongan statusLowongan;

    private int jumlahAsdosDibutuhkan;
    private int jumlahAsdosDiterima;
    private int jumlahAsdosPendaftar;

    @ElementCollection
    private List<String> idAsdosDiterima;

    public Lowongan() {}

    public Lowongan(String lowonganId,
                    String idMataKuliah,
                    String tahunAjaran,
                    String statusLowongan,
                    String semester,
                    int jumlahAsdosDibutuhkan,
                    int jumlahAsdosDiterima,
                    int jumlahAsdosPendaftar,
                    List<String> idAsdosDiterima) {

        if (!StatusLowongan.contains(statusLowongan)) {
            throw new IllegalArgumentException("Status lowongan tidak valid: " + statusLowongan);
        }

        if (!Semester.contains(semester)) {
            throw new IllegalArgumentException("Semester tidak valid: " + semester);
        }

        this.lowonganId = UUID.fromString(lowonganId);
        this.idMataKuliah = idMataKuliah;
        this.tahunAjaran = tahunAjaran;
        this.statusLowongan = StatusLowongan.valueOf(statusLowongan.toUpperCase());
        this.semester = Semester.valueOf(semester.toUpperCase());
        this.jumlahAsdosDibutuhkan = jumlahAsdosDibutuhkan;
        this.jumlahAsdosDiterima = jumlahAsdosDiterima;
        this.jumlahAsdosPendaftar = jumlahAsdosPendaftar;
        this.idAsdosDiterima = idAsdosDiterima;
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
