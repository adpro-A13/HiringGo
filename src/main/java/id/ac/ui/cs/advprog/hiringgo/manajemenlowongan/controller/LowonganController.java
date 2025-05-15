package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDetailResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterBySemester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterByStatus;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lowongan")
public class LowonganController {

    @Autowired
    private LowonganService lowonganService;

    @GetMapping
    public ResponseEntity<List<LowonganDetailResponse>> getAllLowongan(
            @RequestParam(required = false) Semester semester,
            @RequestParam(required = false) StatusLowongan status) {

        List<Lowongan> lowonganList = lowonganService.findAll();

        if (semester != null) {
            lowonganList = new FilterBySemester(semester).filter(lowonganList);
        }

        if (status != null) {
            lowonganList = new FilterByStatus(status).filter(lowonganList);
        }

        List<LowonganDetailResponse> responses = lowonganList.stream()
                .map(LowonganDetailResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getLowonganById(@PathVariable UUID id) {
        try {
            Lowongan lowongan = lowonganService.findById(id);
            LowonganDetailResponse response = new LowonganDetailResponse(lowongan);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lowongan dengan ID " + id + " tidak ditemukan");
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('DOSEN')")
    public ResponseEntity<?> createLowongan(@RequestBody Lowongan lowongan) {
        try {
            Lowongan created = lowonganService.createLowongan(lowongan);
            LowonganDetailResponse response = new LowonganDetailResponse(created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Gagal membuat lowongan: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOSEN')")
    public ResponseEntity<?> deleteLowongan(@PathVariable UUID id) {
        try {
            lowonganService.deleteLowonganById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lowongan dengan ID " + id + " tidak ditemukan");
        }
    }

    @GetMapping("/enums/semester")
    public ResponseEntity<Semester[]> getAllSemesters() {
        return ResponseEntity.ok(Semester.values());
    }

    @GetMapping("/enums/status")
    public ResponseEntity<StatusLowongan[]> getAllStatuses() {
        return ResponseEntity.ok(StatusLowongan.values());
    }

    @PreAuthorize("hasRole('DOSEN')")
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

    @PreAuthorize("hasRole('DOSEN')")
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

    @PreAuthorize("hasRole('DOSEN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLowongan(@PathVariable UUID id, @RequestBody Lowongan updatedLowongan) {
            if (!id.equals(updatedLowongan.getLowonganId())) {
                return ResponseEntity.badRequest().body("ID di URL dan body tidak cocok");
            }
            try {
                Lowongan updated = lowonganService.updateLowongan(id, updatedLowongan);
                return ResponseEntity.ok(new LowonganDetailResponse(updated));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Gagal memperbarui lowongan: " + e.getMessage());
            }
    }
}
