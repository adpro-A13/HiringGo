package id.ac.ui.cs.advprog.hiringgo.matakuliah.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "mata_kuliah_dosen",
            joinColumns = @JoinColumn(name = "matkul_kode"),
            inverseJoinColumns = @JoinColumn(name = "dosen_id")
    )
    private Set<Dosen> dosenPengampu = new HashSet<>();

    public MataKuliah(String kode, String nama, String deskripsi) {
        if (kode == null || kode.isBlank())
            throw new IllegalArgumentException("Kode Mata Kuliah harus diisi");
        if (nama == null || nama.isBlank())
            throw new IllegalArgumentException("Harap sertakan nama mata kuliah");

        this.kode = kode;
        this.nama = nama;
        this.deskripsi = deskripsi;
    }

    public MataKuliah addDosenPengampu(Dosen dosen) {
        if (dosen == null || dosen.getNip().isBlank())
            throw new IllegalArgumentException("Dosen tidak boleh null");
        this.dosenPengampu.add(dosen);
        return this;
    }

    public Set<Dosen> getDosenPengampu() {
        return Collections.unmodifiableSet(dosenPengampu);
    }
}