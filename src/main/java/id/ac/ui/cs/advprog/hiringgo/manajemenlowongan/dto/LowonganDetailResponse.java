package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class LowonganDetailResponse {
    private UUID lowonganId;
    private String idMataKuliah;
    private String mataKuliah; // This would come from another service in a real app
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
        this.mataKuliah = "Mata Kuliah " + lowongan.getMataKuliah().getKode(); // Placeholder
        this.tahunAjaran = lowongan.getTahunAjaran();
        this.semester = lowongan.getSemester().toString();
        this.statusLowongan = lowongan.getStatusLowongan().toString();
        this.judul = "Asisten Dosen " + lowongan.getMataKuliah().getKode(); // Placeholder
        this.deskripsi = "Deskripsi untuk " + lowongan.getMataKuliah(); // Placeholder
        this.persyaratan = "Persyaratan untuk " + lowongan.getMataKuliah(); // Placeholder
        this.jumlahAsdosDibutuhkan = lowongan.getJumlahAsdosDibutuhkan();
        this.jumlahAsdosDiterima = lowongan.getJumlahAsdosDiterima();
        this.jumlahAsdosPendaftar = lowongan.getJumlahAsdosPendaftar();
    }

}