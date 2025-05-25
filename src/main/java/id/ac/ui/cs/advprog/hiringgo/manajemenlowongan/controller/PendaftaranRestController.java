package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDetailResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.ApplicationProcessingService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.ApplicationStatusService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.AuthenticationValidatorService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.ResponseBuilderService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.validation.DaftarFormBusinessValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/lowongandaftar")
@RequiredArgsConstructor
public class PendaftaranRestController {

    private final LowonganService lowonganService;
    private final ApplicationProcessingService applicationProcessingService;
    private final ApplicationStatusService applicationStatusService;
    private final AuthenticationValidatorService authenticationValidatorService;
    private final ResponseBuilderService responseBuilderService;
    private final LowonganMapper lowonganMapper;
    private final DaftarFormBusinessValidator daftarFormBusinessValidator;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLowonganDetail(@PathVariable UUID id) {
        try {
            Lowongan lowongan = lowonganService.findById(id);
            LowonganDetailResponse lowonganDetailResponse = new LowonganDetailResponse(lowongan);
            return responseBuilderService.buildSuccessResponse(
                    "Lowongan detail retrieved successfully",
                    "lowongan",
                    lowonganDetailResponse
            );
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Lowongan tidak ditemukan dengan ID: " + id);
        }
    }

    @PostMapping("/{id}/daftar")
    public ResponseEntity<Map<String, Object>> daftar(
            @PathVariable UUID id,
            @Valid @RequestBody DaftarForm daftarForm,
            Principal principal) {
        try {
            daftarFormBusinessValidator.validateBusinessRules(daftarForm);

            Mahasiswa currentUser = authenticationValidatorService.validateAndGetCurrentUser(principal);
            var pendaftaran = applicationProcessingService.processApplication(id, daftarForm, currentUser);
            return responseBuilderService.buildRegistrationResponse(pendaftaran);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw e;
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Lowongan tidak ditemukan dengan ID: " + id);
        } catch (Exception e) {
            throw new RuntimeException("Terjadi kesalahan saat memproses pendaftaran: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getLowonganApplyStatus(@PathVariable UUID id, Principal principal) {
        try {
            Mahasiswa currentUser = authenticationValidatorService.validateAndGetCurrentUser(principal);
            Map<String, Object> statusData = applicationStatusService.getApplicationStatus(id, currentUser);
            return responseBuilderService.buildSuccessResponse(
                    "Status pendaftaran retrieved successfully",
                    "application_status",
                    statusData
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Terjadi kesalahan saat mengambil status pendaftaran: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllLowonganForMahasiswa() {
        try {
            List<Lowongan> lowonganList = lowonganService.findAll();
            List<LowonganDTO> lowonganDTOList = lowonganMapper.toDtoList(lowonganList);
            return responseBuilderService.buildListResponse(
                    "Lowongan list retrieved successfully",
                    lowonganDTOList
            );
        } catch (Exception e) {
            throw new RuntimeException("Terjadi kesalahan saat mengambil daftar lowongan: " + e.getMessage());
        }
    }
}