package id.ac.ui.cs.advprog.hiringgo.lowongan.model;

import jakarta.persistence.*;

@Entity
public class Lowongan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String judul;
    private String deskripsi;
    private int kuota;
    private int jumlahPendaftar;

    public Lowongan() {
        // Wajib untuk JPA
    }

    public Lowongan(String judul, String deskripsi, int kuota) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.kuota = kuota;
        this.jumlahPendaftar = 0;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getKuota() {
        return kuota;
    }

    public void setKuota(int kuota) {
        this.kuota = kuota;
    }

    public int getJumlahPendaftar() {
        return jumlahPendaftar;
    }

    public void setJumlahPendaftar(int jumlahPendaftar) {
        this.jumlahPendaftar = jumlahPendaftar;
    }
}
