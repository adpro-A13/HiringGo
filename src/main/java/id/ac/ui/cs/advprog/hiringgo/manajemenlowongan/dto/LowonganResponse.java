package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LowonganResponse {
    private String judul;
    private String mataKuliah;
    private String deskripsi;
    private int jumlahAsdosDibutuhkan;
    private int jumlahAsdosPendaftar;

    public LowonganResponse(Lowongan lowongan) {
        this.judul = lowongan.getJudul();
        this.mataKuliah = lowongan.getMataKuliah();
        this.deskripsi = lowongan.getDeskripsi();
        this.jumlahAsdosDibutuhkan = lowongan.getJumlahAsdosDibutuhkan();
        this.jumlahAsdosPendaftar = lowongan.getJumlahAsdosPendaftar();
    }
}