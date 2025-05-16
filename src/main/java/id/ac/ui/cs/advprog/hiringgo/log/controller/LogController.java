package id.ac.ui.cs.advprog.hiringgo.log.controller;

import id.ac.ui.cs.advprog.hiringgo.log.command.LogCommand;
import id.ac.ui.cs.advprog.hiringgo.log.command.LogCommandInvoker;
import id.ac.ui.cs.advprog.hiringgo.log.command.UpdateStatusCommand;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequestMapping("/logs")
@RestController
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping
    public ResponseEntity<?> createLog(@RequestBody Log log) {
        try {
            Log createdLog = logService.createLog(log);
            return ResponseEntity.ok(createdLog);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating log");
        }
    }

    @GetMapping
    public ResponseEntity<List<Log>> getAllLogs() {
        List<Log> logs = logService.getAllLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLogById(@PathVariable Long id) {
        Optional<Log> log = logService.getLogById(id);
        if (log.isPresent()) {
            return ResponseEntity.ok(log.get());
        } else {
            return ResponseEntity.status(404).body("Log not found");
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Log>> getLogsByStatus(@PathVariable LogStatus status) {
        List<Log> logs = logService.getLogsByStatus(status);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/month")
    public ResponseEntity<List<Log>> getLogsByMonth(
            @RequestParam int bulan,
            @RequestParam int tahun) {
        List<Log> logs = logService.getLogsByMonth(bulan, tahun);
        return ResponseEntity.ok(logs);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateLogStatus(@PathVariable Long id, @RequestBody LogStatus status) {
        try {
            LogCommand cmd = new UpdateStatusCommand(logService, id, status);
            LogCommandInvoker cmdInvoker= new LogCommandInvoker();
            cmdInvoker.setCommand(cmd);
            Log updatedLog = cmdInvoker.run();
            return ResponseEntity.ok(updatedLog);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating log status");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLog(@PathVariable Long id, @RequestBody Log updatedLog) {
        try {
            Log log = logService.updateLog(id, updatedLog);
            return ResponseEntity.ok(log);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating log");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLog(@PathVariable Long id) {
        try {
            logService.deleteLog(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting log");
        }
    }
}
