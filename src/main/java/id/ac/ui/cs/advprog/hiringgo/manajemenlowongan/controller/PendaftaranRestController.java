package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDetailResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/lowongan")
public class PendaftaranRestController {

    private final LowonganService lowonganService;
    private final PendaftaranService pendaftaranService;
    private final JwtService jwtService;

    @Autowired
    public PendaftaranRestController(
            LowonganService lowonganService,
            PendaftaranService pendaftaranService,
            JwtService jwtService) {
        this.lowonganService = lowonganService;
        this.pendaftaranService = pendaftaranService;
        this.jwtService = jwtService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLowonganDetail(@PathVariable UUID id) {
        try {
            Lowongan lowongan = lowonganService.findById(id);
            LowonganDetailResponse response = new LowonganDetailResponse(lowongan);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lowongan tidak ditemukan");
        }
    }

    @PostMapping("/{id}/daftar")
    public ResponseEntity<?> daftar(
            @PathVariable UUID id,
            @Valid @RequestBody DaftarForm form,
            Principal principal) {

        // Authentication check
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Anda harus login terlebih dahulu");
        }

        try {
            String kandidatId = principal.getName();
            Pendaftaran pendaftaran = pendaftaranService.daftar(
                    id,
                    kandidatId,
                    BigDecimal.valueOf(form.getIpk()),
                    form.getSks()
            );

            DaftarResponse response = new DaftarResponse(true, "Berhasil mendaftar asisten dosen", pendaftaran);
            return ResponseEntity.ok(response);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lowongan tidak ditemukan");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Terjadi kesalahan: " + e.getMessage());
        }
    }
}