package id.ac.ui.cs.advprog.hiringgo.log.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.log.dto.request.CreateLogRequest;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.repository.LogRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;
    private final PendaftaranRepository pendaftaranRepository;
    private final UserRepository userRepository;

    public LogServiceImpl(LogRepository logRepository, PendaftaranRepository pendaftaranRepository, UserRepository userRepository) {
        this.logRepository = logRepository;
        this.pendaftaranRepository = pendaftaranRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Log createLog(CreateLogRequest request) {
        Pendaftaran pendaftaran = pendaftaranRepository.findById(UUID.fromString(request.getPendaftaran())).orElseThrow(()->new RuntimeException("Pendaftaran Not Found"));
        User user = userRepository.findById(request.getUser()).orElseThrow(()->new RuntimeException("User Not Found"));

        Log log = new Log.Builder()
                .pendaftaran(pendaftaran)
                .user(user)
                .judul(request.getJudul())
                .kategori(request.getKategori())
                .waktuMulai(request.getWaktuMulai())
                .waktuSelesai(request.getWaktuSelesai())
                .tanggalLog(request.getTanggalLog())
                .keterangan(request.getKeterangan())
                .pesanUntukDosen(request.getPesanUntukDosen())
                .build();
        validateLogTime(log);
        log.setStatus(LogStatus.MENUNGGU);
        return logRepository.save(log);
    }

    @Override
    public List<Log> getLogsByStatus(LogStatus status) {
        return logRepository.findByStatus(status);
    }

    @Async
    @Override
    public CompletableFuture<List<Log>> getLogsByMonth(int bulan, int tahun, UUID id) {
        LocalDate from = LocalDate.of(tahun, bulan, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
        List<Log> result = logRepository.findByTanggalLogBetweenAndUser_Id(from, to, id);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public List<Log> getLogsByDosenMataKuliah(UUID dosenId) {
        return logRepository.findLogsByDosenMataKuliah(dosenId);
    }

    @Override
    public List<Log> getLogsByUser(UUID idUser) {
        return logRepository.findByUserId(idUser);
    }


    @Override
    public Log updateStatus(UUID id, LogStatus status) {
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
    public Optional<Log> getLogById(UUID id) {
        return logRepository.findById(id);
    }

    @Override
    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }

    @Override
    public Log updateLog(UUID id, Log updatedLog) {
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

    @Async
    @Override
    public void deleteLog(UUID id) {
        logRepository.deleteById(id);
    }

    @Override
    public List<Pendaftaran> getLowonganYangDiterima(UUID kandidatId) {
        List<Pendaftaran> semuaPendaftaran = pendaftaranRepository.findByKandidatId(kandidatId);

        return semuaPendaftaran.stream()
                .filter(pendaftaran -> pendaftaran.getStatus() == StatusPendaftaran.DITERIMA)
                .toList();
    }
}
