package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;
import org.springframework.security.core.Authentication;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
@RequestMapping("/lowongan/{id}")
public class PendaftaranController {

    private final LowonganService lowonganService;
    private final PendaftaranService pendaftaranService;
    private final JwtService jwtService;

    @Autowired
    public PendaftaranController(
            LowonganService lowonganService,
            PendaftaranService pendaftaranService,
            JwtService jwtService) {
        this.lowonganService = lowonganService;
        this.pendaftaranService = pendaftaranService;
        this.jwtService = jwtService;
    }

    @GetMapping("/daftar")
    public String showDaftarForm(
            @PathVariable UUID id,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Authentication check
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("error", "Anda harus login terlebih dahulu");
            return "redirect:/auth/login";
        }

        try {
            // Get lowongan data
            Lowongan lowongan = lowonganService.findById(id);

            // Get user information
            Mahasiswa kandidat = (Mahasiswa) authentication.getPrincipal();

            // Add attributes to model
            model.addAttribute("lowongan", lowongan);
            model.addAttribute("kandidat", kandidat);
            model.addAttribute("daftarForm", new DaftarForm());

            // Sample prerequisites list
            List<String> prasyaratList = Arrays.asList(
                    "IF1010 - Dummy Course 1: A",
                    "IF1020 - Dummy Course 2: B"
            );
            model.addAttribute("prasyaratList", prasyaratList);

            return "daftar";

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Lowongan tidak ditemukan");
            return "redirect:/lowongan/list";
        }
    }

    @PostMapping("/daftar")
    public String handleDaftar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("daftarForm") DaftarForm form,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Authentication check
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("error", "Anda harus login terlebih dahulu");
            return "redirect:/auth/login";
        }


        // Handle validation errors
        if (bindingResult.hasErrors()) {
            try {
                Lowongan lowongan = lowonganService.findById(id);
                Mahasiswa kandidat = (Mahasiswa) authentication.getPrincipal();

                model.addAttribute("lowongan", lowongan);
                model.addAttribute("kandidat", kandidat);
                model.addAttribute("prasyaratList", Arrays.asList(
                        "IF1010 - Dummy Course 1: A",
                        "IF1020 - Dummy Course 2: B"
                ));

                return "daftar";
            } catch (NoSuchElementException e) {
                redirectAttributes.addFlashAttribute("error", "Lowongan tidak ditemukan");
                return "redirect:/lowongan/list";
            }
        }

        // Process application
        try {
            Mahasiswa kandidat = (Mahasiswa) authentication.getPrincipal();
            Pendaftaran pendaftaran = pendaftaranService.daftar(id, kandidat, BigDecimal.valueOf(form.getIpk()), form.getSks());
            redirectAttributes.addFlashAttribute("success", "Berhasil mendaftar asisten dosen");
            return "redirect:/lowongan/" + id;
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Lowongan tidak ditemukan");
            return "redirect:/lowongan/list";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/lowongan/" + id + "/daftar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
            return "redirect:/lowongan/" + id + "/daftar";
        }
    }

    @GetMapping
    public String showLowonganDetail(
            @PathVariable UUID id,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Lowongan lowongan = lowonganService.findById(id);
            model.addAttribute("lowongan", lowongan);
            Mahasiswa kandidat = (Mahasiswa) authentication.getPrincipal();
            // Add   user info if authenticated
            if (kandidat != null) {
                model.addAttribute("kandidat", kandidat);
            }

            return "lowonganDetail";
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Lowongan tidak ditemukan");
            return "redirect:/lowongan/list";
        }
    }
}