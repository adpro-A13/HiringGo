package id.ac.ui.cs.advprog.hiringgo.log.service;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LogService {
    Log createLog(Log log);

    Optional<Log> getLogById(Long id);
    List<Log> getAllLogs();
    List<Log> getLogsByStatus(LogStatus status);
    List<Log> getLogsByTanggal(LocalDate from, LocalDate to);

    Log updateStatus(Long id, LogStatus status);
    Log updateLog(Long id, Log updatedLog);

    void deleteLog(Long id);
}
