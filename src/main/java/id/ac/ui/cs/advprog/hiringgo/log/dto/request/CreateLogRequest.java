package id.ac.ui.cs.advprog.hiringgo.log.dto.request;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class CreateLogRequest {
    private String judul;
    private String pendaftaran;
    private UUID user;
    private LogKategori kategori;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private LocalDate tanggalLog;
    private String keterangan;
    private String pesanUntukDosen;
}
