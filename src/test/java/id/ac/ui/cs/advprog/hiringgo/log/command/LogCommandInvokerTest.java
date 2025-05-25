package id.ac.ui.cs.advprog.hiringgo.log.command;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class LogCommandInvokerTest {

    private LogCommandInvoker invoker;
    private LogService mockLogService;

    @BeforeEach
    void setUp() {
        invoker = new LogCommandInvoker();
        mockLogService = mock(LogService.class);
    }

    @Test
    void testInvokerWithUpdateStatusCommand() {
        // Arrange
        UUID logId = UUID.randomUUID();
        LogStatus newStatus = LogStatus.DITERIMA;

        Log expectedLog = new Log.Builder()
                .id(logId)
                .status(newStatus)
                .build();

        when(mockLogService.updateStatus(logId, newStatus)).thenReturn(expectedLog);

        LogCommand command = new UpdateStatusCommand(mockLogService, logId, newStatus);

        // Act
        invoker.setCommand(command);
        Log result = invoker.run();

        // Assert
        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        assertEquals(logId, result.getId());
    }

    @Test
    void testInvokerWithNullCommand() {
        // No command set
        Exception exception = assertThrows(NullPointerException.class, () -> invoker.run());
        assertNotNull(exception);
    }
}