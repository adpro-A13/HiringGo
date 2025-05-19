package id.ac.ui.cs.advprog.hiringgo.log.model;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
    private LogStatus status;

    private Log(Builder builder) {
        this.id = builder.id;
        this.judul = builder.judul;
        this.keterangan = builder.keterangan;
        this.kategori = builder.kategori;
        this.waktuMulai = builder.waktuMulai;
        this.waktuSelesai = builder.waktuSelesai;
        this.tanggalLog = builder.tanggalLog;
        this.pesanUntukDosen = builder.pesanUntukDosen;
        this.status = builder.status;
    }

    public static class Builder {
        private Long id;
        private String judul;
        private String keterangan;
        private LogKategori kategori;
        private LocalTime waktuMulai;
        private LocalTime waktuSelesai;
        private LocalDate tanggalLog;
        private String pesanUntukDosen;
        private LogStatus status = LogStatus.MENUNGGU; // default status

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder judul(String judul) {
            this.judul = judul;
            return this;
        }

        public Builder keterangan(String keterangan) {
            this.keterangan = keterangan;
            return this;
        }

        public Builder kategori(LogKategori kategori) {
            this.kategori = kategori;
            return this;
        }

        public Builder waktuMulai(LocalTime waktuMulai) {
            this.waktuMulai = waktuMulai;
            return this;
        }

        public Builder waktuSelesai(LocalTime waktuSelesai) {
            this.waktuSelesai = waktuSelesai;
            return this;
        }

        public Builder tanggalLog(LocalDate tanggalLog) {
            this.tanggalLog = tanggalLog;
            return this;
        }

        public Builder pesanUntukDosen(String pesanUntukDosen) {
            this.pesanUntukDosen = pesanUntukDosen;
            return this;
        }

        public Builder status(LogStatus status) {
            this.status = status;
            return this;
        }

        public Log build() {
            return new Log(this);
        }
    }

    public void validate() {
        if (waktuMulai == null || waktuSelesai == null) {
            throw new IllegalArgumentException("Waktu mulai dan selesai harus diisi.");
        }
        if (!waktuMulai.isBefore(waktuSelesai)) {
            throw new IllegalArgumentException("Waktu selesai harus setelah waktu mulai.");
        }
    }
}