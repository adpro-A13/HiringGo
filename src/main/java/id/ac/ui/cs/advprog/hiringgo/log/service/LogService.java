package id.ac.ui.cs.advprog.hiringgo.log.service;

import id.ac.ui.cs.advprog.hiringgo.log.dto.request.CreateLogRequest;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LogService {
    Log createLog(CreateLogRequest request);

    Optional<Log> getLogById(Long id);
    List<Log> getAllLogs();
    List<Log> getLogsByStatus(LogStatus status);
    CompletableFuture<List<Log>> getLogsByMonth(int bulan, int tahun, UUID id);
    List<Log> getLogsByPendaftaran(String kode);
    List<Log> getLogsByUser(UUID idUser);

    Log updateStatus(Long id, LogStatus status);
    Log updateLog(Long id, Log updatedLog);

    void deleteLog(Long id);
}
