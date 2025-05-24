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
            if (response != null) {
                response.setUserRole("TEST_ROLE");
            } else {
                response.setUserRole("TEST_ROLE");
            }
            commonCalled = true;
        }

        @Override
        protected void populateRoleSpecificData(UUID userId, DashboardResponse response) {
            if (response != null) {
                response.setUsername("test-user");
            } else {
                response.setUsername("test-user");
            }
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
        service = new TestService() {
            @Override
            protected void validateUser(UUID userId) {
                throw new IllegalArgumentException("Invalid user");
            }
        };

        assertThrows(IllegalArgumentException.class, () -> service.getDashboardData(userId));
    }

    @Test
    void testGetDashboardDataWithNullUserId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDashboardData(null)
        );

        assertEquals("User ID tidak boleh null", exception.getMessage());
        assertFalse(service.validateCalled, "validateUser should not be called with null userId");
        assertFalse(service.commonCalled, "populateCommonData should not be called after validation fails");
        assertFalse(service.roleCalled, "populateRoleSpecificData should not be called after validation fails");
    }

    @Test
    void testCreateResponseAndPopulateWithoutCallingParentMethod() {
        DashboardResponse response = service.createDashboardResponse();
        assertSame(service.dummyResponse, response);
        service.populateCommonData(userId, response);
        assertTrue(service.commonCalled);
        service.commonCalled = false;
        assertThrows(NullPointerException.class, () -> service.populateCommonData(userId, null));
        assertFalse(service.commonCalled);
        service.populateRoleSpecificData(userId, response);
        assertTrue(service.roleCalled);
        service.roleCalled = false;
        assertThrows(NullPointerException.class, () -> service.populateRoleSpecificData(userId, null));
        assertFalse(service.roleCalled);
    }
}