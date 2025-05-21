package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDetailResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterBySemester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterByStatus;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("hasAuthority('DOSEN')")
@RequestMapping("/api/lowongan")
public class LowonganController {

    @Autowired
    private LowonganService lowonganService;

    private final LowonganMapper lowonganMapper;

    public LowonganController(LowonganService lowonganService, LowonganMapper lowonganMapper) {
        this.lowonganService = lowonganService;
        this.lowonganMapper = lowonganMapper;
    }

    @GetMapping
    public ResponseEntity<List<LowonganDTO>> getAllLowongan(
            @RequestParam(required = false) Semester semester,
            @RequestParam(required = false) StatusLowongan status) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Lowongan> lowonganList = lowonganService.findAllByDosenUsername(username);

        if (semester != null) {
            lowonganList = lowonganService.filterLowongan("FilterBySemester", semester.name(), lowonganList);
        }

        if (status != null) {
            lowonganList = lowonganService.filterLowongan("FilterByStatus", status.name(), lowonganList);
        }

        List<LowonganDTO> responses = lowonganMapper.toDtoList(lowonganList);
        return ResponseEntity.ok(responses);
    }



    @GetMapping("/{id}")
    public ResponseEntity<?> getLowonganById(@PathVariable UUID id) {
        try {
            Lowongan lowongan = lowonganService.findById(id);
            LowonganDTO response = lowonganMapper.toDto(lowongan);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lowongan dengan ID " + id + " tidak ditemukan");
        }
    }

    @PostMapping
    public ResponseEntity<?> createLowongan(@RequestBody LowonganDTO lowonganDTO) {
        try {
            Lowongan lowonganEntity = lowonganMapper.toEntity(lowonganDTO);
            Lowongan created = lowonganService.createLowongan(lowonganEntity);
            LowonganDTO responseDTO = lowonganMapper.toDto(created);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Gagal membuat lowongan: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLowongan(@PathVariable UUID id) {
        try {
            lowonganService.deleteLowonganById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lowongan dengan ID " + id + " tidak ditemukan");
        }
    }

    @PostMapping("/{lowonganId}/terima/{pendaftaranId}")
    public ResponseEntity<?> terimaPendaftar(@PathVariable UUID lowonganId, @PathVariable UUID pendaftaranId) {
        try {
            lowonganService.terimaPendaftar(lowonganId, pendaftaranId);
            return ResponseEntity.ok("Pendaftar berhasil diterima");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Gagal menerima pendaftar: " + e.getMessage());
        }
    }

    @DeleteMapping("/{lowonganId}/tolak/{pendaftaranId}")
    public ResponseEntity<?> tolakPendaftar(@PathVariable UUID lowonganId, @PathVariable UUID pendaftaranId) {
        try {
            lowonganService.tolakPendaftar(lowonganId, pendaftaranId);
            return ResponseEntity.ok("Pendaftar berhasil ditolak");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Gagal menolak pendaftar: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLowongan(@PathVariable UUID id, @RequestBody LowonganDTO updatedLowonganDTO) {
        if (updatedLowonganDTO.getLowonganId() == null || !id.equals(updatedLowonganDTO.getLowonganId())) {
            return ResponseEntity.badRequest().body("ID di URL dan body tidak cocok atau ID kosong");
        }
        try {
            Lowongan updatedLowongan = lowonganMapper.toEntity(updatedLowonganDTO);
            Lowongan updated = lowonganService.updateLowongan(id, updatedLowongan);
            LowonganDTO responseDto = lowonganMapper.toDto(updated);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Gagal memperbarui lowongan: " + e.getMessage());
        }
    }

}
