package id.ac.ui.cs.advprog.hiringgo.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MataKuliah {
    String kode;
    String nama;
    String deskripsi;
    List<String> dosenPengampu;

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


