package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.strategy;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class StandardPendaftaranStrategy implements PendaftaranStrategy {

    private static final String ERR_LOWONGAN_NOT_FOUND = "Lowongan tidak ditemukan";
    private static final String ERR_KUOTA_PENUH = "Kuota lowongan sudah penuh!";

    private final LowonganRepository lowonganRepository;
    private final PendaftaranRepository pendaftaranRepository;

    public StandardPendaftaranStrategy(LowonganRepository lowonganRepository,
                                       PendaftaranRepository pendaftaranRepository) {
        if (lowonganRepository == null) {
            throw new IllegalArgumentException("LowonganRepository cannot be null");
        }
        if (pendaftaranRepository == null) {
            throw new IllegalArgumentException("PendaftaranRepository cannot be null");
        }
        this.lowonganRepository = lowonganRepository;
        this.pendaftaranRepository = pendaftaranRepository;
    }

    @Override
    @Transactional
    public Pendaftaran execute(UUID lowonganId, Mahasiswa kandidat, BigDecimal ipk, int sks) {
        validateInputs(lowonganId, kandidat, ipk);
        try {
            Lowongan lowongan = fetchLowongan(lowonganId);
            ensureQuotaAvailable(lowongan);
            Pendaftaran pendaftaran = buildPendaftaran(lowongan, kandidat, ipk, sks);
            updateLowonganCount(lowongan);
            return pendaftaranRepository.save(pendaftaran);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // propagate known exceptions as is
            throw e;
        } catch (Exception e) {
            // wrap unexpected exceptions
            throw new IllegalStateException(
                    "Gagal melakukan pendaftaran untuk lowongan " + lowonganId + ": " + e.getMessage(),
                    e
            );
        }
    }

    private void validateInputs(UUID lowonganId, Mahasiswa kandidat, BigDecimal ipk) {
        if (lowonganId == null) {
            throw new IllegalArgumentException("Lowongan ID cannot be null");
        }
        if (kandidat == null) {
            throw new IllegalArgumentException("Kandidat cannot be null");
        }
        if (ipk == null) {
            throw new IllegalArgumentException("IPK cannot be null");
        }
    }

    private Lowongan fetchLowongan(UUID lowonganId) {
        return lowonganRepository.findById(lowonganId)
                .orElseThrow(() -> new IllegalStateException(ERR_LOWONGAN_NOT_FOUND));
    }

    private void ensureQuotaAvailable(Lowongan lowongan) {
        if (lowongan.getJumlahAsdosDiterima() >= lowongan.getJumlahAsdosDibutuhkan()) {
            throw new IllegalStateException(ERR_KUOTA_PENUH);
        }
    }

    private Pendaftaran buildPendaftaran(Lowongan lowongan,
                                         Mahasiswa kandidat,
                                         BigDecimal ipk,
                                         int sks) {
        Pendaftaran p = new Pendaftaran();
        p.setLowongan(lowongan);
        p.setKandidat(kandidat);
        p.setIpk(ipk);
        p.setSks(sks);
        p.setWaktuDaftar(LocalDateTime.now());
        return p;
    }

    private void updateLowonganCount(Lowongan lowongan) {
        lowongan.setJumlahAsdosPendaftar(lowongan.getJumlahAsdosPendaftar() + 1);
        lowonganRepository.save(lowongan);
    }
}