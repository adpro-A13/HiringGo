package id.ac.ui.cs.advprog.hiringgo.command;

import id.ac.ui.cs.advprog.hiringgo.log.command.UpdateStatusCommand;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class LogCommandTest {
    @Test
    void testUpdateStatusCommand() {
        LogService mockService = mock(LogService.class);
        Log updatedLog = new Log();
        updatedLog.setId(1L);
        updatedLog.setStatus(LogStatus.DITERIMA);

        when(mockService.updateStatus(1L, LogStatus.DITERIMA)).thenReturn(updatedLog);

        UpdateStatusCommand cmd = new UpdateStatusCommand(mockService, 1L, LogStatus.DITERIMA);
        Object result = cmd.execute();

        assertTrue(result instanceof Log);
        assertEquals(LogStatus.DITERIMA, ((Log) result).getStatus());
    }

}
