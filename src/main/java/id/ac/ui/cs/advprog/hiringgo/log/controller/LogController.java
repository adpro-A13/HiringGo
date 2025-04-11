package id.ac.ui.cs.advprog.hiringgo.log.controller;

import id.ac.ui.cs.advprog.hiringgo.log.dto.request.CreateLogRequest;
import id.ac.ui.cs.advprog.hiringgo.log.dto.response.BaseResponse;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public ResponseEntity<BaseResponse<Log>> createLog(@RequestBody CreateLogRequest request) {
        Log log = new Log.Builder()
                .judul(request.getJudul())
                .keterangan(request.getKeterangan())
                .kategori(request.getKategori())
                .waktuMulai(request.getWaktuMulai())
                .waktuSelesai(request.getWaktuSelesai())
                .tanggalLog(request.getTanggalLog())
                .pesanUntukDosen(request.getPesanUntukDosen())
                .build();

        Log savedLog = logService.createLog(log);
        return ResponseEntity.ok(new BaseResponse<>("success", savedLog));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<BaseResponse<List<Log>>> getLogsByStatus(@PathVariable LogStatus status) {
        return ResponseEntity.ok(new BaseResponse<>("success", logService.getLogsByStatus(status)));
    }

    @GetMapping("/tanggal")
    public ResponseEntity<BaseResponse<List<Log>>> getLogsByTanggal(@RequestParam LocalDate from,
                                                                    @RequestParam LocalDate to) {
        return ResponseEntity.ok(new BaseResponse<>("success", logService.getLogsByTanggal(from, to)));
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<BaseResponse<Log>> updateStatus(@PathVariable Long id,
                                                          @PathVariable LogStatus status) {
        Log updatedLog = logService.updateStatus(id, status);
        return ResponseEntity.ok(new BaseResponse<>("success", updatedLog));
    }
}
