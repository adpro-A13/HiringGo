package id.ac.ui.cs.advprog.hiringgo.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DashboardResponse {
    private String userRole;
    private String username;
    private String fullName;
    private Map<String, String> availableFeatures;
}