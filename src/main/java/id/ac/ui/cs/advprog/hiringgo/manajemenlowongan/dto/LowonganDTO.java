package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LowonganDTO {
    private UUID lowonganId;
    private String idMataKuliah;
    private String namaMataKuliah;
    private String deskripsiMataKuliah;

    private String tahunAjaran;
    private String semester;
    private String statusLowongan;
    private int jumlahAsdosDibutuhkan;
    private int jumlahAsdosDiterima;
    private int jumlahAsdosPendaftar;
    private List<UUID> idDaftarPendaftaran;
}