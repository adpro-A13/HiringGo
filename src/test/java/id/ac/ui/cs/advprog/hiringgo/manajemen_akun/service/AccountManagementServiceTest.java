package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.AdminDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.ChangeRoleDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.DosenDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AccountManagementService accountManagementService;

    private Admin testAdmin;
    private Dosen testDosen;
    private Mahasiswa testMahasiswa;
    private UUID testAdminId;
    private UUID testDosenId;
    private UUID testMahasiswaId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountManagementService = new AccountManagementService(userRepository, passwordEncoder);

        testAdminId = UUID.randomUUID();
        testAdmin = new Admin("admin@test.com", "encoded_password");
        testAdmin.setId(testAdminId);

        testDosenId = UUID.randomUUID();
        testDosen = new Dosen();
        testDosen.setId(testDosenId);
        testDosen.setUsername("dosen@test.com");
        testDosen.setPassword("encoded_password");
        testDosen.setFullName("Test Dosen");
        testDosen.setNip("12345");

        testMahasiswaId = UUID.randomUUID();
        testMahasiswa = new Mahasiswa();
        testMahasiswa.setId(testMahasiswaId);
        testMahasiswa.setUsername("mahasiswa@test.com");
        testMahasiswa.setPassword("encoded_password");
        testMahasiswa.setFullName("Test Mahasiswa");
        testMahasiswa.setNim("54321");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        List<User> userList = Arrays.asList(testAdmin, testDosen, testMahasiswa);
        when(userRepository.findAll()).thenReturn(userList);

        List<UserResponseDto> result = accountManagementService.getAllUsers();

        assertEquals(3, result.size());
        
        boolean foundAdmin = false;
        boolean foundDosen = false;
        boolean foundMahasiswa = false;
        
        for (UserResponseDto dto : result) {
            if (dto.getId().equals(testAdminId.toString())) {
                foundAdmin = true;
                assertEquals("admin@test.com", dto.getEmail());
                assertEquals("ADMIN", dto.getRole());
            } else if (dto.getId().equals(testDosenId.toString())) {
                foundDosen = true;
                assertEquals("dosen@test.com", dto.getEmail());
                assertEquals("DOSEN", dto.getRole());
                assertEquals("Test Dosen", dto.getFullName());
                assertEquals("12345", dto.getNip());
            } else if (dto.getId().equals(testMahasiswaId.toString())) {
                foundMahasiswa = true;
                assertEquals("mahasiswa@test.com", dto.getEmail());
                assertEquals("MAHASISWA", dto.getRole());
                assertEquals("Test Mahasiswa", dto.getFullName());
                assertEquals("54321", dto.getNim());
            }
        }
        
        assertTrue(foundAdmin && foundDosen && foundMahasiswa);
    }

    @Test
    void getUserById_withValidId_shouldReturnUser() {
        when(userRepository.findById(testDosenId)).thenReturn(Optional.of(testDosen));

        UserResponseDto result = accountManagementService.getUserById(testDosenId.toString());

        assertEquals(testDosenId.toString(), result.getId());
        assertEquals("dosen@test.com", result.getEmail());
        assertEquals("DOSEN", result.getRole());
        assertEquals("Test Dosen", result.getFullName());
        assertEquals("12345", result.getNip());
    }

    @Test
    void getUserById_withInvalidIdFormat_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.getUserById("invalid-uuid");
        });
        
        assertEquals("Invalid user ID format", exception.getMessage());
    }

    @Test
    void getUserById_withNonExistentId_shouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.getUserById(nonExistentId.toString());
        });
        
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createDosenAccount_withValidData_shouldCreateDosen() {
        DosenDto dosenDto = new DosenDto();
        dosenDto.setEmail("new.dosen@test.com");
        dosenDto.setPassword("password");
        dosenDto.setFullName("New Dosen");
        dosenDto.setNip("67890");
        
        when(userRepository.findByEmail(dosenDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(Dosen.class))).thenAnswer(invocation -> {
            Dosen savedDosen = invocation.getArgument(0);
            savedDosen.setId(UUID.randomUUID());
            return savedDosen;
        });

        UserResponseDto result = accountManagementService.createDosenAccount(dosenDto);

        assertNotNull(result);
        assertEquals("new.dosen@test.com", result.getEmail());
        assertEquals("DOSEN", result.getRole());
        assertEquals("New Dosen", result.getFullName());
        assertEquals("67890", result.getNip());
        
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(Dosen.class));
    }

    @Test
    void createDosenAccount_withMissingEmail_shouldThrowException() {
        DosenDto dosenDto = new DosenDto();
        dosenDto.setPassword("password");
        dosenDto.setFullName("New Dosen");
        dosenDto.setNip("67890");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createDosenAccount(dosenDto);
        });
        
        assertEquals("Email is required", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createDosenAccount_withMissingPassword_shouldThrowException() {
        DosenDto dosenDto = new DosenDto();
        dosenDto.setEmail("new.dosen@test.com");
        dosenDto.setFullName("New Dosen");
        dosenDto.setNip("67890");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createDosenAccount(dosenDto);
        });
        
        assertEquals("Password is required", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createDosenAccount_withMissingFullName_shouldThrowException() {
        DosenDto dosenDto = new DosenDto();
        dosenDto.setEmail("new.dosen@test.com");
        dosenDto.setPassword("password");
        dosenDto.setNip("67890");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createDosenAccount(dosenDto);
        });
        
        assertEquals("Full name is required", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createDosenAccount_withMissingNip_shouldThrowException() {
        DosenDto dosenDto = new DosenDto();
        dosenDto.setEmail("new.dosen@test.com");
        dosenDto.setPassword("password");
        dosenDto.setFullName("New Dosen");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createDosenAccount(dosenDto);
        });
        
        assertEquals("NIP is required", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createDosenAccount_withExistingEmail_shouldThrowException() {
        DosenDto dosenDto = new DosenDto();
        dosenDto.setEmail("existing@test.com");
        dosenDto.setPassword("password");
        dosenDto.setFullName("New Dosen");
        dosenDto.setNip("67890");
        
        when(userRepository.findByEmail(dosenDto.getEmail())).thenReturn(Optional.of(testDosen));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createDosenAccount(dosenDto);
        });
        
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createAdminAccount_withValidData_shouldCreateAdmin() {
        AdminDto adminDto = new AdminDto();
        adminDto.setEmail("new.admin@test.com");
        adminDto.setPassword("password");
        
        when(userRepository.findByEmail(adminDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(Admin.class))).thenAnswer(invocation -> {
            Admin savedAdmin = invocation.getArgument(0);
            savedAdmin.setId(UUID.randomUUID());
            return savedAdmin;
        });

        UserResponseDto result = accountManagementService.createAdminAccount(adminDto);

        assertNotNull(result);
        assertEquals("new.admin@test.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
        
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(Admin.class));
    }

    @Test
    void createAdminAccount_withMissingEmail_shouldThrowException() {
        AdminDto adminDto = new AdminDto();
        adminDto.setPassword("password");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createAdminAccount(adminDto);
        });
        
        assertEquals("Email is required", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createAdminAccount_withMissingPassword_shouldThrowException() {
        AdminDto adminDto = new AdminDto();
        adminDto.setEmail("new.admin@test.com");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createAdminAccount(adminDto);
        });
        
        assertEquals("Password is required", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createAdminAccount_withExistingEmail_shouldThrowException() {
        AdminDto adminDto = new AdminDto();
        adminDto.setEmail("existing@test.com");
        adminDto.setPassword("password");
        
        when(userRepository.findByEmail(adminDto.getEmail())).thenReturn(Optional.of(testAdmin));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createAdminAccount(adminDto);
        });
        
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changeUserRole_fromMahasiswaToAdmin_shouldChangeRole() {
        ChangeRoleDto changeRoleDto = new ChangeRoleDto();
        changeRoleDto.setNewRole("ADMIN");
        
        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(userRepository.save(any(Admin.class))).thenAnswer(invocation -> {
            Admin savedAdmin = invocation.getArgument(0);
            savedAdmin.setId(UUID.randomUUID());
            return savedAdmin;
        });

        UserResponseDto result = accountManagementService.changeUserRole(testMahasiswaId.toString(), changeRoleDto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        
        verify(userRepository).delete(testMahasiswa);
        verify(userRepository).save(any(Admin.class));
    }

    @Test
    void changeUserRole_fromAdminToMahasiswa_shouldChangeRole() {
        ChangeRoleDto changeRoleDto = new ChangeRoleDto();
        changeRoleDto.setNewRole("MAHASISWA");
        
        when(userRepository.findById(testAdminId)).thenReturn(Optional.of(testAdmin));
        when(userRepository.save(any(Mahasiswa.class))).thenAnswer(invocation -> {
            Mahasiswa savedMahasiswa = invocation.getArgument(0);
            savedMahasiswa.setId(UUID.randomUUID());
            return savedMahasiswa;
        });

        UserResponseDto result = accountManagementService.changeUserRole(testAdminId.toString(), changeRoleDto);

        assertNotNull(result);
        assertEquals("MAHASISWA", result.getRole());
        
        verify(userRepository).delete(testAdmin);
        verify(userRepository).save(any(Mahasiswa.class));
    }

    @Test
    void changeUserRole_fromDosenToMahasiswa_shouldChangeRole() {
        ChangeRoleDto changeRoleDto = new ChangeRoleDto();
        changeRoleDto.setNewRole("MAHASISWA");
        
        when(userRepository.findById(testDosenId)).thenReturn(Optional.of(testDosen));
        when(userRepository.save(any(Mahasiswa.class))).thenAnswer(invocation -> {
            Mahasiswa savedMahasiswa = invocation.getArgument(0);
            savedMahasiswa.setId(UUID.randomUUID());
            return savedMahasiswa;
        });

        UserResponseDto result = accountManagementService.changeUserRole(testDosenId.toString(), changeRoleDto);

        assertNotNull(result);
        assertEquals("MAHASISWA", result.getRole());
        
        verify(userRepository).delete(testDosen);
        verify(userRepository).save(any(Mahasiswa.class));
    }

    @Test
    void changeUserRole_toSameRole_shouldNotChangeAnything() {
        ChangeRoleDto changeRoleDto = new ChangeRoleDto();
        changeRoleDto.setNewRole("ADMIN");
        
        when(userRepository.findById(testAdminId)).thenReturn(Optional.of(testAdmin));

        UserResponseDto result = accountManagementService.changeUserRole(testAdminId.toString(), changeRoleDto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        assertEquals(testAdminId.toString(), result.getId());
        
        verify(userRepository, never()).delete(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changeUserRole_withInvalidIdFormat_shouldThrowException() {
        ChangeRoleDto changeRoleDto = new ChangeRoleDto();
        changeRoleDto.setNewRole("ADMIN");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.changeUserRole("invalid-uuid", changeRoleDto);
        });
        
        assertEquals("Invalid user ID format", exception.getMessage());
    }

    @Test
    void changeUserRole_withNonExistentId_shouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        ChangeRoleDto changeRoleDto = new ChangeRoleDto();
        changeRoleDto.setNewRole("ADMIN");
        
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.changeUserRole(nonExistentId.toString(), changeRoleDto);
        });
        
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void changeUserRole_withInvalidRole_shouldThrowException() {
        ChangeRoleDto changeRoleDto = new ChangeRoleDto();
        changeRoleDto.setNewRole("INVALID_ROLE");
        
        when(userRepository.findById(testAdminId)).thenReturn(Optional.of(testAdmin));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.changeUserRole(testAdminId.toString(), changeRoleDto);
        });
        
        assertEquals("Invalid role. Valid roles are: ADMIN, DOSEN, MAHASISWA", exception.getMessage());
    }

    @Test
    void deleteUser_withValidId_shouldDeleteUser() {
        when(userRepository.findById(testAdminId)).thenReturn(Optional.of(testAdmin));

        accountManagementService.deleteUser(testAdminId.toString());

        verify(userRepository).delete(testAdmin);
    }

    @Test
    void deleteUser_withInvalidIdFormat_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.deleteUser("invalid-uuid");
        });
        
        assertEquals("Invalid user ID format", exception.getMessage());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteUser_withNonExistentId_shouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.deleteUser(nonExistentId.toString());
        });
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).delete(any());
    }
}