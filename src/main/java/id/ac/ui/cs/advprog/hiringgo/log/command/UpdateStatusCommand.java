package id.ac.ui.cs.advprog.hiringgo.log.command;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;

import java.util.UUID;

public class UpdateStatusCommand implements LogCommand {
    private final LogService logService;
    private final UUID id;
    private final LogStatus status;

    public UpdateStatusCommand(LogService logService, UUID id, LogStatus status) {
        this.logService = logService;
        this.id = id;
        this.status = status;
    }

    @Override
    public Log execute() {
        return logService.updateStatus(id, status);
    }
}

