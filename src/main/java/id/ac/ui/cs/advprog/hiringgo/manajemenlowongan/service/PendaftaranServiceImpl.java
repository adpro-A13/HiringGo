package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;


import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class PendaftaranServiceImpl implements PendaftaranService {

    private final LowonganRepository lowonganRepository;
    private final PendaftaranRepository pendaftaranRepository;

    public PendaftaranServiceImpl(LowonganRepository lowonganRepository,
                                  PendaftaranRepository pendaftaranRepository) {
        this.lowonganRepository = lowonganRepository;
        this.pendaftaranRepository = pendaftaranRepository;
    }

    @Override
    public Pendaftaran daftar(UUID lowonganId, String kandidatId, BigDecimal ipk, int sks) {
        // Cek keberadaan lowongan
        Lowongan lowongan = lowonganRepository.findById(lowonganId)
                .orElseThrow(() -> new RuntimeException("Lowongan tidak ditemukan"));
        // Cek kuota lowongan (jumlah pendaftar tidak boleh melebihi jumlah dibutuhkan)
        if (lowongan.getJumlahAsdosPendaftar() >= lowongan.getJumlahAsdosDibutuhkan()) {
            throw new IllegalStateException("Kuota lowongan sudah penuh!");
        }
        // Buat entitas Pendaftaran baru
        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setKandidatId(kandidatId);
        pendaftaran.setIpk(ipk);
        pendaftaran.setSks(sks);
        pendaftaran.setWaktuDaftar(LocalDateTime.now());
        // Simpan pendaftaran ke database
        Pendaftaran savedPendaftaran = pendaftaranRepository.save(pendaftaran);
        // Update counter jumlah pendaftar di lowongan
        lowongan.setJumlahAsdosPendaftar(lowongan.getJumlahAsdosPendaftar() + 1);
        lowonganRepository.save(lowongan);
        return savedPendaftaran;
    }

    @Override
    public List<Pendaftaran> getByLowongan(UUID lowonganId) {
        return pendaftaranRepository.findByLowonganLowonganId(lowonganId);
    }
}

