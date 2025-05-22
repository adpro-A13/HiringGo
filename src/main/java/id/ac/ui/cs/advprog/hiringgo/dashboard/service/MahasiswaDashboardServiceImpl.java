package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.MahasiswaDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class MahasiswaDashboardServiceImpl extends AbstractDashboardService {

    private final UserRepository userRepository;
    private final LowonganRepository lowonganRepository;
    private final PendaftaranRepository pendaftaranRepository;
    private final LowonganMapper lowonganMapper;


    @Autowired
    public MahasiswaDashboardServiceImpl(
            UserRepository userRepository,
            LowonganRepository lowonganRepository,
            PendaftaranRepository pendaftaranRepository,
            LowonganMapper lowonganMapper) {
        this.userRepository = userRepository;
        this.lowonganRepository = lowonganRepository;
        this.pendaftaranRepository = pendaftaranRepository;
        this.lowonganMapper = lowonganMapper;
    }

    @Override
    protected void validateUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User tidak ditemukan dengan ID: " + userId);
        }

        boolean isMahasiswa = userRepository.findById(userId)
                .filter(Mahasiswa.class::isInstance)
                .isPresent();

        if (!isMahasiswa) {
            throw new IllegalArgumentException("User dengan ID: " + userId + " bukan mahasiswa");
        }
    }

    @Override
    protected DashboardResponse createDashboardResponse() {
        return new MahasiswaDashboardResponse();
    }

    @Override
    protected void populateCommonData(UUID userId, DashboardResponse response) {
        Mahasiswa mahasiswa = userRepository.findById(userId)
                .filter(Mahasiswa.class::isInstance)
                .map(Mahasiswa.class::cast)
                .orElseThrow(() -> new NoSuchElementException("Mahasiswa tidak ditemukan dengan ID: " + userId));

        response.setUserRole("MAHASISWA");
        response.setUsername(mahasiswa.getUsername());
        response.setFullName(mahasiswa.getFullName());

        Map<String, String> features = new HashMap<>();
        features.put("pendaftaran", "/api/pendaftaran");
        features.put("lowongan", "/api/lowongan");
        features.put("profile", "/api/profile");
        features.put("logActivities", "/api/log");
        response.setAvailableFeatures(features);
    }

    @Override
    protected void populateRoleSpecificData(UUID userId, DashboardResponse baseResponse) {
        MahasiswaDashboardResponse response = (MahasiswaDashboardResponse) baseResponse;

        List<Lowongan> allLowonganWithOpenStatus = lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA);
        int openLowonganCount = (int) allLowonganWithOpenStatus.stream()
                .filter(lowongan -> lowongan.getJumlahAsdosPendaftar() < lowongan.getJumlahAsdosDibutuhkan())
                .count();
        response.setOpenLowonganCount(openLowonganCount);

        List<Lowongan> allLowongan = lowonganRepository.findAll();
        response.setTotalLowonganCount(allLowongan.size());

        List<Pendaftaran> allApplications = pendaftaranRepository.findByKandidatId(userId);
        response.setTotalApplicationsCount(allApplications.size());
        int pendingCount = countApplicationsByStatus(allApplications, StatusPendaftaran.BELUM_DIPROSES);
        int acceptedCount = countApplicationsByStatus(allApplications, StatusPendaftaran.DITERIMA);
        int rejectedCount = countApplicationsByStatus(allApplications, StatusPendaftaran.DITOLAK);

        response.setPendingApplicationsCount(pendingCount);
        response.setAcceptedApplicationsCount(acceptedCount);
        response.setRejectedApplicationsCount(rejectedCount);

        response.setTotalLoggedHours(calculateTotalLoggedHours(userId));

        response.setTotalIncentive(calculateTotalIncentive(userId));

        List<LowonganDTO> acceptedLowongan = allApplications.stream()
                .filter(app -> app.getStatus() == StatusPendaftaran.DITERIMA)
                .map(Pendaftaran::getLowongan)
                .filter(Objects::nonNull)
                .map(this::convertToLowonganDTO)
                .collect(Collectors.toList());
        response.setAcceptedLowongan(acceptedLowongan);

        List<LowonganDTO> recentLowongan = allLowonganWithOpenStatus.stream()
                .map(this::convertToLowonganDTO)
                .collect(Collectors.toList());
        response.setRecentLowongan(recentLowongan);
    }

    private int countApplicationsByStatus(List<Pendaftaran> applications, StatusPendaftaran status) {
        if (applications == null) {
            return 0;
        }
        return (int) applications.stream()
                .filter(app -> app.getStatus() == status)
                .count();
    }

    private int calculateTotalLoggedHours(UUID userId) {
        // TODO: Implementasikan perhitungan jam yang sesungguhnya
        // dari repository log aktivitas (jika ada)
        return 0;
    }

    private BigDecimal calculateTotalIncentive(UUID userId) {
        // TODO: Implementasikan perhitungan insentif yang sesungguhnya
        // dari repository honor/pembayaran (jika ada)
        return BigDecimal.ZERO;
    }

    private LowonganDTO convertToLowonganDTO(Lowongan lowongan) {
        return lowonganMapper.toDto(lowongan);
    }

}