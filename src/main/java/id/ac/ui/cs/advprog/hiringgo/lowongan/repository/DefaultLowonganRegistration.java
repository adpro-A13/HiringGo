package id.ac.ui.cs.advprog.hiringgo.lowongan.service;

import id.ac.ui.cs.advprog.hiringgo.lowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.lowongan.model.Pendaftar;
import org.springframework.stereotype.Component;

@Component
public class DefaultLowonganRegistration extends LowonganRegistrationTemplate {

    @Override
    protected void validate(Pendaftar pendaftar) {
        // Contoh validasi IPK dan SKS
        if (pendaftar.getIpk() < 0.0 || pendaftar.getIpk() > 4.0) {
            throw new IllegalArgumentException("IPK di luar rentang [0.0, 4.0]");
        }
        if (pendaftar.getSks() < 0) {
            throw new IllegalArgumentException("SKS tidak boleh negatif");
        }
    }

    @Override
    protected void processRegistration(Lowongan lowongan, Pendaftar pendaftar) {
        // Misal: cek apakah lowongan masih buka
        if (lowongan.getJumlahPendaftar() >= lowongan.getKuota()) {
            throw new IllegalStateException("Kuota lowongan sudah penuh!");
        }
        // Tambahkan jumlah pendaftar
        lowongan.setJumlahPendaftar(lowongan.getJumlahPendaftar() + 1);
    }

    @Override
    protected void updateQuota(Lowongan lowongan) {
        // Contoh: bisa dilakukan penyesuaian, di sini tidak ada logika tambahan
    }

    @Override
    protected void notifyUser(Pendaftar pendaftar) {
        // Contoh notifikasi
        System.out.println("Notifikasi: Halo " + pendaftar.getNama() + 
            ", pendaftaran Anda berhasil!");
    }
}
