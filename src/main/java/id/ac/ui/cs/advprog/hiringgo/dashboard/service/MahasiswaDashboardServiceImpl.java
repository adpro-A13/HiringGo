package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.MahasiswaDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation.IncentiveCalculationService;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.data.ApplicationDataService;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature.MahasiswaFeatureProvider;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.validation.MahasiswaValidationService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class MahasiswaDashboardServiceImpl extends AbstractDashboardService {

    private final MahasiswaValidationService validationService;
    private final IncentiveCalculationService calculationService;
    private final ApplicationDataService applicationDataService;
    private final LowonganRepository lowonganRepository;
    private final MahasiswaFeatureProvider featureProvider;

    @Autowired
    public MahasiswaDashboardServiceImpl(
            MahasiswaValidationService validationService,
            IncentiveCalculationService calculationService,
            ApplicationDataService applicationDataService,
            LowonganRepository lowonganRepository,
            MahasiswaFeatureProvider featureProvider) {
        this.validationService = validationService;
        this.calculationService = calculationService;
        this.applicationDataService = applicationDataService;
        this.lowonganRepository = lowonganRepository;
        this.featureProvider = featureProvider;
    }

    @Override
    protected void validateUser(UUID userId) {
        validationService.validateMahasiswa(userId);
    }

    @Override
    protected DashboardResponse createDashboardResponse() {
        return new MahasiswaDashboardResponse();
    }

    @Override
    protected void populateCommonData(UUID userId, DashboardResponse response) {
        Mahasiswa mahasiswa = validationService.getMahasiswaById(userId);

        response.setUserRole("MAHASISWA");
        response.setUsername(mahasiswa.getUsername());
        response.setFullName(mahasiswa.getFullName());
        response.setAvailableFeatures(featureProvider.getAvailableFeatures());
    }

    @Override
    protected void populateRoleSpecificData(UUID userId, DashboardResponse baseResponse) {
        MahasiswaDashboardResponse response = (MahasiswaDashboardResponse) baseResponse;

        CompletableFuture<BigDecimal> hoursFuture = calculationService.calculateTotalLoggedHoursAsync(userId);
        CompletableFuture<BigDecimal> incentiveFuture = calculationService.calculateTotalIncentiveAsync(userId);

        List<Lowongan> allLowongan = lowonganRepository.findAll();
        List<Lowongan> openLowongan = lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA);

        List<Pendaftaran> allApplications = applicationDataService.getAllApplications(userId);

        response.setTotalLowonganCount(allLowongan.size());
        response.setOpenLowonganCount(openLowongan.size());
        response.setTotalApplicationsCount(allApplications.size());

        response.setPendingApplicationsCount(
                applicationDataService.countApplicationsByStatus(allApplications, StatusPendaftaran.BELUM_DIPROSES));
        response.setAcceptedApplicationsCount(
                applicationDataService.countApplicationsByStatus(allApplications, StatusPendaftaran.DITERIMA));
        response.setRejectedApplicationsCount(
                applicationDataService.countApplicationsByStatus(allApplications, StatusPendaftaran.DITOLAK));

        response.setAcceptedLowongan(applicationDataService.getAcceptedLowongan(userId));

        response.setTotalLoggedHours(hoursFuture.join());
        response.setTotalIncentive(incentiveFuture.join());
    }
}