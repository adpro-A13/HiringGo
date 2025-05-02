package id.ac.ui.cs.advprog.hiringgo.matakuliah.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@EqualsAndHashCode(of = "kode")
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "mata_kuliah")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MataKuliah {
    @Id
    @ToString.Include
    private String kode;

    @Setter
    private String nama;

    @Setter
    private String deskripsi;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "mata_kuliah_dosen_pengampu",
            joinColumns = @JoinColumn(name = "matkul_kode"))
    @Column(name = "dosen_pengampu")
    private List<String> dosenPengampu = new ArrayList<>();

    public MataKuliah(String kode, String nama, String deskripsi) {
        if (kode == null || kode.isBlank())
            throw new IllegalArgumentException("Kode Mata Kuliah harus diisi");
        if (nama == null || nama.isBlank())
            throw new IllegalArgumentException("Harap sertakan nama mata kuliah");

        this.kode = kode;
        this.nama = nama;
        this.deskripsi = deskripsi;
    }

    public MataKuliah addDosenPengampu(String dosen) {
        if (dosen == null || dosen.isBlank())
            throw new IllegalArgumentException("Nama dosen tidak boleh kosong");
        this.dosenPengampu.add(dosen);
        return this;
    }

    public List<String> getDosenPengampu() {
        return Collections.unmodifiableList(dosenPengampu);
    }
}