package id.ac.ui.cs.advprog.hiringgo.notifikasi.event;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotifikasiEvent {
    private final Mahasiswa mahasiswa;
    private final MataKuliah mataKuliah;
    private final String tahunAjaran;
    private final Semester semester;
    private final String status;
}
