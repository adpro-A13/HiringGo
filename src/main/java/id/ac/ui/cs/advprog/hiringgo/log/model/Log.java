package id.ac.ui.cs.advprog.hiringgo.log.model;

import enums.log.LogStatus;
import enums.log.LogKategori;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Log {
    private String judul;
    private String keterangan;
    private LogKategori kategori;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private LocalDate tanggalLog;
    private String pesanUntukDosen;
    private LogStatus status = LogStatus.MENUNGGU;

    public void validate() {
        if (waktuMulai == null || waktuSelesai == null) {
            throw new IllegalArgumentException("Waktu mulai dan selesai harus diisi.");
        }
        if (waktuSelesai.isBefore(waktuMulai)) {
            throw new IllegalArgumentException("Waktu selesai harus setelah waktu mulai.");
        }
    }
}