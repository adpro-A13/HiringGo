package id.ac.ui.cs.advprog.hiringgo.log.service;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.repository.LogRepository;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public Log createLog(Log log) {
        validateLogTime(log);
        log.setStatus(LogStatus.MENUNGGU);
        return logRepository.save(log);
    }

    @Override
    public List<Log> getLogsByStatus(LogStatus status) {
        return logRepository.findByStatus(status);
    }

    @Override
    public List<Log> getLogsByTanggal(LocalDate from, LocalDate to) {
        return logRepository.findByTanggalLogBetween(from, to);
    }

    @Override
    public Log updateStatus(Long id, LogStatus status) {
        Log log = logRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log tidak ditemukan"));
        log.setStatus(status);
        return logRepository.save(log);
    }

    private void validateLogTime(Log log) {
        if (log.getWaktuMulai() == null || log.getWaktuSelesai() == null) {
            throw new IllegalArgumentException("Waktu mulai dan selesai tidak boleh kosong");
        }
        if (!log.getWaktuMulai().isBefore(log.getWaktuSelesai())) {
            throw new IllegalArgumentException("Waktu mulai harus sebelum waktu selesai");
        }
    }
}
