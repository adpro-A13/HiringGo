package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.controller;

import id.ac.ui.cs.advprog.hiringgo.common.dto.GlobalResponseDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.AdminDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.EditUserDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.DosenDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.UserResponseDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.MahasiswaDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.service.AccountManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AccountManagementControllerTest {

    @Mock
    private AccountManagementService accountManagementService;

    @InjectMocks
    private AccountManagementController accountManagementController;

    private UserResponseDto testUserResponse;
    private List<UserResponseDto> testUsersList;
    private DosenDto testDosenDto;
    private AdminDto testAdminDto;
    private MahasiswaDto testMahasiswaDto;    
    private EditUserDto testEditUserDto;
    private final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174000";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUserResponse = new UserResponseDto();
        testUserResponse.setId(TEST_USER_ID);
        testUserResponse.setEmail("test@example.com");
        testUserResponse.setRole("DOSEN");
        testUserResponse.setFullName("Test User");
        testUserResponse.setNip("12345");

        UserResponseDto adminResponse = new UserResponseDto();
        adminResponse.setId("admin-id");
        adminResponse.setEmail("admin@example.com");
        adminResponse.setRole("ADMIN");

        testUsersList = Arrays.asList(testUserResponse, adminResponse);

        testDosenDto = new DosenDto();
        testDosenDto.setEmail("dosen@example.com");
        testDosenDto.setPassword("password");
        testDosenDto.setFullName("New Dosen");
        testDosenDto.setNip("67890");

        testAdminDto = new AdminDto();
        testAdminDto.setEmail("new.admin@example.com");
        testAdminDto.setPassword("password");

        testMahasiswaDto = new MahasiswaDto();
        testMahasiswaDto.setEmail("mahasiswa@example.com");
        testMahasiswaDto.setPassword("password");        
        testMahasiswaDto.setFullName("New Mahasiswa");
        testMahasiswaDto.setNim("13518000");

        testEditUserDto = new EditUserDto();
        testEditUserDto.setNewRole("ADMIN");
    }    

    @Test
    void getAllUsers_shouldReturnUsersList() {
        when(accountManagementService.getAllUsers()).thenReturn(testUsersList);

        ResponseEntity<GlobalResponseDto<List<UserResponseDto>>> response = accountManagementController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatusCode());
        assertEquals(testUsersList, response.getBody().getData());
        assertEquals("Users retrieved successfully", response.getBody().getMessage());
        verify(accountManagementService).getAllUsers();
    }

    @Test
    void getUserById_withValidId_shouldReturnUser() {
        when(accountManagementService.getUserById(TEST_USER_ID)).thenReturn(testUserResponse);

        ResponseEntity<GlobalResponseDto<UserResponseDto>> response = accountManagementController.getUserById(TEST_USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatusCode());
        assertEquals(testUserResponse, response.getBody().getData());
        assertEquals("User retrieved successfully", response.getBody().getMessage());
        verify(accountManagementService).getUserById(TEST_USER_ID);
    }

    @Test
    void getUserById_withInvalidId_shouldReturnNotFound() {
        when(accountManagementService.getUserById(anyString()))
                .thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity<GlobalResponseDto<UserResponseDto>> response = accountManagementController.getUserById("non-existent-id");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatusCode());
        assertEquals("User not found", response.getBody().getMessage());
        verify(accountManagementService).getUserById("non-existent-id");
    }

    @Test
    void createDosenAccount_withValidData_shouldReturnCreatedUser() {
        when(accountManagementService.createDosenAccount(any(DosenDto.class))).thenReturn(testUserResponse);

        ResponseEntity<GlobalResponseDto<UserResponseDto>> response = accountManagementController.createDosenAccount(testDosenDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(HttpStatus.CREATED.value(), response.getBody().getStatusCode());
        assertEquals(testUserResponse, response.getBody().getData());
        assertEquals("Dosen account created successfully", response.getBody().getMessage());
        verify(accountManagementService).createDosenAccount(testDosenDto);
    }

    @Test
    void createDosenAccount_withInvalidData_shouldReturnBadRequest() {
        String errorMessage = "Email already exists";
        when(accountManagementService.createDosenAccount(any(DosenDto.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<GlobalResponseDto<UserResponseDto>> response = accountManagementController.createDosenAccount(testDosenDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        verify(accountManagementService).createDosenAccount(testDosenDto);
    }

    @Test
    void createAdminAccount_withValidData_shouldReturnCreatedUser() {
        UserResponseDto adminResponse = new UserResponseDto();
        adminResponse.setId("new-admin-id");
        adminResponse.setEmail("new.admin@example.com");
        adminResponse.setRole("ADMIN");
        
        when(accountManagementService.createAdminAccount(any(AdminDto.class))).thenReturn(adminResponse);

        ResponseEntity<GlobalResponseDto<UserResponseDto>> response = accountManagementController.createAdminAccount(testAdminDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(HttpStatus.CREATED.value(), response.getBody().getStatusCode());
        assertEquals(adminResponse, response.getBody().getData());
        assertEquals("Admin account created successfully", response.getBody().getMessage());
        verify(accountManagementService).createAdminAccount(testAdminDto);
    }

    @Test
    void createAdminAccount_withInvalidData_shouldReturnBadRequest() {
        String errorMessage = "Email already exists";
        when(accountManagementService.createAdminAccount(any(AdminDto.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<GlobalResponseDto<UserResponseDto>> response = accountManagementController.createAdminAccount(testAdminDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        verify(accountManagementService).createAdminAccount(testAdminDto);
    }

    @Test
    void createMahasiswaAccount_withValidData_shouldReturnCreatedUser() {
        when(accountManagementService.createMahasiswaAccount(any(MahasiswaDto.class))).thenReturn(testUserResponse);

        ResponseEntity<GlobalResponseDto<UserResponseDto>> response = accountManagementController.createMahasiswaAccount(testMahasiswaDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(HttpStatus.CREATED.value(), response.getBody().getStatusCode());
        assertEquals(testUserResponse, response.getBody().getData());
        assertEquals("Mahasiswa account created successfully", response.getBody().getMessage());
        verify(accountManagementService).createMahasiswaAccount(testMahasiswaDto);
    }

    @Test
    void createMahasiswaAccount_withInvalidData_shouldReturnBadRequest() {
        String errorMessage = "Email already exists";
        when(accountManagementService.createMahasiswaAccount(any(MahasiswaDto.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<GlobalResponseDto<UserResponseDto>> response = accountManagementController.createMahasiswaAccount(testMahasiswaDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        verify(accountManagementService).createMahasiswaAccount(testMahasiswaDto);
    }    

    @Test
    void editUser_withValidData_shouldReturnUpdatedUser() {
        UserResponseDto updatedUser = new UserResponseDto();
        updatedUser.setId(TEST_USER_ID);
        updatedUser.setEmail("test@example.com");
        updatedUser.setRole("ADMIN");
        
        when(accountManagementService.editUser(TEST_USER_ID, testEditUserDto)).thenReturn(updatedUser);

        ResponseEntity<GlobalResponseDto<UserResponseDto>> response = accountManagementController.editUser(TEST_USER_ID, testEditUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatusCode());
        assertEquals(updatedUser, response.getBody().getData());
        assertEquals("User updated successfully", response.getBody().getMessage());
        verify(accountManagementService).editUser(TEST_USER_ID, testEditUserDto);
    }

    @Test
    void editUser_withInvalidData_shouldReturnBadRequest() {
        String errorMessage = "Invalid role";
        when(accountManagementService.editUser(anyString(), any(EditUserDto.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<GlobalResponseDto<UserResponseDto>> response = accountManagementController.editUser(TEST_USER_ID, testEditUserDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        verify(accountManagementService).editUser(TEST_USER_ID, testEditUserDto);
    }    @Test
    void deleteUser_withValidId_shouldReturnNoContent() {
        doNothing().when(accountManagementService).deleteUser(TEST_USER_ID);

        ResponseEntity<GlobalResponseDto<Void>> response = accountManagementController.deleteUser(TEST_USER_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getBody().getStatusCode());
        assertEquals("User deleted successfully", response.getBody().getMessage());
        verify(accountManagementService).deleteUser(TEST_USER_ID);
    }

    @Test
    void deleteUser_withInvalidId_shouldReturnBadRequest() {
        String errorMessage = "User not found";
        doThrow(new IllegalArgumentException(errorMessage))
                .when(accountManagementService).deleteUser(anyString());

        ResponseEntity<GlobalResponseDto<Void>> response = accountManagementController.deleteUser("invalid-id");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        verify(accountManagementService).deleteUser("invalid-id");
    }
}