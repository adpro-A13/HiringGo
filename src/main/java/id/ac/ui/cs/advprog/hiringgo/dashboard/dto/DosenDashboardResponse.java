package id.ac.ui.cs.advprog.hiringgo.dashboard.dto;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO; // Changed import
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DosenDashboardResponse extends DashboardResponse {
    private int courseCount;
    private int acceptedAssistantCount;
    private int openPositionCount;
    private List<MataKuliahDTO> courses;
    private List<LowonganDTO> openPositions; // Changed type
}