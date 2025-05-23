package id.ac.ui.cs.advprog.hiringgo.matakuliah.controller;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.service.MataKuliahService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/matakuliah")
public class MataKuliahController {

    private final MataKuliahService mataKuliahService;
    private final MataKuliahMapper mataKuliahMapper;

    @Autowired
    public MataKuliahController(MataKuliahService mataKuliahService, MataKuliahMapper mataKuliahMapper) {
        this.mataKuliahService = mataKuliahService;
        this.mataKuliahMapper = mataKuliahMapper;
    }

    @GetMapping("/getAll")
    @PreAuthorize("isAuthenticated()")
    public CompletableFuture<ResponseEntity<List<MataKuliahDTO>>> getAllMataKuliah() {
        return mataKuliahService.findAll()
                .thenApply(mks -> ResponseEntity.ok(mataKuliahMapper.toDtoList(mks)));
    }

    @GetMapping("/{kode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MataKuliahDTO> getMataKuliahByKode(@PathVariable String kode) {
        MataKuliah mataKuliah = mataKuliahService.findByKode(kode);
        if (mataKuliah == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mataKuliahMapper.toDto(mataKuliah));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CompletableFuture<ResponseEntity<MataKuliahDTO>> createMataKuliah(@RequestBody MataKuliahDTO mataKuliahDTO) {
        try {
            MataKuliah mataKuliah = mataKuliahMapper.toEntity(mataKuliahDTO);
            return mataKuliahService.create(mataKuliah)
                    .thenApply(created -> ResponseEntity.status(HttpStatus.CREATED).body(mataKuliahMapper.toDto(created)));
        } catch (IllegalArgumentException e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
        }
    }

    @PutMapping("update/{kode}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CompletableFuture<ResponseEntity<MataKuliahDTO>> updateMataKuliah(@PathVariable String kode, @RequestBody MataKuliahDTO mataKuliahDTO) {
        if (!kode.equals(mataKuliahDTO.getKode())) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
        }

        try {
            MataKuliah mataKuliah = mataKuliahMapper.toEntity(mataKuliahDTO);
            return mataKuliahService.update(mataKuliah)
                    .thenApply(updated -> ResponseEntity.ok(mataKuliahMapper.toDto(updated)));
        } catch (IllegalArgumentException e) {
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }
    }

    @DeleteMapping("/{kode}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CompletableFuture<ResponseEntity<Void>> deleteMataKuliah(@PathVariable String kode) {
        return mataKuliahService.deleteByKode(kode)
                .thenApply(v -> ResponseEntity.noContent().build());
    }
}