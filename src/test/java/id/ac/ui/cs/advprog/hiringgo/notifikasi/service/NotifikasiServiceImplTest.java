package id.ac.ui.cs.advprog.hiringgo.notifikasi.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.dto.NotifikasiDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import java.util.List;
import java.util.Arrays;
import java.time.LocalDateTime;

import id.ac.ui.cs.advprog.hiringgo.notifikasi.model.Notifikasi;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.repository.NotifikasiRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotifikasiServiceImplTest {

    @Mock
    private NotifikasiRepository notifikasiRepository;

    @InjectMocks
    private NotifikasiServiceImpl notifikasiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockSecurityContext(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetNotifikasiByMahasiswa_ShouldReturnDtoList() {
        // Arrange
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setUsername("student@example.com");

        MataKuliah matkul = new MataKuliah("CS101", "Pemrograman", "Dasar");
        Notifikasi notif = new Notifikasi(mahasiswa, matkul, "2024/2025", Semester.GANJIL, "DITERIMA");
        notif.setId(UUID.randomUUID());
        notif.setCreatedAt(LocalDateTime.now());

        when(notifikasiRepository.findByMahasiswa(mahasiswa)).thenReturn(List.of(notif));

        // Act
        List<NotifikasiDTO> result = notifikasiService.getNotifikasiByMahasiswa(mahasiswa);

        // Assert
        assertEquals(1, result.size());
        NotifikasiDTO dto = result.get(0);
        assertEquals("Pemrograman", dto.getMataKuliahNama());
        assertEquals("2024/2025", dto.getTahunAjaran());
        assertEquals("GANJIL", dto.getSemester());
        assertEquals("DITERIMA", dto.getStatus());
        assertFalse(dto.isRead());
        assertNotNull(dto.getCreatedAt());
    }

    @Test
    void testCountUnreadByMahasiswa_ShouldReturnCorrectCount() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setUsername("student@example.com");

        when(notifikasiRepository.findByMahasiswaAndReadFalse(mahasiswa)).thenReturn(Arrays.asList(new Notifikasi(), new Notifikasi()));

        long unreadCount = notifikasiService.countUnreadByMahasiswa(mahasiswa);

        assertEquals(2, unreadCount);
    }


    @Test
    void testMarkAsRead_ShouldSucceed_WhenMahasiswaIsOwner() {
        UUID id = UUID.randomUUID();
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setUsername("student@example.com");

        Notifikasi notifikasi = new Notifikasi();
        notifikasi.setId(id);
        notifikasi.setMahasiswa(mahasiswa);
        notifikasi.setRead(false);

        when(notifikasiRepository.findById(id)).thenReturn(Optional.of(notifikasi));
        mockSecurityContext("student@example.com");

        notifikasiService.markAsRead(id);

        assertTrue(notifikasi.isRead());
        verify(notifikasiRepository, times(1)).save(notifikasi);
    }

    @Test
    void testMarkAsRead_ShouldThrowAccessDenied_WhenNotOwner() {
        UUID id = UUID.randomUUID();
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setUsername("student@example.com");

        Notifikasi notifikasi = new Notifikasi();
        notifikasi.setId(id);
        notifikasi.setMahasiswa(mahasiswa);
        notifikasi.setRead(false);

        when(notifikasiRepository.findById(id)).thenReturn(Optional.of(notifikasi));
        mockSecurityContext("intruder@example.com");

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> notifikasiService.markAsRead(id));
        verify(notifikasiRepository, never()).save(any());
    }

    @Test
    void testMarkAsRead_ShouldThrowEntityNotFound_WhenNotifikasiNotExist() {
        UUID id = UUID.randomUUID();
        when(notifikasiRepository.findById(id)).thenReturn(Optional.empty());
        mockSecurityContext("student@example.com");

        assertThrows(EntityNotFoundException.class, () -> notifikasiService.markAsRead(id));
    }
}
