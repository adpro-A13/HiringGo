package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
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
@RequiredArgsConstructor
public class PendaftaranServiceImpl implements PendaftaranService {

    private static final String ERR_LOWONGAN_NOT_FOUND = "Lowongan tidak ditemukan";
    private static final String ERR_KUOTA_PENUH       = "Kuota lowongan sudah penuh!";

    private final LowonganRepository lowonganRepository;
    private final PendaftaranRepository pendaftaranRepository;

    @Override
    public Pendaftaran daftar(UUID lowonganId,
                              Mahasiswa kandidat,
                              BigDecimal ipk,
                              int sks) {
        // 1) Cari lowongan, lempar NoSuchElementException dengan pesan yang di‐test
        Lowongan lowongan = lowonganRepository.findById(lowonganId)
                .orElseThrow(() ->
                        new NoSuchElementException(ERR_LOWONGAN_NOT_FOUND)
                );

        // 2) Validasi kuota
        if (lowongan.getJumlahAsdosPendaftar() >= lowongan.getJumlahAsdosDibutuhkan()) {
            throw new IllegalStateException(ERR_KUOTA_PENUH);
        }

        // 3) Buat dan simpan pendaftaran
        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setKandidat(kandidat);
        pendaftaran.setIpk(ipk);
        pendaftaran.setSks(sks);
        pendaftaran.setWaktuDaftar(LocalDateTime.now());
        Pendaftaran saved = pendaftaranRepository.save(pendaftaran);

        // 4) Update counter dan panggil save agar mock lowonganRepository verifikasi terpenuhi
        lowongan.setJumlahAsdosPendaftar(lowongan.getJumlahAsdosPendaftar() + 1);
        lowonganRepository.save(lowongan);

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pendaftaran> getByLowongan(UUID lowonganId) {
        return pendaftaranRepository.findByLowonganLowonganId(lowonganId);
    }
}
