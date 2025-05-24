package id.ac.ui.cs.advprog.hiringgo.dashboard.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.AdminDashboardServiceImpl;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.DosenDashboardServiceImpl;
import id.ac.ui.cs.advprog.hiringgo.dashboard.service.MahasiswaDashboardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final MahasiswaDashboardServiceImpl mahasiswaDashboardService;
    private final DosenDashboardServiceImpl dosenDashboardService;
    private final AdminDashboardServiceImpl adminDashboardService;

    @Autowired
    public DashboardController(
            MahasiswaDashboardServiceImpl mahasiswaDashboardService,
            DosenDashboardServiceImpl dosenDashboardService,
            AdminDashboardServiceImpl adminDashboardService) {
        this.mahasiswaDashboardService = mahasiswaDashboardService;
        this.dosenDashboardService = dosenDashboardService;
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/mahasiswa")
    @PreAuthorize("hasAuthority('MAHASISWA')")
    public ResponseEntity<DashboardResponse> getMahasiswaDashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        Mahasiswa mahasiswa = (Mahasiswa) authentication.getPrincipal();
        DashboardResponse dashboardResponse = mahasiswaDashboardService.getDashboardData(mahasiswa.getId());

        return ResponseEntity.ok(dashboardResponse);
    }

    @GetMapping("/dosen")
    @PreAuthorize("hasAuthority('DOSEN')")
    public ResponseEntity<DashboardResponse> getDosenDashboard(Authentication authentication) {
        if (authentication ==   null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        Dosen dosen = (Dosen) authentication.getPrincipal();
        DashboardResponse dashboardResponse = dosenDashboardService.getDashboardData(dosen.getId());

        return ResponseEntity.ok(dashboardResponse);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<DashboardResponse> getAdminDashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        Admin admin = (Admin) authentication.getPrincipal();
        DashboardResponse dashboardResponse = adminDashboardService.getDashboardData(admin.getId());

        return ResponseEntity.ok(dashboardResponse);
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralError(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Terjadi kesalahan server");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}