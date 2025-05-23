package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
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
import java.util.concurrent.CompletableFuture;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
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
    private final LogService logService;


    @Autowired
    public MahasiswaDashboardServiceImpl(
            UserRepository userRepository,
            LowonganRepository lowonganRepository,
            PendaftaranRepository pendaftaranRepository,
            LowonganMapper lowonganMapper, LogService logService) {
        this.userRepository = userRepository;
        this.lowonganRepository = lowonganRepository;
        this.pendaftaranRepository = pendaftaranRepository;
        this.lowonganMapper = lowonganMapper;
        this.logService = logService;
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

        CompletableFuture<BigDecimal> hoursFuture = calculateTotalLoggedHoursAsync(userId);
        CompletableFuture<BigDecimal> incentiveFuture = calculateTotalIncentiveAsync(userId);

        List<Lowongan> allLowongan = lowonganRepository.findAll();
        List<Lowongan> openLowongan = lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA);
        List<Pendaftaran> allApplications = pendaftaranRepository.findByKandidatId(userId);

        response.setTotalLowonganCount(allLowongan.size());
        response.setOpenLowonganCount(openLowongan.size());
        response.setTotalApplicationsCount(allApplications.size());

        int pendingCount = countApplicationsByStatus(allApplications, StatusPendaftaran.BELUM_DIPROSES);
        int acceptedCount = countApplicationsByStatus(allApplications, StatusPendaftaran.DITERIMA);
        int rejectedCount = countApplicationsByStatus(allApplications, StatusPendaftaran.DITOLAK);

        response.setPendingApplicationsCount(pendingCount);
        response.setAcceptedApplicationsCount(acceptedCount);
        response.setRejectedApplicationsCount(rejectedCount);

        List<LowonganDTO> acceptedLowongan = allApplications.stream()
                .filter(app -> app.getStatus() == StatusPendaftaran.DITERIMA)
                .map(Pendaftaran::getLowongan)
                .map(this::convertToLowonganDTO)
                .collect(Collectors.toList());

        response.setAcceptedLowongan(acceptedLowongan);

        response.setTotalLoggedHours(hoursFuture.join());
        response.setTotalIncentive(incentiveFuture.join());
    }

    private int countApplicationsByStatus(List<Pendaftaran> applications, StatusPendaftaran status) {
        if (applications == null) {
            return 0;
        }
        return (int) applications.stream()
                .filter(app -> app.getStatus() == status)
                .count();
    }

    private BigDecimal calculateTotalLoggedHours(UUID userId) {
        List<Log> logs = logService.getLogsByUser(userId);
        long totalLoggedMinutes = logs.stream()
                .map(log -> Duration.between(log.getWaktuMulai(), log.getWaktuSelesai()))
                .mapToLong(Duration::toMinutes)
                .sum();
        return BigDecimal.valueOf(totalLoggedMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalIncentive(UUID userId) {
        List<Log> logs = logService.getLogsByUser(userId);
        long totalLoggedMinutes = logs.stream()
                .map(log -> Duration.between(log.getWaktuMulai(), log.getWaktuSelesai()))
                .mapToLong(Duration::toMinutes)
                .sum();

        BigDecimal totalHours = BigDecimal.valueOf(totalLoggedMinutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        return totalHours.multiply(BigDecimal.valueOf(27500));
    }

    private LowonganDTO convertToLowonganDTO(Lowongan lowongan) {
        return lowonganMapper.toDto(lowongan);
    }

    private CompletableFuture<BigDecimal> calculateTotalLoggedHoursAsync(UUID userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Log> logs = logService.getLogsByUser(userId);
            long totalLoggedMinutes = logs.stream()
                    .map(log -> Duration.between(log.getWaktuMulai(), log.getWaktuSelesai()))
                    .mapToLong(Duration::toMinutes)
                    .sum();
            return BigDecimal.valueOf(totalLoggedMinutes)
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        });
    }

    private CompletableFuture<BigDecimal> calculateTotalIncentiveAsync(UUID userId) {
        return calculateTotalLoggedHoursAsync(userId)
                .thenApply(hours -> hours.multiply(BigDecimal.valueOf(27500)));
    }


}