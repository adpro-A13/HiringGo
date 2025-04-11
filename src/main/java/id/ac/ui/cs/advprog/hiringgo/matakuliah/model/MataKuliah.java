package id.ac.ui.cs.advprog.hiringgo.matakuliah.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "mata_kuliah")
public class MataKuliah {
    @Id
    String kode;

    String nama;
    String deskripsi;

    @ElementCollection
    @CollectionTable(
            name = "mata_kuliah_dosen_pengampu",
            joinColumns = @JoinColumn(name = "matkul_kode")
    )
    @Column(name = "dosen_pengampu")
    List<String> dosenPengampu;

    // Constructor tanpa argumen
    protected MataKuliah() {
    }

    private MataKuliah(Builder builder) {
        this.kode = builder.kode;
        this.nama = builder.nama;
        this.deskripsi = builder.deskripsi;
        this.dosenPengampu = new ArrayList<>(builder.dosenPengampu);
    }

    public static class Builder {
        String kode;
        String nama;
        String deskripsi;
        List<String> dosenPengampu = new ArrayList<>();

        public Builder withKode(String kode) {
            this.kode = kode;
            return this;
        }

        public Builder withNama(String nama) {
            this.nama = nama;
            return this;
        }

        public Builder withDeskripsi(String deskripsi) {
            this.deskripsi = deskripsi;
            return this;
        }

        public Builder addDosenPengampu(String dosenPengampu) {
            this.dosenPengampu.add(dosenPengampu);
            return this;
        }

        public MataKuliah build() {
            if (kode == null || kode.isBlank()) {
                throw new IllegalArgumentException("Kode Mata Kuliah harus diisi");
            }
            if (nama == null || nama.isBlank()) {
                throw new IllegalArgumentException("Harap sertakan nama mata kuliah");
            }
            if (dosenPengampu.isEmpty()) {
                throw new IllegalArgumentException("Harus ada minimal 1 dosen pengampu!");
            }

            return new MataKuliah(this);
        }
    }
}


