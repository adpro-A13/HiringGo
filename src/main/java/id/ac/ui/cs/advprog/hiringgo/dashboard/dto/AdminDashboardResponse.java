package id.ac.ui.cs.advprog.hiringgo.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDashboardResponse extends DashboardResponse {
    private int dosenCount;
    private int mahasiswaCount;
    private int courseCount;
    private int lowonganCount;
}