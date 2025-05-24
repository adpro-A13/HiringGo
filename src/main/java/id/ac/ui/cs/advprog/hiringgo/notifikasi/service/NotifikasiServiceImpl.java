package id.ac.ui.cs.advprog.hiringgo.notifikasi.service;

import id.ac.ui.cs.advprog.hiringgo.notifikasi.dto.NotifikasiDTO;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.model.Notifikasi;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.repository.NotifikasiRepository;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotifikasiServiceImpl implements NotifikasiService {

    private final NotifikasiRepository notifikasiRepository;

    @Override
    public List<NotifikasiDTO> getNotifikasiByMahasiswa(Mahasiswa mahasiswa) {
        return notifikasiRepository.findByMahasiswa(mahasiswa)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public long countUnreadByMahasiswa(Mahasiswa mahasiswa) {
        return notifikasiRepository.findByMahasiswaAndReadFalse(mahasiswa).size();
    }

    @Override
    public void markAsRead(UUID notifikasiId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Notifikasi notifikasi = notifikasiRepository.findById(notifikasiId)
                .orElseThrow(() -> new EntityNotFoundException("Notifikasi tidak ditemukan"));

        if (!notifikasi.getMahasiswa().getUsername().equals(username)) {
            throw new AccessDeniedException("Anda tidak berhak menandai notifikasi ini sebagai dibaca.");
        }

        notifikasi.setRead(true);
        notifikasiRepository.save(notifikasi);
    }


    private NotifikasiDTO convertToDto(Notifikasi notifikasi) {
        return NotifikasiDTO.builder()
                .id(notifikasi.getId())
                .mataKuliahNama(notifikasi.getMataKuliah().getNama())
                .tahunAjaran(notifikasi.getTahunAjaran())
                .semester(notifikasi.getSemester().getValue())
                .status(notifikasi.getStatus())
                .read(notifikasi.isRead())
                .createdAt(notifikasi.getCreatedAt())
                .build();
    }
}
