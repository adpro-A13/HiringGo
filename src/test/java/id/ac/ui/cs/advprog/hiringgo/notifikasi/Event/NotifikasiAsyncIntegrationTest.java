package id.ac.ui.cs.advprog.hiringgo.notifikasi.Event;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.event.NotifikasiEvent;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.model.Notifikasi;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.repository.NotifikasiRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NotifikasiAsyncIntegrationTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private NotifikasiRepository notifikasiRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MataKuliahRepository mataKuliahRepository;

    @Test
    void testAsyncNotifikasiListenerShouldRunInBackground() throws Exception {
        Mahasiswa mahasiswa = new Mahasiswa("mahasiswa@example.com", "mahasiswa123",
                "am cooked", "123456789");
        userRepository.save(mahasiswa);
        MataKuliah mataKuliah = new MataKuliah("CS1234", "Advprog", "aku lelah");
        mataKuliahRepository.save(mataKuliah);
        String tahunAjaran = "2024/2025";
        Semester semester = Semester.GANJIL;
        String status = "DITERIMA";

        long start = System.currentTimeMillis();
        publisher.publishEvent(new NotifikasiEvent(mahasiswa, mataKuliah, tahunAjaran, semester, status));
        long duration = System.currentTimeMillis() - start;

        assertTrue(duration < 500, "Event handling should be async and not block main thread");

        Thread.sleep(2500);

        List<Notifikasi> hasil = notifikasiRepository.findAll();
        assertTrue(!hasil.isEmpty(), "Notifikasi harus sudah disimpan secara async");
    }
}
