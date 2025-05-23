package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.strategy;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;

import java.math.BigDecimal;
import java.util.UUID;

public interface PendaftaranStrategy {
    Pendaftaran execute(UUID lowonganId, Mahasiswa kandidat, BigDecimal ipk, int sks);
}