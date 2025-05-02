package id.ac.ui.cs.advprog.hiringgo.matakuliah.controller;

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

    @Autowired
    public MataKuliahController(MataKuliahService mataKuliahService) {
        this.mataKuliahService = mataKuliahService;
    }

    @GetMapping
    public ResponseEntity<List<MataKuliah>> getAllMataKuliah() {
        List<MataKuliah> mataKuliahList = mataKuliahService.findAll();
        return ResponseEntity.ok(mataKuliahList);
    }

    @GetMapping("/{kode}")
    public ResponseEntity<MataKuliah> getMataKuliahByKode(@PathVariable String kode) {
        MataKuliah mataKuliah = mataKuliahService.findByKode(kode);
        if (mataKuliah == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mataKuliah);
    }

    @PostMapping
    public ResponseEntity<MataKuliah> createMataKuliah(@RequestBody MataKuliah mataKuliah) {
        try {
            MataKuliah createdMataKuliah = mataKuliahService.create(mataKuliah);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMataKuliah);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{kode}")
    public ResponseEntity<MataKuliah> updateMataKuliah(@PathVariable String kode, @RequestBody MataKuliah mataKuliah) {
        if (!kode.equals(mataKuliah.getKode())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            MataKuliah updatedMataKuliah = mataKuliahService.update(mataKuliah);
            return ResponseEntity.ok(updatedMataKuliah);
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