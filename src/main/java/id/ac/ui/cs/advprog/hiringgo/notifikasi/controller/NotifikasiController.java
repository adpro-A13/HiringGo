package id.ac.ui.cs.advprog.hiringgo.notifikasi.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.dto.NotifikasiDTO;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.service.NotifikasiService;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifikasi")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MAHASISWA')")
public class NotifikasiController {

    private final NotifikasiService notifikasiService;
    private final UserRepository mahasiswaRepository;

    private Mahasiswa getCurrentMahasiswa() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return (Mahasiswa) mahasiswaRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Mahasiswa tidak ditemukan dengan email: " + username));
    }

    @GetMapping
    public List<NotifikasiDTO> getAllNotifikasi() {
        Mahasiswa mahasiswa = getCurrentMahasiswa();
        return notifikasiService.getNotifikasiByMahasiswa(mahasiswa);
    }

    @GetMapping("/unread-count")
    public long getUnreadCount() {
        Mahasiswa mahasiswa = getCurrentMahasiswa();
        return notifikasiService.countUnreadByMahasiswa(mahasiswa);
    }

    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable UUID id) {
        notifikasiService.markAsRead(id);
    }
}
