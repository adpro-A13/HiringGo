package id.ac.ui.cs.advprog.hiringgo.log.controller;

import id.ac.ui.cs.advprog.hiringgo.log.command.LogCommand;
import id.ac.ui.cs.advprog.hiringgo.log.command.LogCommandInvoker;
import id.ac.ui.cs.advprog.hiringgo.log.command.UpdateStatusCommand;
import id.ac.ui.cs.advprog.hiringgo.log.dto.request.CreateLogRequest;
import id.ac.ui.cs.advprog.hiringgo.log.dto.response.LowonganWithPendaftaranDTO;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequestMapping("/api/logs")
@RestController
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PreAuthorize("hasAuthority('MAHASISWA')")
    @PostMapping
    public ResponseEntity<?> createLog(@RequestBody CreateLogRequest createLogRequest) {
        try {
            Log createdLog = logService.createLog(createLogRequest);
            return ResponseEntity.ok(createdLog);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating log");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Log>> getAllLogs() {
        List<Log> logs = logService.getAllLogs();
        return ResponseEntity.ok(logs);
    }

    @PreAuthorize("hasAuthority('DOSEN')")
    @GetMapping("/dosen/{dosenId}")
    public ResponseEntity<?> getLogsByDosenMataKuliah(@PathVariable UUID dosenId) {
        try {
            List<Log> logs = logService.getLogsByDosenMataKuliah(dosenId);
            return ResponseEntity.ok(logs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('MAHASISWA')")
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Log>> getLogsByUser(@PathVariable UUID id) {
        List<Log> logs = logService.getLogsByUser(id);
        return ResponseEntity.ok(logs);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getLogById(@PathVariable UUID id) {
        Optional<Log> log = logService.getLogById(id);
        if (log.isPresent()) {
            return ResponseEntity.ok(log.get());
        } else {
            return ResponseEntity.status(404).body("Log not found");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Log>> getLogsByStatus(@PathVariable LogStatus status) {
        List<Log> logs = logService.getLogsByStatus(status);
        return ResponseEntity.ok(logs);
    }

    @PreAuthorize("hasAuthority('MAHASISWA')")
    @GetMapping("/month")
    public ResponseEntity<List<Log>> getLogsByMonth(
            @RequestParam UUID id,
            @RequestParam int bulan,
            @RequestParam int tahun) {
        try {
            List<Log> logs = logService.getLogsByMonth(bulan, tahun, id).get();
            return ResponseEntity.ok(logs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body(null);
        }catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PreAuthorize("hasAuthority('DOSEN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateLogStatus(@PathVariable UUID id, @RequestBody LogStatus status) {
        try {
            LogCommand cmd = new UpdateStatusCommand(logService, id, status);
            LogCommandInvoker cmdInvoker= new LogCommandInvoker();
            cmdInvoker.setCommand(cmd);
            Log updatedLog = cmdInvoker.run();
            return ResponseEntity.ok(updatedLog);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('MAHASISWA')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLog(@PathVariable UUID id, @RequestBody Log updatedLog) {
        try {
            Log log = logService.updateLog(id, updatedLog);
            return ResponseEntity.ok(log);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('MAHASISWA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLog(@PathVariable UUID id) {
        try {
            logService.deleteLog(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('MAHASISWA')")
    @GetMapping("/listLowongan")
    public ResponseEntity<List<LowonganWithPendaftaranDTO>> getLowonganYangDiterima() {
        List<LowonganWithPendaftaranDTO> lowongan = logService.getLowonganYangDiterima();
        return ResponseEntity.ok(lowongan);
    }

}
