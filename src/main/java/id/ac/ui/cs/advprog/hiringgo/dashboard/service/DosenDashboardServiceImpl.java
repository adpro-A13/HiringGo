package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DosenDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation.DosenStatisticsService;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.data.DosenCourseDataService;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature.DosenFeatureProvider;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.validation.DosenValidationService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DosenDashboardServiceImpl extends AbstractDashboardService {

    private final DosenValidationService validationService;
    private final DosenCourseDataService courseDataService;
    private final DosenStatisticsService statisticsService;
    private final DosenFeatureProvider featureProvider;

    @Autowired
    public DosenDashboardServiceImpl(
            DosenValidationService validationService,
            DosenCourseDataService courseDataService,
            DosenStatisticsService statisticsService,
            DosenFeatureProvider featureProvider) {
        this.validationService = validationService;
        this.courseDataService = courseDataService;
        this.statisticsService = statisticsService;
        this.featureProvider = featureProvider;
    }

    @Override
    protected void validateUser(UUID userId) {
        validationService.validateDosen(userId);
    }

    @Override
    protected DashboardResponse createDashboardResponse() {
        return new DosenDashboardResponse();
    }

    @Override
    protected void populateCommonData(UUID userId, DashboardResponse response) {
        Dosen dosen = validationService.getDosenById(userId);

        response.setUserRole("DOSEN");
        response.setUsername(dosen.getUsername());
        response.setFullName(dosen.getFullName());
        response.setAvailableFeatures(featureProvider.getAvailableFeatures());
    }

    @Override
    protected void populateRoleSpecificData(UUID userId, DashboardResponse dashboardResponse) {
        DosenDashboardResponse response = (DosenDashboardResponse) dashboardResponse;

        Dosen dosen = validationService.getDosenById(userId);
        List<MataKuliah> coursesTaught = courseDataService.getCoursesTaughtByDosen(dosen);

        if (coursesTaught.isEmpty()) {
            setEmptyResponseData(response);
            return;
        }

        List<MataKuliahDTO> coursesDTO = courseDataService.convertCoursesToDTO(coursesTaught);
        response.setCoursesTaught(coursesDTO);

        Map<String, List<LowonganDTO>> lowonganPerCourse = courseDataService
                .mapLowonganToCoursesAsync(coursesTaught).join();
        response.setLowonganPerCourse(lowonganPerCourse);

        Map<String, Integer> acceptedAssistantsPerCourse = statisticsService
                .countAcceptedAssistantsPerCourse(coursesTaught);
        response.setAcceptedAssistantsPerCourse(acceptedAssistantsPerCourse);

        response.setCourseCount(coursesTaught.size());
        response.setAcceptedAssistantCount(statisticsService.getTotalAcceptedAssistants(coursesTaught));
        response.setOpenPositionCount(statisticsService.getTotalOpenPositions(coursesTaught));
    }

    private void setEmptyResponseData(DosenDashboardResponse response) {
        response.setCoursesTaught(Collections.emptyList());
        response.setLowonganPerCourse(Collections.emptyMap());
        response.setAcceptedAssistantsPerCourse(Collections.emptyMap());
        response.setCourseCount(0);
        response.setAcceptedAssistantCount(0);
        response.setOpenPositionCount(0);
    }
}