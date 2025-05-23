package id.ac.ui.cs.advprog.hiringgo.notifikasi.Event;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.event.NotifikasiEvent;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.event.NotifikasiEventListener;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.model.Notifikasi;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.repository.NotifikasiRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class NotifikasiEventListenerTest {

    @Mock
    private NotifikasiRepository notifikasiRepository;

    @InjectMocks
    private NotifikasiEventListener listener;

    @Test
    void testHandleNotifikasiEvent_shouldSaveNotifikasiToRepository() {
        Mahasiswa mahasiswa = mock(Mahasiswa.class);
        MataKuliah mataKuliah = mock(MataKuliah.class);
        String tahunAjaran = "2024/2025";
        Semester semester = Semester.GANJIL;
        String status = "DITERIMA";

        NotifikasiEvent event = new NotifikasiEvent(mahasiswa, mataKuliah, tahunAjaran, semester, status);

        listener.handleNotifikasiEvent(event);

        ArgumentCaptor<Notifikasi> captor = ArgumentCaptor.forClass(Notifikasi.class);
        verify(notifikasiRepository, times(1)).save(captor.capture());

        Notifikasi savedNotif = captor.getValue();
        assertEquals(mahasiswa, savedNotif.getMahasiswa());
        assertEquals(mataKuliah, savedNotif.getMataKuliah());
        assertEquals(tahunAjaran, savedNotif.getTahunAjaran());
        assertEquals(semester, savedNotif.getSemester());
        assertEquals(status, savedNotif.getStatus());
    }
}
