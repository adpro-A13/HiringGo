package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.Enums.UserRoleEnums;
import id.ac.ui.cs.advprog.hiringgo.authentication.factory.UserFactory;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.common.service.AsyncLogoutService;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.AdminDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.EditUserDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.DosenDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.MahasiswaDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.UserResponseDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AsyncLogoutService asyncLogoutService;

    public AccountManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder, AsyncLogoutService asyncLogoutService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.asyncLogoutService = asyncLogoutService;
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

        User admin = UserFactory.createUser(
            UserRoleEnums.ADMIN,
            adminDto.getEmail(),
            passwordEncoder.encode(adminDto.getPassword()),
            null,
            null
        );
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
    }    @Transactional(rollbackFor = Exception.class)
    public UserResponseDto editUser(String id, EditUserDto editUserDto) {
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
        String oldEmail = currentUser.getUsername();
        String oldPassword = currentUser.getPassword();
        String password = currentUser.getPassword();
        if (editUserDto.getPassword() != null && !editUserDto.getPassword().isEmpty()) {
            password = passwordEncoder.encode(editUserDto.getPassword());
        }
        
        UserRoleEnums newRole;
          
        if (editUserDto.getNewRole() == null || editUserDto.getNewRole().isEmpty()) {
            String currentRoleStr = currentUser.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalArgumentException("Current user has no role"));
            
            newRole = UserRoleEnums.valueOf(currentRoleStr);
        } else {
            try {
                newRole = UserRoleEnums.valueOf(editUserDto.getNewRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role. Valid roles are: ADMIN, DOSEN, MAHASISWA");
            }
        }
        
        String email = editUserDto.getEmail() != null && !editUserDto.getEmail().isEmpty() 
                ? editUserDto.getEmail() 
                : currentUser.getUsername();
        
        if (!email.equals(currentUser.getUsername()) && userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
          
        String fullName = "";
        String identifier = "";
          if (currentUser instanceof Mahasiswa) {
            Mahasiswa mahasiswa = (Mahasiswa) currentUser;
            fullName = editUserDto.getFullName() != null && !editUserDto.getFullName().isEmpty() 
                    ? editUserDto.getFullName() 
                    : mahasiswa.getFullName();
            
            identifier = editUserDto.getIdentifier() != null && !editUserDto.getIdentifier().isEmpty() 
                    ? editUserDto.getIdentifier() 
                    : mahasiswa.getNim();
        } else if (currentUser instanceof Dosen) {
            Dosen dosen = (Dosen) currentUser;
            fullName = editUserDto.getFullName() != null && !editUserDto.getFullName().isEmpty() 
                    ? editUserDto.getFullName() 
                    : dosen.getFullName();
                    
            identifier = editUserDto.getIdentifier() != null && !editUserDto.getIdentifier().isEmpty() 
                    ? editUserDto.getIdentifier() 
                    : dosen.getNip();
        } else if (editUserDto.getFullName() != null && !editUserDto.getFullName().isEmpty()) {
            fullName = editUserDto.getFullName();
        }
          boolean emailChanged = !oldEmail.equals(email);
          boolean passwordChanged = editUserDto.getPassword() != null && !editUserDto.getPassword().isEmpty() && !passwordEncoder.matches(editUserDto.getPassword(), oldPassword);
        if (currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(newRole.getValue()))) {
            if (editUserDto.getPassword() != null && !editUserDto.getPassword().isEmpty()) {
                currentUser.setPassword(password);
            }
            
            if (currentUser instanceof Mahasiswa) {
                Mahasiswa mahasiswa = (Mahasiswa) currentUser;
                mahasiswa.setFullName(fullName);
                mahasiswa.setNim(identifier);
                
                if (!email.equals(mahasiswa.getUsername())) {
                    mahasiswa.setUsername(email);
                }
                
                User savedUser = userRepository.save(mahasiswa);
                UserResponseDto dto = mapUserToDto(savedUser);
                return dto;
            } else if (currentUser instanceof Dosen) {
                Dosen dosen = (Dosen) currentUser;
                dosen.setFullName(fullName);
                dosen.setNip(identifier);
                
                if (!email.equals(dosen.getUsername())) {
                    dosen.setUsername(email);
                }
                
                User savedUser = userRepository.save(dosen);
                return mapUserToDto(savedUser);
            }
            if (!email.equals(currentUser.getUsername())) {
                currentUser.setUsername(email);
                userRepository.save(currentUser);
            }
            if (emailChanged || passwordChanged) {
                asyncLogoutService.logoutUser(currentUser.getId().toString());
            }
            return mapUserToDto(currentUser);
        }        User newUser;
        UUID currentId = currentUser.getId();
        System.out.println(editUserDto.getPassword());
        System.out.println(password);
        if (newRole == UserRoleEnums.ADMIN) {
            newUser = new Admin(email, password);
            newUser.setId(currentId);
        } else {
            newUser = UserFactory.createUser(newRole, email, password, fullName, identifier, currentId);
        }
        
        try {
            userRepository.delete(currentUser);
            userRepository.flush();
            User savedUser = userRepository.saveAndFlush(newUser);
            if (!savedUser.getId().equals(currentId)) {
                throw new IllegalStateException("User ID changed during role update: expected " + currentId + " but got " + savedUser.getId());
            }
            if (emailChanged || passwordChanged) {
                asyncLogoutService.logoutUser(currentUser.getId().toString());
            }
            return mapUserToDto(savedUser);
        } catch (Exception e) {
            System.err.println("Error during user role update: " + e.getMessage());
            System.err.println("User ID: " + currentId + ", New Role: " + newRole);
            e.printStackTrace();
            throw e;
        }
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
            String nim = mahasiswa.getNim();
            System.out.println("Mapping mahasiswa with NIM: " + nim);
            dto.setNim(nim);
        } else if (user instanceof Dosen) {
            Dosen dosen = (Dosen) user;
            dto.setFullName(dosen.getFullName());
            dto.setNip(dosen.getNip());
        }
        
        return dto;
    }
}