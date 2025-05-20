package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository;


import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PendaftaranRepository extends JpaRepository<Pendaftaran, UUID> {
    List<Pendaftaran> findByLowonganLowonganId(UUID lowonganId);
    List<Pendaftaran> findByKandidatId(UUID kandidatId);
    List<Pendaftaran> findByKandidatIdAndLowonganLowonganId(UUID kandidatId, UUID lowonganId);
}
