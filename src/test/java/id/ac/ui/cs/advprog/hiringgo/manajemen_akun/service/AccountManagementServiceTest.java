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

}
