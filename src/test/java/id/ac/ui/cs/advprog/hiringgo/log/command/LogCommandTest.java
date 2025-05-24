package id.ac.ui.cs.advprog.hiringgo.log.command;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class LogCommandTest {

    @Test
    void testUpdateStatusCommand() {
        LogService mockService = mock(LogService.class);

        Log updatedLog = new Log.Builder()
                .id(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"))
                .status(LogStatus.DITERIMA)
                .build();

        when(mockService.updateStatus(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"), LogStatus.DITERIMA)).thenReturn(updatedLog);

        UpdateStatusCommand cmd = new UpdateStatusCommand(mockService, UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"), LogStatus.DITERIMA);
        Object result = cmd.execute();

        assertTrue(result instanceof Log);
        assertEquals(LogStatus.DITERIMA, ((Log) result).getStatus());
    }
}
