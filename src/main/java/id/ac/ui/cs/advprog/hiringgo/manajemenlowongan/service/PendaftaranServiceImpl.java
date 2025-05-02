package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;


import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor            // (jika menggunakan Lombok)
public class PendaftaranServiceImpl implements PendaftaranService {

    private static final String ERR_LOWONGAN_NOT_FOUND = "Lowongan tidak ditemukan: %s";
    private static final String ERR_KUOTA_PENUH       = "Kuota lowongan sudah penuh!";

    private final LowonganRepository lowonganRepository;
    private final PendaftaranRepository pendaftaranRepository;

    @Override
    public Pendaftaran daftar(UUID lowonganId,
                              String kandidatId,
                              BigDecimal ipk,
                              int sks) {
        // Ambil lowongan, lempar NoSuchElementException jika kosong
        Lowongan lowongan = lowonganRepository.findById(lowonganId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                String.format(ERR_LOWONGAN_NOT_FOUND, lowonganId))
                );

        // Validasi kuota
        if (lowongan.getJumlahAsdosPendaftar() >= lowongan.getJumlahAsdosDibutuhkan()) {
            throw new IllegalStateException(ERR_KUOTA_PENUH);
        }

        // Buat entitas dan simpan
        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setKandidatId(kandidatId);
        pendaftaran.setIpk(ipk);
        pendaftaran.setSks(sks);
        pendaftaran.setWaktuDaftar(LocalDateTime.now());

        Pendaftaran saved = pendaftaranRepository.save(pendaftaran);

        // Update counter—akan ter‐flush otomatis saat commit
        lowongan.setJumlahAsdosPendaftar(
                lowongan.getJumlahAsdosPendaftar() + 1
        );

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pendaftaran> getByLowongan(UUID lowonganId) {
        return pendaftaranRepository.findByLowonganLowonganId(lowonganId);
    }

}

