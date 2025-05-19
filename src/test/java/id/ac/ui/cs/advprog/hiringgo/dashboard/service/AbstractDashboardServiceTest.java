package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AbstractDashboardServiceTest {

    static class TestService extends AbstractDashboardService {
        boolean validateCalled;
        boolean commonCalled;
        boolean roleCalled;
        final DashboardResponse dummyResponse = new DashboardResponse();

        @Override
        protected void validateUser(UUID userId) {
            validateCalled = true;
        }

        @Override
        protected DashboardResponse createDashboardResponse() {
            return dummyResponse;
        }

        @Override
        protected void populateCommonData(UUID userId, DashboardResponse response) {
            commonCalled = true;
        }

        @Override
        protected void populateRoleSpecificData(UUID userId, DashboardResponse response) {
            roleCalled = true;
        }
    }

    private TestService service;
    private UUID userId;

    @BeforeEach
    void setup() {
        service = new TestService();
        userId = UUID.randomUUID();
    }

    @Test
    void testGetDashboardDataHappyPath() {
        DashboardResponse response = service.getDashboardData(userId);

        assertSame(service.dummyResponse, response, "Should return the response from createDashboardResponse");
        assertTrue(service.validateCalled, "validateUser must be called");
        assertTrue(service.commonCalled, "populateCommonData must be called");
        assertTrue(service.roleCalled, "populateRoleSpecificData must be called");
    }

    @Test
    void testGetDashboardDataValidateUserThrows() {
        // Create a service that fails validation
        service = new TestService() {
            @Override
            protected void validateUser(UUID userId) {
                throw new IllegalArgumentException("Invalid user");
            }
        };

        assertThrows(IllegalArgumentException.class, () -> service.getDashboardData(userId));
    }
}
