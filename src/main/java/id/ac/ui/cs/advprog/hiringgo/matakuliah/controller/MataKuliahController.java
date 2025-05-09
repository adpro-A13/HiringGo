package id.ac.ui.cs.advprog.hiringgo.matakuliah.controller;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.service.MataKuliahService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<MataKuliahDTO>> getAllMataKuliah() {
        List<MataKuliah> mataKuliahList = mataKuliahService.findAll();
        return ResponseEntity.ok(mataKuliahMapper.toDtoList(mataKuliahList));
    }

    @GetMapping("/{kode}")
    public ResponseEntity<MataKuliahDTO> getMataKuliahByKode(@PathVariable String kode) {
        MataKuliah mataKuliah = mataKuliahService.findByKode(kode);
        if (mataKuliah == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mataKuliahMapper.toDto(mataKuliah));
    }

    @PostMapping("/create")
    public ResponseEntity<MataKuliahDTO> createMataKuliah(@RequestBody MataKuliahDTO mataKuliahDTO) {
        try {
            MataKuliah mataKuliah = mataKuliahMapper.toEntity(mataKuliahDTO);
            MataKuliah createdMataKuliah = mataKuliahService.create(mataKuliah);
            return ResponseEntity.status(HttpStatus.CREATED).body(mataKuliahMapper.toDto(createdMataKuliah));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("update/{kode}")
    public ResponseEntity<MataKuliahDTO> updateMataKuliah(@PathVariable String kode, @RequestBody MataKuliahDTO mataKuliahDTO) {
        if (!kode.equals(mataKuliahDTO.getKode())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            MataKuliah mataKuliah = mataKuliahMapper.toEntity(mataKuliahDTO);
            MataKuliah updatedMataKuliah = mataKuliahService.update(mataKuliah);
            return ResponseEntity.ok(mataKuliahMapper.toDto(updatedMataKuliah));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{kode}")
    public ResponseEntity<Void> deleteMataKuliah(@PathVariable String kode) {
        mataKuliahService.deleteByKode(kode);
        return ResponseEntity.noContent().build();
    }
}