package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.AdminDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class AdminDashboardServiceImpl extends AbstractDashboardService {

    private final UserRepository userRepository;
    private final MataKuliahRepository mataKuliahRepository;
    private final LowonganRepository lowonganRepository;

    @Autowired
    public AdminDashboardServiceImpl(
            UserRepository userRepository,
            MataKuliahRepository mataKuliahRepository,
            LowonganRepository lowonganRepository) {
        this.userRepository = userRepository;
        this.mataKuliahRepository = mataKuliahRepository;
        this.lowonganRepository = lowonganRepository;
    }

    @Override
    protected void validateUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User tidak ditemukan dengan ID: " + userId);
        }

        boolean isAdmin = userRepository.findById(userId)
                .filter(Admin.class::isInstance)
                .isPresent();

        if (!isAdmin) {
            throw new IllegalArgumentException("User dengan ID: " + userId + " bukan admin");
        }
    }

    @Override
    protected DashboardResponse createDashboardResponse() {
        return new AdminDashboardResponse();
    }

    @Override
    protected void populateCommonData(UUID userId, DashboardResponse response) {
        // Ambil dan cast ke Admin
        Admin admin = userRepository.findById(userId)
                .filter(Admin.class::isInstance)
                .map(Admin.class::cast)
                .orElseThrow(() -> new NoSuchElementException("Admin tidak ditemukan dengan ID: " + userId));

        response.setUserRole("ADMIN");
        response.setUsername(admin.getUsername());
        response.setFullName(admin.getUsername()); // Using username as fullName for Admin

        // Definisikan fitur yang tersedia untuk Admin
        Map<String, String> features = new HashMap<>();
        features.put("manajemenAkun", "/api/admin/accounts");
        features.put("manajemenMataKuliah", "/api/admin/matakuliah");
        features.put("manajemenLowongan", "/api/admin/lowongan");
        features.put("profile", "/api/profile");
        response.setAvailableFeatures(features);
    }

    @Override
    protected void populateRoleSpecificData(UUID userId, DashboardResponse baseResponse) {
        AdminDashboardResponse response = (AdminDashboardResponse) baseResponse;

        CompletableFuture<Integer> dosenCountFuture = CompletableFuture.supplyAsync(() ->
                (int) StreamSupport.stream(userRepository.findAll().spliterator(), false)
                        .filter(Dosen.class::isInstance)
                        .count()
        );

        CompletableFuture<Integer> mahasiswaCountFuture = CompletableFuture.supplyAsync(() ->
                (int) StreamSupport.stream(userRepository.findAll().spliterator(), false)
                        .filter(Mahasiswa.class::isInstance)
                        .count()
        );

        CompletableFuture<Integer> courseCountFuture = CompletableFuture.supplyAsync(() ->
                (int) mataKuliahRepository.count()
        );

        CompletableFuture<Integer> lowonganCountFuture = CompletableFuture.supplyAsync(() ->
                (int) lowonganRepository.count()
        );

        response.setDosenCount(dosenCountFuture.join());
        response.setMahasiswaCount(mahasiswaCountFuture.join());
        response.setCourseCount(courseCountFuture.join());
        response.setLowonganCount(lowonganCountFuture.join());
    }
}