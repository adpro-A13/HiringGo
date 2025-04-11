package id.ac.ui.cs.advprog.hiringgo.controller;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public Log createLog(@RequestBody Log log) {
        return logService.createLog(log);
    }

    @GetMapping("/status/{status}")
    public List<Log> getLogsByStatus(@PathVariable LogStatus status) {
        return logService.getLogsByStatus(status);
    }

    @GetMapping("/tanggal")
    public List<Log> getLogsByTanggal(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return logService.getLogsByTanggal(from, to);
    }

    @PutMapping("/{id}/status/{status}")
    public Log updateStatus(@PathVariable Long id, @PathVariable LogStatus status) {
        return logService.updateStatus(id, status);
    }
}
