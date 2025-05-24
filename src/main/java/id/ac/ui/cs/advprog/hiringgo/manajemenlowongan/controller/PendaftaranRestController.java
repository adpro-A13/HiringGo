package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDetailResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lowongandaftar")
@PreAuthorize("hasAuthority('MAHASISWA')")
public class PendaftaranRestController {

    private final LowonganService lowonganService;
    private final PendaftaranService pendaftaranService;
    private final UserRepository userRepository;
    private final PendaftaranRepository pendaftaranRepository;
    private final LowonganMapper lowonganMapper;

    @Autowired
    public PendaftaranRestController(
            LowonganService lowonganService,
            PendaftaranService pendaftaranService,
            JwtService jwtService,
            UserRepository userRepository,
            PendaftaranRepository pendaftaranRepository,
            LowonganMapper lowonganMapper
    ) {
        this.lowonganService = lowonganService;
        this.pendaftaranService = pendaftaranService;
        this.userRepository = userRepository;
        this.pendaftaranRepository = pendaftaranRepository;
        this.lowonganMapper = lowonganMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getLowonganDetail(@PathVariable UUID id) {
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
    public ResponseEntity<Object> daftar(
            @PathVariable UUID id,
            @Valid @RequestBody DaftarForm form,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Anda harus login terlebih dahulu");
        }

        try {
            String email = principal.getName();
            Mahasiswa kandidat = (Mahasiswa) userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

            List<Pendaftaran> pendaftaranList = pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(
                    kandidat.getId(), id);

            if (!pendaftaranList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Anda sudah mendaftar untuk lowongan ini");
            }

            Pendaftaran pendaftaran = pendaftaranService.daftar(
                    id,
                    kandidat,
                    BigDecimal.valueOf(form.getIpk()),
                    form.getSks()
            );

            DaftarResponse response = new DaftarResponse(true, "Berhasil mendaftar asisten dosen", pendaftaran);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User tidak ditemukan: " + e.getMessage());
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

    @GetMapping("/{id}/status")
    public ResponseEntity<Object> getLowonganApplyStatus(@PathVariable UUID id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Anda harus login terlebih dahulu");
        }

        try {
            String email = principal.getName();
            Mahasiswa mahasiswa = (Mahasiswa) userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

            List<Pendaftaran> pendaftaranList = pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(
                    mahasiswa.getId(), id);

            if (pendaftaranList.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "hasApplied", false,
                        "status", "BELUM_DAFTAR"
                ));
            }

            Pendaftaran pendaftaran = pendaftaranList.get(0);
            return ResponseEntity.ok(Map.of(
                    "hasApplied", true,
                    "status", pendaftaran.getStatus().toString(),
                    "pendaftaranId", pendaftaran.getPendaftaranId()
            ));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User tidak ditemukan: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<LowonganDTO>> getAllLowonganForMahasiswa() {
        List<Lowongan> lowonganList = lowonganService.findAll();
        List<LowonganDTO> lowonganDTOList = lowonganMapper.toDtoList(lowonganList);
        return ResponseEntity.ok(lowonganDTOList);
    }
}