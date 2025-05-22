package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.strategy.PendaftaranStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PendaftaranServiceImpl implements PendaftaranService {

    private final PendaftaranRepository pendaftaranRepository;
    private PendaftaranStrategy pendaftaranStrategy;

    @Autowired
    public PendaftaranServiceImpl(PendaftaranRepository pendaftaranRepository,
                                  PendaftaranStrategy standardPendaftaranStrategy) {
        if (pendaftaranRepository == null) {
            throw new IllegalArgumentException("PendaftaranRepository cannot be null");
        }
        if (standardPendaftaranStrategy == null) {
            throw new IllegalArgumentException("PendaftaranStrategy cannot be null");
        }
        this.pendaftaranRepository = pendaftaranRepository;
        this.pendaftaranStrategy = standardPendaftaranStrategy;
    }

    public void setPendaftaranStrategy(PendaftaranStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("PendaftaranStrategy cannot be null");
        }
        this.pendaftaranStrategy = strategy;
    }

    @Override
    public Pendaftaran daftar(UUID lowonganId,
                              Mahasiswa kandidat,
                              BigDecimal ipk,
                              int sks) {
        if (lowonganId == null) {
            throw new IllegalArgumentException("Lowongan ID cannot be null");
        }
        if (kandidat == null) {
            throw new IllegalArgumentException("Kandidat cannot be null");
        }
        if (ipk == null) {
            throw new IllegalArgumentException("IPK cannot be null");
        }

        try {
            return pendaftaranStrategy.execute(lowonganId, kandidat, ipk, sks);
        } catch (RuntimeException cause) {
            throw new IllegalStateException(
                    "Error during pendaftaran for lowongan " + lowonganId + " and kandidat " + kandidat.getId(),
                    cause
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pendaftaran> getByLowongan(UUID lowonganId) {
        if (lowonganId == null) {
            throw new IllegalArgumentException("Lowongan ID cannot be null");
        }

        try {
            return pendaftaranRepository.findByLowonganLowonganId(lowonganId);
        } catch (RuntimeException cause) {
            throw new IllegalStateException(
                    "Failed to retrieve pendaftaran for lowongan " + lowonganId,
                    cause
            );
        }
    }
}