package id.ac.ui.cs.advprog.hiringgo.dashboard.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO; // Changed import
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class MahasiswaDashboardResponse extends DashboardResponse {
    private int openLowonganCount;
    private int totalLowonganCount;
    private int totalApplicationsCount;
    private int pendingApplicationsCount;
    private int acceptedApplicationsCount;
    private int rejectedApplicationsCount;
    private int totalLoggedHours;
    private BigDecimal totalIncentive;
    private List<LowonganDTO> acceptedLowongan;
    private List<LowonganDTO> recentLowongan;
}