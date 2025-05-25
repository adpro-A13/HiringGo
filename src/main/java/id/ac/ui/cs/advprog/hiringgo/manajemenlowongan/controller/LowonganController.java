package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganFilterService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganSortService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasAuthority('DOSEN')")
@RequestMapping("/api/lowongan")
public class LowonganController {

    private LowonganService lowonganService;
    private final LowonganMapper lowonganMapper;
    private PendaftaranService pendaftaranService;
    private LowonganSortService lowonganSortService;
    private LowonganFilterService lowonganFilterService;
    public LowonganController(LowonganService lowonganService, LowonganMapper lowonganMapper,
                              PendaftaranService pendaftaranService, LowonganSortService lowonganSortService,
                              LowonganFilterService lowonganFilterService) {
        this.lowonganService = lowonganService;
        this.lowonganMapper = lowonganMapper;
        this.pendaftaranService = pendaftaranService;
        this.lowonganSortService = lowonganSortService;
        this.lowonganFilterService = lowonganFilterService;
    }



    @GetMapping
    public ResponseEntity<List<LowonganDTO>> getAllLowongan(
            @RequestParam(required = false) String filterStrategy,
            @RequestParam(required = false) String filterValue,
            @RequestParam(required = false) String sortStrategy
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Lowongan> lowonganList = lowonganService.findAllByDosenUsername(username);

        if (filterStrategy != null && filterValue != null) {
            lowonganList = lowonganFilterService.filter(lowonganList, filterStrategy, filterValue);
        }

        if (sortStrategy != null && !sortStrategy.isEmpty()) {
            lowonganList = lowonganSortService.sort(lowonganList, sortStrategy);
        }

        List<LowonganDTO> responses = lowonganMapper.toDtoList(lowonganList);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getLowonganById(@PathVariable UUID id) {
        try {
            Lowongan lowongan = lowonganService.findById(id);
            LowonganDTO response = lowonganMapper.toDto(lowongan);

            List<UUID> idDaftarPendaftaran = pendaftaranService.getByLowongan(lowongan.getLowonganId())
                    .stream()
                    .map(Pendaftaran::getPendaftaranId)
                    .toList();

            response.setIdDaftarPendaftaran(idDaftarPendaftaran);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lowongan dengan ID " + id + " tidak ditemukan");
        }
    }

    @PostMapping
    public ResponseEntity<Object> createLowongan(@RequestBody LowonganDTO lowonganDTO) {
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
    public ResponseEntity<Object> deleteLowongan(@PathVariable UUID id) {
        try {
            lowonganService.deleteLowonganById(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    @PostMapping("/{lowonganId}/terima/{pendaftaranId}")
    public ResponseEntity<Object> terimaPendaftar(@PathVariable UUID lowonganId, @PathVariable UUID pendaftaranId) {
        try {
            lowonganService.terimaPendaftar(lowonganId, pendaftaranId);
            return ResponseEntity.ok("Pendaftar berhasil diterima");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Gagal menerima pendaftar: " + e.getMessage());
        }
    }

    @PostMapping("/{lowonganId}/tolak/{pendaftaranId}")
    public ResponseEntity<Object> tolakPendaftar(@PathVariable UUID lowonganId, @PathVariable UUID pendaftaranId) {
        try {
            lowonganService.tolakPendaftar(lowonganId, pendaftaranId);
            return ResponseEntity.ok("Pendaftar berhasil ditolak");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Gagal menolak pendaftar: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateLowongan(@PathVariable UUID id, @RequestBody LowonganDTO updatedLowonganDTO) {
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
