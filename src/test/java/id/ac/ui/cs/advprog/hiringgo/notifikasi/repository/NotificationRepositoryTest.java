package id.ac.ui.cs.advprog.hiringgo.notifikasi.repository;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.model.Notifikasi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class NotificationRepositoryTest {

    @Autowired
    private NotifikasiRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private MataKuliah mataKuliah;
    @Autowired
    private MataKuliahRepository mataKuliahRepository;

    @Test
    @DisplayName("Harus bisa menyimpan dan mengambil notifikasi berdasarkan Mahasiswa")
    public void testFindByMahasiswa() {
        Mahasiswa mahasiswa = new Mahasiswa("tes@gmail.com", "password", "Budi", "1906391234");
        mataKuliah = new MataKuliah("IF101", "Pemrograman", "Dasar Pemrograman");
        mahasiswa = userRepository.save(mahasiswa);
        mataKuliah = mataKuliahRepository.save(mataKuliah);
        Notifikasi notif1 = new Notifikasi(
                mahasiswa,
                mataKuliah,
                "2024/2025",
                Semester.GANJIL,
                "DITERIMA"
        );

        Notifikasi notif2 = new Notifikasi(
                mahasiswa,
                mataKuliah,
                "2024/2025",
                Semester.GANJIL,
                "DITOLAK"
        );

        Notifikasi notif3 = new Notifikasi(
                mahasiswa,
                mataKuliah,
                "2024/2025",
                Semester.GANJIL,
                "DITERIMA"
        );

        notificationRepository.saveAll(List.of(notif1, notif2, notif3));

        List<Notifikasi> results = notificationRepository.findByMahasiswa(mahasiswa);
        assertEquals(3, results.size());
    }

    @Test
    @DisplayName("Harus bisa mengambil notifikasi yang belum dibaca")
    public void testFindByMahasiswaAndIsReadFalse() {
        Mahasiswa mahasiswa = new Mahasiswa("tes@gmail.com", "password", "Budi", "1906391234");
        mataKuliah = new MataKuliah("IF101", "Pemrograman", "Dasar Pemrograman");
        mahasiswa = userRepository.save(mahasiswa);
        mataKuliah = mataKuliahRepository.save(mataKuliah);
        Notifikasi notif1 = new Notifikasi(
                mahasiswa,
                mataKuliah,
                "2024/2025",
                Semester.GANJIL,
                "DITERIMA"
        );

        Notifikasi notif2 = new Notifikasi(
                mahasiswa,
                mataKuliah,
                "2024/2025",
                Semester.GANJIL,
                "DITOLAK"
        );

        Notifikasi notif3 = new Notifikasi(
                mahasiswa,
                mataKuliah,
                "2024/2025",
                Semester.GANJIL,
                "DITERIMA"
        );
        notif3.setRead(true);
        notificationRepository.saveAll(List.of(notif1, notif2, notif3));

        List<Notifikasi> unreadNotifs = notificationRepository.findByMahasiswaAndReadFalse(mahasiswa);
        assertEquals(2, unreadNotifs.size());

        assertTrue(unreadNotifs.stream().allMatch(n -> !n.isRead()));
    }
}
