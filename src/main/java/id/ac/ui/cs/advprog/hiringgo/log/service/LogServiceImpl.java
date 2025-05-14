package id.ac.ui.cs.advprog.hiringgo.log.service;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.repository.LogRepository;
import java.util.Optional;

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
    public List<Log> getLogsByMonth(int bulan, int tahun) {
        LocalDate from = LocalDate.of(tahun, bulan, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
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

    @Override
    public Optional<Log> getLogById(Long id) {
        return logRepository.findById(id);
    }

    @Override
    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }

    @Override
    public Log updateLog(Long id, Log updatedLog) {
        validateLogTime(updatedLog);

        Log existing = logRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log tidak ditemukan"));

        existing.setJudul(updatedLog.getJudul());
        existing.setKategori(updatedLog.getKategori());
        existing.setTanggalLog(updatedLog.getTanggalLog());
        existing.setWaktuMulai(updatedLog.getWaktuMulai());
        existing.setWaktuSelesai(updatedLog.getWaktuSelesai());
        // status tidak diubah di sini

        return logRepository.save(existing);
    }

    @Override
    public void deleteLog(Long id) {
        logRepository.deleteById(id);
    }
}
