package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ApplicationStatusService {

    private final PendaftaranRepository pendaftaranRepository;

    @Autowired
    public ApplicationStatusService(PendaftaranRepository pendaftaranRepository) {
        this.pendaftaranRepository = pendaftaranRepository;
    }

    public Map<String, Object> getApplicationStatus(UUID lowonganId, Mahasiswa mahasiswa) {
        List<Pendaftaran> pendaftaranList = findApplicationsByUserAndLowongan(lowonganId, mahasiswa);
        return buildStatusData(pendaftaranList);
    }

    public boolean hasUserAlreadyApplied(UUID lowonganId, Mahasiswa mahasiswa) {
        List<Pendaftaran> applications = findApplicationsByUserAndLowongan(lowonganId, mahasiswa);
        return !applications.isEmpty();
    }

    private List<Pendaftaran> findApplicationsByUserAndLowongan(UUID lowonganId, Mahasiswa mahasiswa) {
        return pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(
                mahasiswa.getId(), lowonganId);
    }

    private Map<String, Object> buildStatusData(List<Pendaftaran> pendaftaranList) {
        Map<String, Object> statusData = new HashMap<>();

        if (pendaftaranList.isEmpty()) {
            statusData.put("hasApplied", false);
            statusData.put("status", "BELUM_DAFTAR");
        } else {
            Pendaftaran pendaftaran = pendaftaranList.get(0);
            statusData.put("hasApplied", true);
            statusData.put("status", pendaftaran.getStatus().toString());
            statusData.put("pendaftaranId", pendaftaran.getPendaftaranId());
        }

        return statusData;
    }
}