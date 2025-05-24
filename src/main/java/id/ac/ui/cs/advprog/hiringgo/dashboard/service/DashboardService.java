package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;

import java.util.UUID;

public interface DashboardService {
    DashboardResponse getDashboardData(UUID userId);
}