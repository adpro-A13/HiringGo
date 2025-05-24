package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.AdminDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation.AdminStatisticsService;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature.AdminFeatureProvider;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.validation.AdminValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AdminDashboardServiceImpl extends AbstractDashboardService {

    private final AdminValidationService validationService;
    private final AdminStatisticsService statisticsService;
    private final AdminFeatureProvider featureProvider;

    @Autowired
    public AdminDashboardServiceImpl(
            AdminValidationService validationService,
            AdminStatisticsService statisticsService,
            AdminFeatureProvider featureProvider) {
        this.validationService = validationService;
        this.statisticsService = statisticsService;
        this.featureProvider = featureProvider;
    }

    @Override
    protected void validateUser(UUID userId) {
        validationService.validateAdmin(userId);
    }

    @Override
    protected DashboardResponse createDashboardResponse() {
        return new AdminDashboardResponse();
    }

    @Override
    protected void populateCommonData(UUID userId, DashboardResponse response) {
        Admin admin = validationService.getAdminById(userId);

        response.setUserRole("ADMIN");
        response.setUsername(admin.getUsername());
        response.setFullName(admin.getUsername());
        response.setAvailableFeatures(featureProvider.getAvailableFeatures());
    }

    @Override
    protected void populateRoleSpecificData(UUID userId, DashboardResponse baseResponse) {
        AdminDashboardResponse response = (AdminDashboardResponse) baseResponse;

        CompletableFuture<Integer> dosenCountFuture = statisticsService.countDosenAsync();
        CompletableFuture<Integer> mahasiswaCountFuture = statisticsService.countMahasiswaAsync();
        CompletableFuture<Integer> courseCountFuture = statisticsService.countCoursesAsync();
        CompletableFuture<Integer> lowonganCountFuture = statisticsService.countLowonganAsync();

        response.setDosenCount(dosenCountFuture.join());
        response.setMahasiswaCount(mahasiswaCountFuture.join());
        response.setCourseCount(courseCountFuture.join());
        response.setLowonganCount(lowonganCountFuture.join());
    }
}