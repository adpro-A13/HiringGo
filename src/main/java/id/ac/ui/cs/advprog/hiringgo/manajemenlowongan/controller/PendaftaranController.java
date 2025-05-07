package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

// Pastikan ini sesuai versi Boot:
//  - Boot 3 → jakarta.validation.Valid
//  - Boot 2 → javax.validation.Valid
import jakarta.validation.Valid;

@Controller
@RequestMapping("/lowongan/{id}")
public class PendaftaranController {

    private final LowonganService lowonganService;
    private final PendaftaranService pendaftaranService;

    public PendaftaranController(LowonganService lowonganService,
                                 PendaftaranService pendaftaranService) {
        this.lowonganService = lowonganService;
        this.pendaftaranService = pendaftaranService;
    }

    @GetMapping("/daftar")
    public String showDaftarForm(@PathVariable UUID id, Model model, Principal principal) {
        Lowongan lowongan = lowonganService.findById(id);
        String kandidatId = principal != null ? principal.getName() : "anonymous";

        model.addAttribute("lowongan", lowongan);
        model.addAttribute("kandidatId", kandidatId);
        model.addAttribute("daftarForm", new DaftarForm());
//        nanti remove ini
        List<String> dummyPrasyarat = Arrays.asList(
                "IF1010 - Dummy Course 1: A",
                "IF1020 - Dummy Course 2: B"
        );
        model.addAttribute("prasyaratList", dummyPrasyarat);

        return "daftar";
    }

    @PostMapping("/daftar")
    public String handleDaftar(@PathVariable UUID id,
                               @Valid @ModelAttribute("daftarForm") DaftarForm form,
                               BindingResult bindingResult,
                               Model model,
                               Principal principal) {
        if (bindingResult.hasErrors()) {
            Lowongan lowongan = lowonganService.findById(id);
            String kandidatId = principal != null ? principal.getName() : "anonymous";

            model.addAttribute("lowongan", lowongan);
            model.addAttribute("kandidatId", kandidatId);
            model.addAttribute("prasyaratList", Arrays.asList(
                    "IF1010 - Dummy Course 1: A",
                    "IF1020 - Dummy Course 2: B"
            ));
            return "daftar";
        }

        String kandidatId = principal != null ? principal.getName() : "anonymous";
        pendaftaranService.daftar(id, kandidatId, form.getIpk(), form.getSks());
        return "redirect:/lowongan/" + id;
    }
}
