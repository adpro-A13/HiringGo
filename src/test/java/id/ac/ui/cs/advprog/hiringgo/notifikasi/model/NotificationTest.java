package id.ac.ui.cs.advprog.hiringgo.notifikasi.model;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    private Notifikasi notification;
    private Mahasiswa mahasiswa;
    private MataKuliah mataKuliah;

    @BeforeEach
    void setUp() {
        mahasiswa = new Mahasiswa();
        mahasiswa.setFullName("Andi");
        mahasiswa.setNim("123456789");

        mataKuliah = new MataKuliah("IF101", "Pemrograman", "Dasar Pemrograman");

        notification = new Notifikasi(mahasiswa, mataKuliah, "2024/2025", Semester.GENAP, "DITOLAK");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(mahasiswa, notification.getMahasiswa());
        assertEquals(mataKuliah, notification.getMataKuliah());
        assertEquals("2024/2025", notification.getTahunAjaran());
        assertEquals(Semester.GENAP, notification.getSemester());
        assertEquals("DITOLAK", notification.getStatus());
        assertFalse(notification.isRead());
    }

    @Test
    void testSetters() {
        notification.setStatus("DITERIMA");
        assertEquals("DITERIMA", notification.getStatus());

        notification.setRead(true);
        assertTrue(notification.isRead());

        UUID newId = UUID.randomUUID();
        notification.setId(newId);
        assertEquals(newId, notification.getId());
    }
}
