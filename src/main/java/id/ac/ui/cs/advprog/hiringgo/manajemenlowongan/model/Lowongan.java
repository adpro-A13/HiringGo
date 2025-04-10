package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model;

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

    // Masih pakai String id karena model MataKuliah belum ada
    private String idMataKuliah;
    private String tahunAjaran;

    private String semester;
    private String statusLowongan;

    private int jumlahAsdosDibutuhkan;
    private int jumlahAsdosDiterima;
    private int jumlahAsdosPendaftar;

    // Masih pakai String id karena model Mahasiswa belum ada
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

        if (!statusLowongan.equals("DIBUKA") && !statusLowongan.equals("DITUTUP")) {
            throw new IllegalArgumentException("Status lowongan tidak valid: " + statusLowongan);
        }

        if (!semester.equals("GANJIL") && !semester.equals("GENAP")) {
            throw new IllegalArgumentException("Semester tidak valid: " + semester);
        }

        this.lowonganId = UUID.fromString(lowonganId);
        this.idMataKuliah = idMataKuliah;
        this.tahunAjaran = tahunAjaran;
        this.statusLowongan = statusLowongan;
        this.semester = semester;
        this.jumlahAsdosDibutuhkan = jumlahAsdosDibutuhkan;
        this.jumlahAsdosDiterima = jumlahAsdosDiterima;
        this.jumlahAsdosPendaftar = jumlahAsdosPendaftar;
        this.idAsdosDiterima = idAsdosDiterima;
    }

    public void setStatusLowongan(String statusLowongan) {
        if (!statusLowongan.equals("DIBUKA") && !statusLowongan.equals("DITUTUP")) {
            throw new IllegalArgumentException("Status lowongan tidak valid: " + statusLowongan);
        }
        this.statusLowongan = statusLowongan;
    }

    public void setSemester(String semester) {
        if (!semester.equals("GANJIL") && !semester.equals("GENAP")) {
            throw new IllegalArgumentException("Semester tidak valid: " + semester);
        }
        this.semester = semester;
    }
}
