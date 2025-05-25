package id.ac.ui.cs.advprog.hiringgo.dashboard.service.data;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApplicationDataService {

    private final PendaftaranRepository pendaftaranRepository;
    private final LowonganMapper lowonganMapper;

    @Autowired
    public ApplicationDataService(PendaftaranRepository pendaftaranRepository, LowonganMapper lowonganMapper) {
        this.pendaftaranRepository = pendaftaranRepository;
        this.lowonganMapper = lowonganMapper;
    }

    public List<Pendaftaran> getAllApplications(UUID userId) {
        return pendaftaranRepository.findByKandidatId(userId);
    }

    public int countApplicationsByStatus(List<Pendaftaran> applications, StatusPendaftaran status) {
        if (applications == null) {
            return 0;
        }
        return (int) applications.stream()
                .filter(app -> app.getStatus() == status)
                .count();
    }

    public List<LowonganDTO> getAcceptedLowongan(UUID userId) {
        return getAllApplications(userId).stream()
                .filter(app -> app.getStatus() == StatusPendaftaran.DITERIMA)
                .map(Pendaftaran::getLowongan)
                .map(lowonganMapper::toDto)
                .collect(Collectors.toList());
    }
}