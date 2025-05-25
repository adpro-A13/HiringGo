package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.controller;

import id.ac.ui.cs.advprog.hiringgo.common.dto.GlobalResponseDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.AdminDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.EditUserDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.DosenDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.MahasiswaDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.UserResponseDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.service.AccountManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@PreAuthorize("hasAuthority('ADMIN')")
public class AccountManagementController {

    private final AccountManagementService accountManagementService;

    public AccountManagementController(AccountManagementService accountManagementService) {
        this.accountManagementService = accountManagementService;
    }

    @GetMapping
    public ResponseEntity<GlobalResponseDto<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = accountManagementService.getAllUsers();
        return ResponseEntity.ok(GlobalResponseDto.success(users, "Users retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponseDto<UserResponseDto>> getUserById(@PathVariable String id) {
        try {
            UserResponseDto user = accountManagementService.getUserById(id);
            return ResponseEntity.ok(GlobalResponseDto.success(user, "User retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GlobalResponseDto.notFound(e.getMessage()));
        }
    }

    @PostMapping("/dosen")
    public ResponseEntity<GlobalResponseDto<UserResponseDto>> createDosenAccount(@RequestBody DosenDto dosenDto) {
        try {
            UserResponseDto createdUser = accountManagementService.createDosenAccount(dosenDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GlobalResponseDto.success(createdUser, "Dosen account created successfully", HttpStatus.CREATED));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(GlobalResponseDto.badRequest(e.getMessage()));
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<GlobalResponseDto<UserResponseDto>> createAdminAccount(@RequestBody AdminDto adminDto) {
        try {
            UserResponseDto createdUser = accountManagementService.createAdminAccount(adminDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GlobalResponseDto.success(createdUser, "Admin account created successfully", HttpStatus.CREATED));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(GlobalResponseDto.badRequest(e.getMessage()));
        }
    }

    @PostMapping("/mahasiswa")
    public ResponseEntity<GlobalResponseDto<UserResponseDto>> createMahasiswaAccount(@RequestBody MahasiswaDto mahasiswaDto) {
        try {
            UserResponseDto createdUser = accountManagementService.createMahasiswaAccount(mahasiswaDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GlobalResponseDto.success(createdUser, "Mahasiswa account created successfully", HttpStatus.CREATED));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(GlobalResponseDto.badRequest(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GlobalResponseDto<UserResponseDto>> editUser(@PathVariable String id, @RequestBody EditUserDto editUserDto) {
        try {
            UserResponseDto updatedUser = accountManagementService.editUser(id, editUserDto);
            return ResponseEntity.ok(GlobalResponseDto.success(updatedUser, "User updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(GlobalResponseDto.badRequest(e.getMessage()));
        }
    }    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponseDto<Void>> deleteUser(@PathVariable String id) {
        try {
            accountManagementService.deleteUser(id);
            GlobalResponseDto<Void> response = GlobalResponseDto.<Void>builder()
                    .success(true)
                    .statusCode(HttpStatus.NO_CONTENT.value())
                    .message("User deleted successfully")
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(GlobalResponseDto.badRequest(e.getMessage()));
        }
    }
}