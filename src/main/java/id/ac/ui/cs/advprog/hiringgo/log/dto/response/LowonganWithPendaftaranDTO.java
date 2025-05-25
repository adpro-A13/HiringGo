package id.ac.ui.cs.advprog.hiringgo.log.dto.response;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;

import java.util.List;

public class LowonganWithPendaftaranDTO {
    private Lowongan lowongan;
    private List<Pendaftaran> pendaftaranUser;

    public LowonganWithPendaftaranDTO(Lowongan lowongan, List<Pendaftaran> pendaftaranUser) {
        this.lowongan = lowongan;
        this.pendaftaranUser = pendaftaranUser;
    }

    public Lowongan getLowongan() {
        return lowongan;
    }

    public List<Pendaftaran> getPendaftaranUser() {
        return pendaftaranUser;
    }
}
