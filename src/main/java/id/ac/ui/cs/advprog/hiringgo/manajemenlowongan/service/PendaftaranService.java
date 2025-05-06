package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PendaftaranService {
    Pendaftaran daftar(UUID lowonganId, String kandidatId, BigDecimal ipk, int sks);
    List<Pendaftaran> getByLowongan(UUID lowonganId);
}
