package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/lowongan")
public class LowonganController {

    @Autowired
    private LowonganService lowonganService;

    @GetMapping("/create")
    public String createLowonganPage(Model model) {
        Lowongan lowongan = new Lowongan();
        model.addAttribute("lowongan", lowongan);
        model.addAttribute("semesterList", Semester.values());
        model.addAttribute("statusList", StatusLowongan.values());
        return "manajemenlowongan/createLowongan";
    }

    @PostMapping("/create")
    public String createLowonganPost(@ModelAttribute Lowongan lowongan) {
        lowonganService.createLowongan(lowongan);
        return "redirect:/lowongan/list";
    }

    @GetMapping("/list")
    public String listLowonganPage(Model model) {
        model.addAttribute("lowonganList", lowonganService.findAll());
        return "manajemenlowongan/listLowongan";
    }

    @PostMapping("/delete")
    public String deleteLowongan(@RequestParam("id") UUID idLowongan) {
        lowonganService.deleteLowonganById(idLowongan);
        return "redirect:/lowongan/list";
    }
}
