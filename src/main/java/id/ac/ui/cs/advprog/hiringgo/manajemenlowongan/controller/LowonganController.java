package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lowongan")
public class LowonganController {

    @Autowired
    private LowonganService lowonganService;

    @GetMapping
    public List<Lowongan> getAllLowongan() {
        return lowonganService.findAll();
    }

    @PostMapping
    public Lowongan createLowongan(@RequestBody Lowongan lowongan) {
        return lowonganService.createLowongan(lowongan);
    }

    @DeleteMapping("/{id}")
    public void deleteLowongan(@PathVariable UUID id) {
        lowonganService.deleteLowonganById(id);
    }

    @GetMapping("/semester")
    public Semester[] getSemesterList() {
        return Semester.values();
    }

    @GetMapping("/status")
    public StatusLowongan[] getStatusList() {
        return StatusLowongan.values();
    }
}
