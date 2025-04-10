package id.ac.ui.cs.advprog.hiringgo.lowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.lowongan.model.Pendaftar;
import id.ac.ui.cs.advprog.hiringgo.lowongan.service.LowonganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lowongan")
public class LowonganController {

    @Autowired
    private LowonganService lowonganService;

    // Endpoint: POST /lowongan/register/{id}
    @PostMapping("/register/{lowonganId}")
    public String registerPendaftar(@PathVariable Long lowonganId,
                                    @RequestBody Pendaftar pendaftar) {
        lowonganService.registerLowongan(lowonganId, pendaftar);
        return "Pendaftaran berhasil untuk lowongan dengan ID: " + lowonganId;
    }
}
