package id.ac.ui.cs.advprog.hiringgo.log.model;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import jakarta.persistence.*;

@Getter @Setter @Entity @Data
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String judul;
    private String keterangan;

    @Enumerated(EnumType.STRING)
    private LogKategori kategori;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private LocalDate tanggalLog;
    private String pesanUntukDosen;

    @Enumerated(EnumType.STRING)
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