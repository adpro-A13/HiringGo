package id.ac.ui.cs.advprog.hiringgo.notifikasi.service;

import id.ac.ui.cs.advprog.hiringgo.notifikasi.dto.NotifikasiDTO;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;

import java.util.List;
import java.util.UUID;

public interface NotifikasiService {
    List<NotifikasiDTO> getNotifikasiByMahasiswa(Mahasiswa mahasiswa);
    long countUnreadByMahasiswa(Mahasiswa mahasiswa);
    void markAsRead(UUID notifikasiId);
}
