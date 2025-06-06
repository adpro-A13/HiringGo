package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class LowonganDetailResponse {
    private UUID lowonganId;
    private String idMataKuliah;
    private String mataKuliah;
    private String tahunAjaran;
    private String semester;
    private String statusLowongan;
    private String judul;
    private String deskripsi;
    private String persyaratan;
    private int jumlahAsdosDibutuhkan;
    private int jumlahAsdosDiterima;
    private int jumlahAsdosPendaftar;
    private List<UUID> idDaftarPendaftaran;

    public LowonganDetailResponse(Lowongan lowongan) {
        this.lowonganId = lowongan.getLowonganId();
        this.idMataKuliah = lowongan.getMataKuliah().getKode();
        this.mataKuliah = "Mata Kuliah " + lowongan.getMataKuliah().getKode();
        this.tahunAjaran = lowongan.getTahunAjaran();
        this.semester = lowongan.getSemester().toString();
        this.statusLowongan = lowongan.getStatusLowongan().toString();
        this.judul = "Asisten Dosen " + lowongan.getMataKuliah().getKode();
        this.deskripsi = "Deskripsi untuk " + lowongan.getMataKuliah();
        this.persyaratan = "Persyaratan untuk " + lowongan.getMataKuliah();
        this.jumlahAsdosDibutuhkan = lowongan.getJumlahAsdosDibutuhkan();
        this.jumlahAsdosDiterima = lowongan.getJumlahAsdosDiterima();
        this.jumlahAsdosPendaftar = lowongan.getJumlahAsdosPendaftar();
    }

}