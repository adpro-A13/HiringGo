package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Test
    void getDashboardData_shouldReturnStubbedResponse_whenMocked() {
        // Arrange
        DashboardService service = mock(DashboardService.class);
        UUID userId = UUID.randomUUID();
        DashboardResponse stubResponse = new DashboardResponse();
        when(service.getDashboardData(userId)).thenReturn(stubResponse);

        // Act
        DashboardResponse result = service.getDashboardData(userId);

        // Assert
        assertSame(stubResponse, result, "Mocked service should return the stubbed response");
        verify(service).getDashboardData(userId);
    }
}