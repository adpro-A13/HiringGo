package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;

@Getter
@Setter
public class LowonganResponse {
    private UUID lowonganId;
    private String idMataKuliah;
    private String tahunAjaran;
    private Semester semester;
    private StatusLowongan statusLowongan;
    private int jumlahAsdosDibutuhkan;
    private int jumlahAsdosDiterima;
    private int jumlahAsdosPendaftar;
    private List<String> idAsdosDiterima;

    public LowonganResponse(Lowongan lowongan) {
        this.lowonganId            = lowongan.getLowonganId();
        this.idMataKuliah         = lowongan.getIdMataKuliah();
        this.tahunAjaran          = lowongan.getTahunAjaran();
        this.semester             = lowongan.getSemester();
        this.statusLowongan       = lowongan.getStatusLowongan();
        this.jumlahAsdosDibutuhkan = lowongan.getJumlahAsdosDibutuhkan();
        this.jumlahAsdosDiterima   = lowongan.getJumlahAsdosDiterima();
        this.jumlahAsdosPendaftar  = lowongan.getJumlahAsdosPendaftar();
        this.idAsdosDiterima      = lowongan.getIdAsdosDiterima();
    }
}