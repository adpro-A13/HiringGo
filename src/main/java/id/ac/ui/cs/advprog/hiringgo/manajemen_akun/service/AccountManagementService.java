package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.Enums.UserRoleEnums;
import id.ac.ui.cs.advprog.hiringgo.authentication.factory.UserFactory;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.AdminDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.ChangeRoleDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.DosenDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.MahasiswaDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.UserResponseDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDto> getAllUsers() {
        List<UserResponseDto> usersList = new ArrayList<>();
        Iterable<User> users = userRepository.findAll();

        for (User user : users) {
            UserResponseDto dto = mapUserToDto(user);
            usersList.add(dto);
        }

        return usersList;
    }

    public UserResponseDto getUserById(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }

        Optional<User> userOptional = userRepository.findById(uuid);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        return mapUserToDto(userOptional.get());
    }

    public UserResponseDto createDosenAccount(DosenDto dosenDto) {
        // Validate input
        if (dosenDto.getEmail() == null || dosenDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (dosenDto.getPassword() == null || dosenDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (dosenDto.getFullName() == null || dosenDto.getFullName().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (dosenDto.getNip() == null || dosenDto.getNip().isEmpty()) {
            throw new IllegalArgumentException("NIP is required");
        }

        if (userRepository.findByEmail(dosenDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User dosen = UserFactory.createUser(
                UserRoleEnums.DOSEN,
                dosenDto.getEmail(),
                passwordEncoder.encode(dosenDto.getPassword()),
                dosenDto.getFullName(),
                dosenDto.getNip()
        );

        User savedDosen = userRepository.save(dosen);
        return mapUserToDto(savedDosen);
    }

    public UserResponseDto createAdminAccount(AdminDto adminDto) {
        if (adminDto.getEmail() == null || adminDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (adminDto.getPassword() == null || adminDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (userRepository.findByEmail(adminDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        Admin admin = new Admin(adminDto.getEmail(), passwordEncoder.encode(adminDto.getPassword()));
        User savedAdmin = userRepository.save(admin);
        return mapUserToDto(savedAdmin);
    }

    public UserResponseDto createMahasiswaAccount(MahasiswaDto mahasiswaDto) {
        if (mahasiswaDto.getEmail() == null || mahasiswaDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (mahasiswaDto.getPassword() == null || mahasiswaDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (mahasiswaDto.getFullName() == null || mahasiswaDto.getFullName().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (mahasiswaDto.getNim() == null || mahasiswaDto.getNim().isEmpty()) {
            throw new IllegalArgumentException("NIM is required");
        }
        if (userRepository.findByEmail(mahasiswaDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User mahasiswa = UserFactory.createUser(
            UserRoleEnums.MAHASISWA,
            mahasiswaDto.getEmail(),
            passwordEncoder.encode(mahasiswaDto.getPassword()),
            mahasiswaDto.getFullName(),
            mahasiswaDto.getNim()
        );
        User savedMahasiswa = userRepository.save(mahasiswa);
        return mapUserToDto(savedMahasiswa);
    }

    public UserResponseDto changeUserRole(String id, ChangeRoleDto changeRoleDto) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }

        Optional<User> userOptional = userRepository.findById(uuid);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User currentUser = userOptional.get();
        UserRoleEnums newRole;
        
        try {
            newRole = UserRoleEnums.valueOf(changeRoleDto.getNewRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Valid roles are: ADMIN, DOSEN, MAHASISWA");
        }
        
        if (currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(newRole.getValue()))) {
            return mapUserToDto(currentUser);
        }

        User newUser;
        String email = currentUser.getUsername();
        String password = currentUser.getPassword();

        if (newRole == UserRoleEnums.ADMIN) {
            newUser = new Admin(email, password);
        } else {
            String fullName = "";
            String identifier = "";
            
            if (currentUser instanceof Mahasiswa) {
                fullName = ((Mahasiswa) currentUser).getFullName();
                identifier = ((Mahasiswa) currentUser).getNim();
            } else if (currentUser instanceof Dosen) {
                fullName = ((Dosen) currentUser).getFullName();
                identifier = ((Dosen) currentUser).getNip();
            }
            
            newUser = UserFactory.createUser(newRole, email, password, fullName, identifier);
        }

        userRepository.delete(currentUser);
        User savedUser = userRepository.save(newUser);
        
        return mapUserToDto(savedUser);
    }

    public void deleteUser(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }

        Optional<User> userOptional = userRepository.findById(uuid);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        userRepository.delete(userOptional.get());
    }

    private UserResponseDto mapUserToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId().toString());
        dto.setEmail(user.getUsername());
        
        user.getAuthorities().stream()
                .findFirst()
                .ifPresent(auth -> dto.setRole(auth.getAuthority()));

        if (user instanceof Mahasiswa) {
            Mahasiswa mahasiswa = (Mahasiswa) user;
            dto.setFullName(mahasiswa.getFullName());
            dto.setNim(mahasiswa.getNim());
        } else if (user instanceof Dosen) {
            Dosen dosen = (Dosen) user;
            dto.setFullName(dosen.getFullName());
            dto.setNip(dosen.getNip());
        }
        
        return dto;
    }
}