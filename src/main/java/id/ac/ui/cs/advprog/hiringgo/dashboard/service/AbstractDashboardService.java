package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public abstract class AbstractDashboardService implements DashboardService {

    @Override
    public final DashboardResponse getDashboardData(UUID userId) {
        validateUser(userId);
        DashboardResponse response = createDashboardResponse();
        populateCommonData(userId, response);
        populateRoleSpecificData(userId, response);
        return response;
    }

    protected abstract void validateUser(UUID userId);
    protected abstract DashboardResponse createDashboardResponse();
    protected abstract void populateCommonData(UUID userId, DashboardResponse response);

    protected abstract void populateRoleSpecificData(UUID userId, DashboardResponse response);
}