package id.ac.ui.cs.advprog.hiringgo.log.command;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;

public class UpdateStatusCommand implements LogCommand {
    private final LogService logService;
    private final Long id;
    private final LogStatus status;

    public UpdateStatusCommand(LogService logService, Long id, LogStatus status) {
        this.logService = logService;
        this.id = id;
        this.status = status;
    }

    @Override
    public Object execute() {
        return logService.updateStatus(id, status);
    }
}

