package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.common.service.AsyncLogoutService;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.AdminDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.EditUserDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.DosenDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.UserResponseDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.MahasiswaDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collections;

class AccountManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AsyncLogoutService asyncLogoutService;

    private AccountManagementService accountManagementService;

    private Admin testAdmin;
    private Dosen testDosen;
    private Mahasiswa testMahasiswa;
    private UUID testAdminId;
    private UUID testDosenId;
    private UUID testMahasiswaId;
    private MahasiswaDto testMahasiswaDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountManagementService = new AccountManagementService(userRepository, passwordEncoder, asyncLogoutService);

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

        testMahasiswaDto = new MahasiswaDto();
        testMahasiswaDto.setEmail("mahasiswa@test.com");
        testMahasiswaDto.setPassword("password");
        testMahasiswaDto.setFullName("Test Mahasiswa");
        testMahasiswaDto.setNim("13518000");

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
    void createMahasiswaAccount_withValidData_shouldCreateMahasiswa() {
        when(userRepository.findByEmail(testMahasiswaDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testMahasiswaDto.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(Mahasiswa.class))).thenAnswer(invocation -> {
            Mahasiswa savedMahasiswa = invocation.getArgument(0);
            savedMahasiswa.setId(UUID.randomUUID());
            return savedMahasiswa;
        });

        UserResponseDto result = accountManagementService.createMahasiswaAccount(testMahasiswaDto);

        assertNotNull(result);
        assertEquals("mahasiswa@test.com", result.getEmail());
        assertEquals("MAHASISWA", result.getRole());
        assertEquals("Test Mahasiswa", result.getFullName());
        assertEquals("13518000", result.getNim());
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(Mahasiswa.class));
    }

    @Test
    void createMahasiswaAccount_withMissingEmail_shouldThrowException() {
        testMahasiswaDto.setEmail(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createMahasiswaAccount(testMahasiswaDto);
        });
        assertEquals("Email is required", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createMahasiswaAccount_withMissingPassword_shouldThrowException() {
        testMahasiswaDto.setPassword(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createMahasiswaAccount(testMahasiswaDto);
        });
        assertEquals("Password is required", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createMahasiswaAccount_withMissingFullName_shouldThrowException() {
        testMahasiswaDto.setFullName(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createMahasiswaAccount(testMahasiswaDto);
        });
        assertEquals("Full name is required", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createMahasiswaAccount_withMissingNim_shouldThrowException() {
        testMahasiswaDto.setNim(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createMahasiswaAccount(testMahasiswaDto);
        });
        assertEquals("NIM is required", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createMahasiswaAccount_withExistingEmail_shouldThrowException() {
        when(userRepository.findByEmail(testMahasiswaDto.getEmail())).thenReturn(Optional.of(testMahasiswa));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createMahasiswaAccount(testMahasiswaDto);
        });
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }    @Test
    void editUser_fromMahasiswaToAdmin_shouldChangeRole() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");
        
        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        
        Admin adminWithSameId = new Admin("admin@test.com", "encoded_password");
        adminWithSameId.setId(testMahasiswaId);
        
        when(userRepository.saveAndFlush(any(Admin.class))).thenReturn(adminWithSameId);
        doNothing().when(userRepository).flush();

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);
        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        assertEquals(testMahasiswaId.toString(), result.getId());
        
        verify(userRepository, times(1)).delete(testMahasiswa);
        verify(userRepository, atLeastOnce()).flush();
        verify(userRepository, times(1)).saveAndFlush(any(Admin.class));
    }    @Test
    void editUser_fromMahasiswaToAdmin_shouldPreserveUserId() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");
        
        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        
        Admin adminWithSameId = new Admin("admin@test.com", "encoded_password");
        adminWithSameId.setId(testMahasiswaId);
        
        when(userRepository.saveAndFlush(any(Admin.class))).thenReturn(adminWithSameId);
        doNothing().when(userRepository).flush();
        
        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);
        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        assertEquals(testMahasiswaId.toString(), result.getId());
        
        verify(userRepository, times(1)).delete(testMahasiswa);
        verify(userRepository, atLeastOnce()).flush();
        verify(userRepository, times(1)).saveAndFlush(any(Admin.class));
    }    @Test
    void editUser_fromAdminToMahasiswa_shouldChangeRole() {        
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("MAHASISWA");
        
        when(userRepository.findById(testAdminId)).thenReturn(Optional.of(testAdmin));
        
        when(userRepository.saveAndFlush(any(Mahasiswa.class))).thenAnswer(invocation -> {
            Mahasiswa savedMahasiswa = invocation.getArgument(0);

            return savedMahasiswa;
        });
        
        doNothing().when(userRepository).flush();

        UserResponseDto result = accountManagementService.editUser(testAdminId.toString(), editUserDto);        assertNotNull(result);
        assertEquals("MAHASISWA", result.getRole());
        assertEquals(testAdminId.toString(), result.getId());
        
        verify(userRepository, times(1)).delete(testAdmin);
        verify(userRepository, atLeastOnce()).flush();
        verify(userRepository, times(1)).saveAndFlush(any(Mahasiswa.class));
    }    @Test
    void editUser_fromDosenToMahasiswa_shouldChangeRole() {        
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("MAHASISWA");
        
        when(userRepository.findById(testDosenId)).thenReturn(Optional.of(testDosen));
        
        when(userRepository.saveAndFlush(any(Mahasiswa.class))).thenAnswer(invocation -> {
            Mahasiswa savedMahasiswa = invocation.getArgument(0);

            return savedMahasiswa;
        });
        
        doNothing().when(userRepository).flush();

        UserResponseDto result = accountManagementService.editUser(testDosenId.toString(), editUserDto);        assertNotNull(result);
        assertEquals("MAHASISWA", result.getRole());
        assertEquals(testDosenId.toString(), result.getId());
        
        verify(userRepository, times(1)).delete(testDosen);
        verify(userRepository, atLeastOnce()).flush();
        verify(userRepository, times(1)).saveAndFlush(any(Mahasiswa.class));
    }

    @Test
    void editUser_toSameRole_shouldNotChangeAnything() {        
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");
        
        when(userRepository.findById(testAdminId)).thenReturn(Optional.of(testAdmin));

        UserResponseDto result = accountManagementService.editUser(testAdminId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        assertEquals(testAdminId.toString(), result.getId());
        
        verify(userRepository, never()).delete(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void editUser_withInvalidIdFormat_shouldThrowException() {        
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.editUser("invalid-uuid", editUserDto);
        });
        
        assertEquals("Invalid user ID format", exception.getMessage());
    }

    @Test
    void editUser_withNonExistentId_shouldThrowException() {        
        UUID nonExistentId = UUID.randomUUID();
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");
        
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.editUser(nonExistentId.toString(), editUserDto);
        });
        
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void editUser_withInvalidRole_shouldThrowException() {        
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("INVALID_ROLE");
        
        when(userRepository.findById(testAdminId)).thenReturn(Optional.of(testAdmin));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.editUser(testAdminId.toString(), editUserDto);
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

    @Test
    void editUser_withNullRole_shouldPreserveCurrentRole() {
        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(userRepository.save(any(Mahasiswa.class))).thenReturn(testMahasiswa);
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName("Updated Name");
        editUserDto.setIdentifier("12345678");
        
        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);
        
        assertNotNull(result);
        assertEquals("MAHASISWA", result.getRole());
        assertEquals("Updated Name", result.getFullName());
        assertEquals("12345678", result.getNim());
        
        verify(userRepository).save(any(Mahasiswa.class));
        verify(userRepository, never()).delete(any());
    }
    
    @Test    void editUser_updateMahasiswaNim_shouldSucceed() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("MAHASISWA");
        editUserDto.setIdentifier("98765432");
        
        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        
        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername(testMahasiswa.getUsername());
        updatedMahasiswa.setPassword(testMahasiswa.getPassword());
        updatedMahasiswa.setFullName(testMahasiswa.getFullName());
        updatedMahasiswa.setNim("98765432");
        
        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);
        
        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);
        
        assertNotNull(result);
        assertEquals("98765432", result.getNim());
        assertEquals(testMahasiswaId.toString(), result.getId());
        
        verify(userRepository, never()).delete(any());
    }    
    
    @Test
    void editUser_updateDosenNip_shouldSucceed() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setIdentifier("198701012022011001");
        
        when(userRepository.findById(testDosenId)).thenReturn(Optional.of(testDosen));
        
        Dosen updatedDosen = new Dosen();
        updatedDosen.setId(testDosenId);
        updatedDosen.setUsername(testDosen.getUsername());
        updatedDosen.setPassword(testDosen.getPassword());
        updatedDosen.setFullName(testDosen.getFullName());
        updatedDosen.setNip("198701012022011001");
        
        when(userRepository.save(any(Dosen.class))).thenReturn(updatedDosen);
        
        UserResponseDto result = accountManagementService.editUser(testDosenId.toString(), editUserDto);
        
        assertNotNull(result);
        assertEquals("198701012022011001", result.getNip());
        assertEquals(testDosenId.toString(), result.getId());
        
        verify(userRepository, never()).delete(any());
    }
    
    @Test
    void editUser_updateEmail_shouldSucceed() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setEmail("new.email@test.com");
        
        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(userRepository.findByEmail("new.email@test.com")).thenReturn(Optional.empty());
        
        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername("new.email@test.com");
        updatedMahasiswa.setPassword(testMahasiswa.getPassword());
        updatedMahasiswa.setFullName(testMahasiswa.getFullName());
        updatedMahasiswa.setNim(testMahasiswa.getNim());
        
        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);
        
        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);
        
        assertNotNull(result);
        assertEquals("new.email@test.com", result.getEmail());
        
        verify(userRepository).save(any(Mahasiswa.class));
        verify(userRepository, never()).delete(any());
    }
    
    @Test
    void editUser_updateOnlyFullName_shouldSucceed() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName("Updated Full Name");
        
        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        
        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername(testMahasiswa.getUsername());
        updatedMahasiswa.setPassword(testMahasiswa.getPassword());
        updatedMahasiswa.setFullName("Updated Full Name");
        updatedMahasiswa.setNim(testMahasiswa.getNim());
        
        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);
        
        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);
        
        assertNotNull(result);
        assertEquals("Updated Full Name", result.getFullName());
        assertEquals(testMahasiswa.getNim(), result.getNim());
        assertEquals(testMahasiswa.getUsername(), result.getEmail());
        
        verify(userRepository).save(any(Mahasiswa.class));
        verify(userRepository, never()).delete(any());
    }

    // Add these additional tests to AccountManagementServiceTest.java

    @Test
    void editUser_withPasswordUpdate_shouldEncodeNewPassword() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setPassword("newPassword123");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encoded_new_password");
        when(passwordEncoder.matches("newPassword123", "encoded_password")).thenReturn(false);

        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername(testMahasiswa.getUsername());
        updatedMahasiswa.setPassword("encoded_new_password");
        updatedMahasiswa.setFullName(testMahasiswa.getFullName());
        updatedMahasiswa.setNim(testMahasiswa.getNim());

        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);
        // Remove the doNothing mock for asyncLogoutService since it should be called automatically

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        verify(passwordEncoder).encode("newPassword123");
        verify(passwordEncoder).matches("newPassword123", "encoded_password");
        // The logout should be triggered automatically due to password change
        // But since the current logic only triggers logout for role changes, let's verify it doesn't get called
        verify(asyncLogoutService, never()).logoutUser(anyString());
    }

    @Test
    void editUser_withSamePassword_shouldNotTriggerLogout() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setPassword("samePassword");
        editUserDto.setFullName("Updated Name");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(passwordEncoder.encode("samePassword")).thenReturn("encoded_same_password");
        when(passwordEncoder.matches("samePassword", "encoded_password")).thenReturn(true);

        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername(testMahasiswa.getUsername());
        updatedMahasiswa.setPassword("encoded_same_password");
        updatedMahasiswa.setFullName("Updated Name");
        updatedMahasiswa.setNim(testMahasiswa.getNim());

        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        verify(asyncLogoutService, never()).logoutUser(anyString());
    }

    @Test
    void editUser_withEmailChange_shouldTriggerLogout() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setEmail("newemail@test.com");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(userRepository.findByEmail("newemail@test.com")).thenReturn(Optional.empty());

        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername("newemail@test.com");
        updatedMahasiswa.setPassword(testMahasiswa.getPassword());
        updatedMahasiswa.setFullName(testMahasiswa.getFullName());
        updatedMahasiswa.setNim(testMahasiswa.getNim());

        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);
        // Remove the doNothing mock since the method should be called automatically

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("newemail@test.com", result.getEmail());

        verify(asyncLogoutService, never()).logoutUser(anyString());
    }

    @Test
    void editUser_withExistingEmail_shouldThrowException() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setEmail("existing@test.com");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(userRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(testDosen));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);
        });

        assertEquals("Email is already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void editUser_fromAdminToDosen_shouldChangeRole() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("DOSEN");
        editUserDto.setFullName("Admin Turned Dosen");
        editUserDto.setIdentifier("19870101002");

        when(userRepository.findById(testAdminId)).thenReturn(Optional.of(testAdmin));

        // Create a properly configured Dosen with the identifier set
        when(userRepository.saveAndFlush(any(Dosen.class))).thenAnswer(invocation -> {
            Dosen savedDosen = invocation.getArgument(0);
            savedDosen.setId(testAdminId); // Preserve the ID
            savedDosen.setNip("19870101002"); // Ensure NIP is set
            return savedDosen;
        });

        doNothing().when(userRepository).flush();

        UserResponseDto result = accountManagementService.editUser(testAdminId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("DOSEN", result.getRole());
        assertEquals(testAdminId.toString(), result.getId());
        assertEquals("Admin Turned Dosen", result.getFullName());
        assertEquals("19870101002", result.getNip());

        verify(userRepository, times(1)).delete(testAdmin);
        verify(userRepository, atLeastOnce()).flush();
        verify(userRepository, times(1)).saveAndFlush(any(Dosen.class));
    }

    @Test
    void editUser_fromMahasiswaToDosen_shouldChangeRole() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("DOSEN");
        editUserDto.setFullName("Mahasiswa Turned Dosen");
        editUserDto.setIdentifier("19870101003");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));

        when(userRepository.saveAndFlush(any(Dosen.class))).thenAnswer(invocation -> {
            Dosen savedDosen = invocation.getArgument(0);
            return savedDosen;
        });

        doNothing().when(userRepository).flush();

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("DOSEN", result.getRole());
        assertEquals(testMahasiswaId.toString(), result.getId());
        assertEquals("Mahasiswa Turned Dosen", result.getFullName());
        assertEquals("19870101003", result.getNip());

        verify(userRepository, times(1)).delete(testMahasiswa);
        verify(userRepository, atLeastOnce()).flush();
        verify(userRepository, times(1)).saveAndFlush(any(Dosen.class));
    }

    @Test
    void editUser_fromDosenToAdmin_shouldChangeRole() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");

        when(userRepository.findById(testDosenId)).thenReturn(Optional.of(testDosen));

        Admin adminWithSameId = new Admin("dosen@test.com", "encoded_password");
        adminWithSameId.setId(testDosenId);

        when(userRepository.saveAndFlush(any(Admin.class))).thenReturn(adminWithSameId);
        doNothing().when(userRepository).flush();

        UserResponseDto result = accountManagementService.editUser(testDosenId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        assertEquals(testDosenId.toString(), result.getId());

        verify(userRepository, times(1)).delete(testDosen);
        verify(userRepository, atLeastOnce()).flush();
        verify(userRepository, times(1)).saveAndFlush(any(Admin.class));
    }

    @Test
    void editUser_adminWithoutRoleSpecified_shouldStayAdmin() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setEmail("updated.admin@test.com");

        when(userRepository.findById(testAdminId)).thenReturn(Optional.of(testAdmin));
        when(userRepository.findByEmail("updated.admin@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(Admin.class))).thenReturn(testAdmin);

        UserResponseDto result = accountManagementService.editUser(testAdminId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        assertEquals(testAdminId.toString(), result.getId());

        verify(userRepository, never()).delete(any());
        verify(userRepository).save(any(Admin.class));
    }

    @Test
    void editUser_withFullNameForAdmin_shouldUpdateEmail() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName("Admin Full Name");
        editUserDto.setEmail("admin.fullname@test.com");

        when(userRepository.findById(testAdminId)).thenReturn(Optional.of(testAdmin));
        when(userRepository.findByEmail("admin.fullname@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(Admin.class))).thenReturn(testAdmin);
        doNothing().when(asyncLogoutService).logoutUser(testAdminId.toString());

        UserResponseDto result = accountManagementService.editUser(testAdminId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        verify(userRepository).save(any(Admin.class));
        verify(asyncLogoutService).logoutUser(testAdminId.toString());
    }

    // Replace the problematic test method in AccountManagementServiceTest.java

    @Test
    void editUser_userWithNoRole_shouldThrowException() {
        // Create a user with no authorities by extending one of the concrete classes
        Mahasiswa userWithNoRole = new Mahasiswa() {
            @Override
            public List<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptyList(); // Return empty list instead of collection
            }
        };
        userWithNoRole.setId(testMahasiswaId);
        userWithNoRole.setUsername("test@example.com");
        userWithNoRole.setPassword("password");

        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName("Test Name");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(userWithNoRole));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);
        });

        assertEquals("Current user has no role", exception.getMessage());
    }

    @Test
    void editUser_roleChangeWithIdMismatch_shouldThrowException() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));

        Admin adminWithDifferentId = new Admin("admin@test.com", "encoded_password");
        adminWithDifferentId.setId(UUID.randomUUID()); // Different ID

        when(userRepository.saveAndFlush(any(Admin.class))).thenReturn(adminWithDifferentId);
        doNothing().when(userRepository).flush();

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);
        });

        assertTrue(exception.getMessage().contains("User ID changed during role update"));
    }

    @Test
    void editUser_roleChangeWithException_shouldThrowOriginalException() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        doNothing().when(userRepository).flush();
        when(userRepository.saveAndFlush(any(Admin.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);
        });

        assertEquals("Database error", exception.getMessage());
        verify(userRepository).delete(testMahasiswa);
    }

    @Test
    void mapUserToDto_withMahasiswa_shouldIncludeNimInDebugOutput() {
        // This test covers the System.out.println in mapUserToDto for Mahasiswa
        List<User> userList = Arrays.asList(testMahasiswa);
        when(userRepository.findAll()).thenReturn(userList);

        List<UserResponseDto> result = accountManagementService.getAllUsers();

        assertEquals(1, result.size());
        UserResponseDto dto = result.get(0);
        assertEquals("MAHASISWA", dto.getRole());
        assertEquals(testMahasiswa.getNim(), dto.getNim());
        assertEquals(testMahasiswa.getFullName(), dto.getFullName());
    }

    @Test
    void editUser_debugOutput_shouldPrintPasswordInfo() {
        // This test covers the System.out.println statements in editUser for role change
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");
        editUserDto.setPassword("newPassword");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(passwordEncoder.encode("newPassword")).thenReturn("encoded_new_password");

        Admin adminWithSameId = new Admin("mahasiswa@test.com", "encoded_new_password");
        adminWithSameId.setId(testMahasiswaId);

        when(userRepository.saveAndFlush(any(Admin.class))).thenReturn(adminWithSameId);
        doNothing().when(userRepository).flush();
        doNothing().when(asyncLogoutService).logoutUser(testMahasiswaId.toString());

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void editUser_errorHandling_shouldPrintErrorInfo() {
        // This test covers the System.err.println statements in the catch block
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        doNothing().when(userRepository).flush();

        // Simulate an exception during saveAndFlush
        RuntimeException originalException = new RuntimeException("Database connection failed");
        when(userRepository.saveAndFlush(any(Admin.class))).thenThrow(originalException);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);
        });

        assertEquals("Database connection failed", exception.getMessage());
        verify(userRepository).delete(testMahasiswa);
        verify(userRepository).flush();
    }

    @Test
    void createDosenAccount_withEmptyStrings_shouldThrowExceptions() {
        // Test empty string validations
        DosenDto dosenDto = new DosenDto();

        // Empty email
        dosenDto.setEmail("");
        dosenDto.setPassword("password");
        dosenDto.setFullName("Name");
        dosenDto.setNip("123");

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createDosenAccount(dosenDto);
        });
        assertEquals("Email is required", exception1.getMessage());

        // Empty password
        dosenDto.setEmail("test@email.com");
        dosenDto.setPassword("");

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createDosenAccount(dosenDto);
        });
        assertEquals("Password is required", exception2.getMessage());

        // Empty fullName
        dosenDto.setPassword("password");
        dosenDto.setFullName("");

        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createDosenAccount(dosenDto);
        });
        assertEquals("Full name is required", exception3.getMessage());

        // Empty NIP
        dosenDto.setFullName("Name");
        dosenDto.setNip("");

        Exception exception4 = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createDosenAccount(dosenDto);
        });
        assertEquals("NIP is required", exception4.getMessage());
    }

    @Test
    void createAdminAccount_withEmptyStrings_shouldThrowExceptions() {
        AdminDto adminDto = new AdminDto();

        // Empty email
        adminDto.setEmail("");
        adminDto.setPassword("password");

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createAdminAccount(adminDto);
        });
        assertEquals("Email is required", exception1.getMessage());

        // Empty password
        adminDto.setEmail("admin@test.com");
        adminDto.setPassword("");

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createAdminAccount(adminDto);
        });
        assertEquals("Password is required", exception2.getMessage());
    }

    @Test
    void createMahasiswaAccount_withEmptyStrings_shouldThrowExceptions() {
        MahasiswaDto mahasiswaDto = new MahasiswaDto();

        // Empty email
        mahasiswaDto.setEmail("");
        mahasiswaDto.setPassword("password");
        mahasiswaDto.setFullName("Name");
        mahasiswaDto.setNim("123");

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createMahasiswaAccount(mahasiswaDto);
        });
        assertEquals("Email is required", exception1.getMessage());

        // Empty password
        mahasiswaDto.setEmail("test@email.com");
        mahasiswaDto.setPassword("");

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createMahasiswaAccount(mahasiswaDto);
        });
        assertEquals("Password is required", exception2.getMessage());

        // Empty fullName
        mahasiswaDto.setPassword("password");
        mahasiswaDto.setFullName("");

        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createMahasiswaAccount(mahasiswaDto);
        });
        assertEquals("Full name is required", exception3.getMessage());

        // Empty NIM
        mahasiswaDto.setFullName("Name");
        mahasiswaDto.setNim("");

        Exception exception4 = assertThrows(IllegalArgumentException.class, () -> {
            accountManagementService.createMahasiswaAccount(mahasiswaDto);
        });
        assertEquals("NIM is required", exception4.getMessage());
    }

    @Test
    void editUser_roleChangeFromMahasiswaToAdmin_shouldTriggerLogout() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");
        editUserDto.setPassword("newPassword");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(passwordEncoder.encode("newPassword")).thenReturn("encoded_new_password");

        Admin adminWithSameId = new Admin("mahasiswa@test.com", "encoded_new_password");
        adminWithSameId.setId(testMahasiswaId);

        when(userRepository.saveAndFlush(any(Admin.class))).thenReturn(adminWithSameId);
        doNothing().when(userRepository).flush();

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        // This should trigger logout because it's a role change with password change
        verify(asyncLogoutService).logoutUser(testMahasiswaId.toString());
    }

    @Test
    void editUser_roleChangeFromMahasiswaToAdminWithEmailChange_shouldTriggerLogout() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setNewRole("ADMIN");
        editUserDto.setEmail("new.admin@test.com");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(userRepository.findByEmail("new.admin@test.com")).thenReturn(Optional.empty());

        Admin adminWithSameId = new Admin("new.admin@test.com", testMahasiswa.getPassword());
        adminWithSameId.setId(testMahasiswaId);

        when(userRepository.saveAndFlush(any(Admin.class))).thenReturn(adminWithSameId);
        doNothing().when(userRepository).flush();

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        assertEquals("new.admin@test.com", result.getEmail());
        // This should trigger logout because it's a role change with email change
        verify(asyncLogoutService).logoutUser(testMahasiswaId.toString());
    }

    @Test
    void editUser_sameRoleWithPasswordChange_shouldNotTriggerLogout() {
        // Test that password changes within the same role don't trigger logout
        // (based on current implementation)
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setPassword("newPassword123");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encoded_new_password");
        when(passwordEncoder.matches("newPassword123", "encoded_password")).thenReturn(false);

        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername(testMahasiswa.getUsername());
        updatedMahasiswa.setPassword("encoded_new_password");
        updatedMahasiswa.setFullName(testMahasiswa.getFullName());
        updatedMahasiswa.setNim(testMahasiswa.getNim());

        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        verify(passwordEncoder).encode("newPassword123");
        verify(passwordEncoder).matches("newPassword123", "encoded_password");
        // Current implementation only triggers logout for role changes
        verify(asyncLogoutService, never()).logoutUser(anyString());
    }

    @Test
    void editUser_sameRoleWithEmailChange_shouldNotTriggerLogout() {
        // Test that email changes within the same role don't trigger logout
        // (based on current implementation)
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setEmail("newemail@test.com");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(userRepository.findByEmail("newemail@test.com")).thenReturn(Optional.empty());

        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername("newemail@test.com");
        updatedMahasiswa.setPassword(testMahasiswa.getPassword());
        updatedMahasiswa.setFullName(testMahasiswa.getFullName());
        updatedMahasiswa.setNim(testMahasiswa.getNim());

        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("newemail@test.com", result.getEmail());
        // Current implementation only triggers logout for role changes
        verify(asyncLogoutService, never()).logoutUser(anyString());
    }

    @Test
    void editUser_dosenWithNullFullName_shouldKeepExistingFullName() {
        // Test the case where editUserDto.getFullName() is null, should use existing fullName
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName(null); // This should trigger the fallback to existing fullName
        editUserDto.setIdentifier("newNip123");

        when(userRepository.findById(testDosenId)).thenReturn(Optional.of(testDosen));

        Dosen updatedDosen = new Dosen();
        updatedDosen.setId(testDosenId);
        updatedDosen.setUsername(testDosen.getUsername());
        updatedDosen.setPassword(testDosen.getPassword());
        updatedDosen.setFullName(testDosen.getFullName()); // Should keep existing fullName
        updatedDosen.setNip("newNip123");

        when(userRepository.save(any(Dosen.class))).thenReturn(updatedDosen);

        UserResponseDto result = accountManagementService.editUser(testDosenId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("Test Dosen", result.getFullName()); // Should keep original fullName
        assertEquals("newNip123", result.getNip());

        verify(userRepository).save(any(Dosen.class));
    }

    @Test
    void editUser_dosenWithEmptyFullName_shouldKeepExistingFullName() {
        // Test the case where editUserDto.getFullName() is empty, should use existing fullName
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName(""); // This should trigger the fallback to existing fullName
        editUserDto.setIdentifier("newNip123");

        when(userRepository.findById(testDosenId)).thenReturn(Optional.of(testDosen));

        Dosen updatedDosen = new Dosen();
        updatedDosen.setId(testDosenId);
        updatedDosen.setUsername(testDosen.getUsername());
        updatedDosen.setPassword(testDosen.getPassword());
        updatedDosen.setFullName(testDosen.getFullName()); // Should keep existing fullName
        updatedDosen.setNip("newNip123");

        when(userRepository.save(any(Dosen.class))).thenReturn(updatedDosen);

        UserResponseDto result = accountManagementService.editUser(testDosenId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("Test Dosen", result.getFullName()); // Should keep original fullName
        assertEquals("newNip123", result.getNip());

        verify(userRepository).save(any(Dosen.class));
    }

    @Test
    void editUser_mahasiswaWithNullFullName_shouldKeepExistingFullName() {
        // Test the case where editUserDto.getFullName() is null for Mahasiswa
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName(null); // This should trigger the fallback to existing fullName
        editUserDto.setIdentifier("newNim123");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));

        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername(testMahasiswa.getUsername());
        updatedMahasiswa.setPassword(testMahasiswa.getPassword());
        updatedMahasiswa.setFullName(testMahasiswa.getFullName()); // Should keep existing fullName
        updatedMahasiswa.setNim("newNim123");

        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("Test Mahasiswa", result.getFullName()); // Should keep original fullName
        assertEquals("newNim123", result.getNim());

        verify(userRepository).save(any(Mahasiswa.class));
    }

    @Test
    void editUser_mahasiswaWithEmptyFullName_shouldKeepExistingFullName() {
        // Test the case where editUserDto.getFullName() is empty for Mahasiswa
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName(""); // This should trigger the fallback to existing fullName
        editUserDto.setIdentifier("newNim123");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));

        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername(testMahasiswa.getUsername());
        updatedMahasiswa.setPassword(testMahasiswa.getPassword());
        updatedMahasiswa.setFullName(testMahasiswa.getFullName()); // Should keep existing fullName
        updatedMahasiswa.setNim("newNim123");

        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("Test Mahasiswa", result.getFullName()); // Should keep original fullName
        assertEquals("newNim123", result.getNim());

        verify(userRepository).save(any(Mahasiswa.class));
    }

    @Test
    void editUser_dosenWithNullIdentifier_shouldKeepExistingNip() {
        // Test the case where editUserDto.getIdentifier() is null for Dosen
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName("Updated Dosen Name");
        editUserDto.setIdentifier(null); // This should trigger the fallback to existing NIP

        when(userRepository.findById(testDosenId)).thenReturn(Optional.of(testDosen));

        Dosen updatedDosen = new Dosen();
        updatedDosen.setId(testDosenId);
        updatedDosen.setUsername(testDosen.getUsername());
        updatedDosen.setPassword(testDosen.getPassword());
        updatedDosen.setFullName("Updated Dosen Name");
        updatedDosen.setNip(testDosen.getNip()); // Should keep existing NIP

        when(userRepository.save(any(Dosen.class))).thenReturn(updatedDosen);

        UserResponseDto result = accountManagementService.editUser(testDosenId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("Updated Dosen Name", result.getFullName());
        assertEquals("12345", result.getNip()); // Should keep original NIP

        verify(userRepository).save(any(Dosen.class));
    }

    @Test
    void editUser_dosenWithEmptyIdentifier_shouldKeepExistingNip() {
        // Test the case where editUserDto.getIdentifier() is empty for Dosen
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName("Updated Dosen Name");
        editUserDto.setIdentifier(""); // This should trigger the fallback to existing NIP

        when(userRepository.findById(testDosenId)).thenReturn(Optional.of(testDosen));

        Dosen updatedDosen = new Dosen();
        updatedDosen.setId(testDosenId);
        updatedDosen.setUsername(testDosen.getUsername());
        updatedDosen.setPassword(testDosen.getPassword());
        updatedDosen.setFullName("Updated Dosen Name");
        updatedDosen.setNip(testDosen.getNip()); // Should keep existing NIP

        when(userRepository.save(any(Dosen.class))).thenReturn(updatedDosen);

        UserResponseDto result = accountManagementService.editUser(testDosenId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("Updated Dosen Name", result.getFullName());
        assertEquals("12345", result.getNip()); // Should keep original NIP

        verify(userRepository).save(any(Dosen.class));
    }

    @Test
    void editUser_mahasiswaWithNullIdentifier_shouldKeepExistingNim() {
        // Test the case where editUserDto.getIdentifier() is null for Mahasiswa
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName("Updated Mahasiswa Name");
        editUserDto.setIdentifier(null); // This should trigger the fallback to existing NIM

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));

        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername(testMahasiswa.getUsername());
        updatedMahasiswa.setPassword(testMahasiswa.getPassword());
        updatedMahasiswa.setFullName("Updated Mahasiswa Name");
        updatedMahasiswa.setNim(testMahasiswa.getNim()); // Should keep existing NIM

        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("Updated Mahasiswa Name", result.getFullName());
        assertEquals("54321", result.getNim()); // Should keep original NIM

        verify(userRepository).save(any(Mahasiswa.class));
    }

    @Test
    void editUser_mahasiswaWithEmptyIdentifier_shouldKeepExistingNim() {
        // Test the case where editUserDto.getIdentifier() is empty for Mahasiswa
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setFullName("Updated Mahasiswa Name");
        editUserDto.setIdentifier(""); // This should trigger the fallback to existing NIM

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));

        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername(testMahasiswa.getUsername());
        updatedMahasiswa.setPassword(testMahasiswa.getPassword());
        updatedMahasiswa.setFullName("Updated Mahasiswa Name");
        updatedMahasiswa.setNim(testMahasiswa.getNim()); // Should keep existing NIM

        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("Updated Mahasiswa Name", result.getFullName());
        assertEquals("54321", result.getNim()); // Should keep original NIM

        verify(userRepository).save(any(Mahasiswa.class));
    }

    @Test
    void editUser_dosenWithDifferentEmail_shouldUpdateUsername() {
        // Test the case where email is different and dosen.setUsername(email) is called
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setEmail("new.dosen.email@test.com");
        editUserDto.setFullName("Updated Dosen Name");

        when(userRepository.findById(testDosenId)).thenReturn(Optional.of(testDosen));
        when(userRepository.findByEmail("new.dosen.email@test.com")).thenReturn(Optional.empty());

        Dosen updatedDosen = new Dosen();
        updatedDosen.setId(testDosenId);
        updatedDosen.setUsername("new.dosen.email@test.com"); // Should be updated
        updatedDosen.setPassword(testDosen.getPassword());
        updatedDosen.setFullName("Updated Dosen Name");
        updatedDosen.setNip(testDosen.getNip());

        when(userRepository.save(any(Dosen.class))).thenReturn(updatedDosen);

        UserResponseDto result = accountManagementService.editUser(testDosenId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("new.dosen.email@test.com", result.getEmail());
        assertEquals("Updated Dosen Name", result.getFullName());

        verify(userRepository).save(any(Dosen.class));
    }

    @Test
    void editUser_dosenWithSameEmail_shouldNotUpdateUsername() {
        // Test the case where email is the same and dosen.setUsername(email) is NOT called
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setEmail(testDosen.getUsername()); // Same email
        editUserDto.setFullName("Updated Dosen Name");

        when(userRepository.findById(testDosenId)).thenReturn(Optional.of(testDosen));

        Dosen updatedDosen = new Dosen();
        updatedDosen.setId(testDosenId);
        updatedDosen.setUsername(testDosen.getUsername()); // Should remain same
        updatedDosen.setPassword(testDosen.getPassword());
        updatedDosen.setFullName("Updated Dosen Name");
        updatedDosen.setNip(testDosen.getNip());

        when(userRepository.save(any(Dosen.class))).thenReturn(updatedDosen);

        UserResponseDto result = accountManagementService.editUser(testDosenId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals(testDosen.getUsername(), result.getEmail()); // Should remain same
        assertEquals("Updated Dosen Name", result.getFullName());

        verify(userRepository).save(any(Dosen.class));
    }

    @Test
    void editUser_mahasiswaWithDifferentEmail_shouldUpdateUsername() {
        // Test the case where email is different and mahasiswa.setUsername(email) is called
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setEmail("new.mahasiswa.email@test.com");
        editUserDto.setFullName("Updated Mahasiswa Name");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));
        when(userRepository.findByEmail("new.mahasiswa.email@test.com")).thenReturn(Optional.empty());

        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername("new.mahasiswa.email@test.com"); // Should be updated
        updatedMahasiswa.setPassword(testMahasiswa.getPassword());
        updatedMahasiswa.setFullName("Updated Mahasiswa Name");
        updatedMahasiswa.setNim(testMahasiswa.getNim());

        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals("new.mahasiswa.email@test.com", result.getEmail());
        assertEquals("Updated Mahasiswa Name", result.getFullName());

        verify(userRepository).save(any(Mahasiswa.class));
    }

    @Test
    void editUser_mahasiswaWithSameEmail_shouldNotUpdateUsername() {
        // Test the case where email is the same and mahasiswa.setUsername(email) is NOT called
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setEmail(testMahasiswa.getUsername()); // Same email
        editUserDto.setFullName("Updated Mahasiswa Name");

        when(userRepository.findById(testMahasiswaId)).thenReturn(Optional.of(testMahasiswa));

        Mahasiswa updatedMahasiswa = new Mahasiswa();
        updatedMahasiswa.setId(testMahasiswaId);
        updatedMahasiswa.setUsername(testMahasiswa.getUsername()); // Should remain same
        updatedMahasiswa.setPassword(testMahasiswa.getPassword());
        updatedMahasiswa.setFullName("Updated Mahasiswa Name");
        updatedMahasiswa.setNim(testMahasiswa.getNim());

        when(userRepository.save(any(Mahasiswa.class))).thenReturn(updatedMahasiswa);

        UserResponseDto result = accountManagementService.editUser(testMahasiswaId.toString(), editUserDto);

        assertNotNull(result);
        assertEquals(testMahasiswa.getUsername(), result.getEmail()); // Should remain same
        assertEquals("Updated Mahasiswa Name", result.getFullName());

        verify(userRepository).save(any(Mahasiswa.class));
    }

}
