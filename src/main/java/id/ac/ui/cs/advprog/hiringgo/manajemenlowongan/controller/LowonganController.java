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
    public ResponseEntity<LowonganDTO> getLowonganById(@PathVariable UUID id) {
        Lowongan lowongan = lowonganService.findById(id);
        LowonganDTO response = lowonganMapper.toDto(lowongan);
        List<UUID> idDaftarPendaftaran = pendaftaranService.getByLowongan(lowongan.getLowonganId())
                .stream()
                .map(Pendaftaran::getPendaftaranId)
                .toList();

        response.setIdDaftarPendaftaran(idDaftarPendaftaran);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<LowonganDTO> createLowongan(@RequestBody LowonganDTO dto) {
        Lowongan created = lowonganService.createLowongan(lowonganMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lowonganMapper.toDto(created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLowongan(@PathVariable UUID id) {
        lowonganService.deleteLowonganById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{lowonganId}/terima/{pendaftaranId}")
    public ResponseEntity<Void> terimaPendaftar(
            @PathVariable UUID lowonganId,
            @PathVariable UUID pendaftaranId
    ) {
        lowonganService.terimaPendaftar(lowonganId, pendaftaranId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{lowonganId}/tolak/{pendaftaranId}")
    public ResponseEntity<Object> tolakPendaftar(@PathVariable UUID lowonganId, @PathVariable UUID pendaftaranId) {
        lowonganService.tolakPendaftar(lowonganId, pendaftaranId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LowonganDTO> updateLowongan(
            @PathVariable UUID id,
            @RequestBody LowonganDTO dto
    ) {
        if (!id.equals(dto.getLowonganId())) {
            throw new IllegalArgumentException("ID di URL dan body tidak cocok atau ID kosong");
        }
        Lowongan updated = lowonganService.updateLowongan(id, lowonganMapper.toEntity(dto));
        return ResponseEntity.ok(lowonganMapper.toDto(updated));
    }
}
