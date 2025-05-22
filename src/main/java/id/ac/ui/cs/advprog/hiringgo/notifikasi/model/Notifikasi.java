package id.ac.ui.cs.advprog.hiringgo.notifikasi.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notifikasi {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mahasiswa_id")
    private Mahasiswa mahasiswa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mata_kuliah_kode")
    private MataKuliah mataKuliah;

    @Column(nullable = false)
    private String tahunAjaran;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semester semester;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private boolean read = false;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Notifikasi() {}

    public Notifikasi(Mahasiswa mahasiswa, MataKuliah mataKuliah,
                      String tahunAjaran, Semester semester, String status) {
        this.mahasiswa = mahasiswa;
        this.mataKuliah = mataKuliah;
        this.tahunAjaran = tahunAjaran;
        this.semester = semester;
        this.status = status;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public Notifikasi(Mahasiswa mahasiswa, String status, boolean isRead) {
        this.mahasiswa = mahasiswa;
        this.status = status;
        this.read = isRead;
    }

}
