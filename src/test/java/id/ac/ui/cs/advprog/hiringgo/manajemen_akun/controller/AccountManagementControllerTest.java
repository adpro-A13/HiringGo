package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.controller;

import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.AdminDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.ChangeRoleDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.DosenDto;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.UserResponseDto;
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
    private ChangeRoleDto testChangeRoleDto;
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

        testChangeRoleDto = new ChangeRoleDto();
        testChangeRoleDto.setNewRole("ADMIN");
    }

    @Test
    void getAllUsers_shouldReturnUsersList() {
        when(accountManagementService.getAllUsers()).thenReturn(testUsersList);

        ResponseEntity<List<UserResponseDto>> response = accountManagementController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUsersList, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(accountManagementService).getAllUsers();
    }

    @Test
    void getUserById_withValidId_shouldReturnUser() {
        when(accountManagementService.getUserById(TEST_USER_ID)).thenReturn(testUserResponse);

        ResponseEntity<?> response = accountManagementController.getUserById(TEST_USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserResponse, response.getBody());
        verify(accountManagementService).getUserById(TEST_USER_ID);
    }

    @Test
    void getUserById_withInvalidId_shouldReturnNotFound() {
        when(accountManagementService.getUserById(anyString()))
                .thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity<?> response = accountManagementController.getUserById("non-existent-id");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(accountManagementService).getUserById("non-existent-id");
    }

    @Test
    void createDosenAccount_withValidData_shouldReturnCreatedUser() {
        when(accountManagementService.createDosenAccount(any(DosenDto.class))).thenReturn(testUserResponse);

        ResponseEntity<?> response = accountManagementController.createDosenAccount(testDosenDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testUserResponse, response.getBody());
        verify(accountManagementService).createDosenAccount(testDosenDto);
    }

    @Test
    void createDosenAccount_withInvalidData_shouldReturnBadRequest() {
        String errorMessage = "Email already exists";
        when(accountManagementService.createDosenAccount(any(DosenDto.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<?> response = accountManagementController.createDosenAccount(testDosenDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(accountManagementService).createDosenAccount(testDosenDto);
    }

    @Test
    void createAdminAccount_withValidData_shouldReturnCreatedUser() {
        UserResponseDto adminResponse = new UserResponseDto();
        adminResponse.setId("new-admin-id");
        adminResponse.setEmail("new.admin@example.com");
        adminResponse.setRole("ADMIN");
        
        when(accountManagementService.createAdminAccount(any(AdminDto.class))).thenReturn(adminResponse);

        ResponseEntity<?> response = accountManagementController.createAdminAccount(testAdminDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(adminResponse, response.getBody());
        verify(accountManagementService).createAdminAccount(testAdminDto);
    }

    @Test
    void createAdminAccount_withInvalidData_shouldReturnBadRequest() {
        String errorMessage = "Email already exists";
        when(accountManagementService.createAdminAccount(any(AdminDto.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<?> response = accountManagementController.createAdminAccount(testAdminDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(accountManagementService).createAdminAccount(testAdminDto);
    }

    @Test
    void changeUserRole_withValidData_shouldReturnUpdatedUser() {
        UserResponseDto updatedUser = new UserResponseDto();
        updatedUser.setId(TEST_USER_ID);
        updatedUser.setEmail("test@example.com");
        updatedUser.setRole("ADMIN");
        
        when(accountManagementService.changeUserRole(TEST_USER_ID, testChangeRoleDto)).thenReturn(updatedUser);

        ResponseEntity<?> response = accountManagementController.changeUserRole(TEST_USER_ID, testChangeRoleDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
        assertEquals("ADMIN", ((UserResponseDto) response.getBody()).getRole());
        verify(accountManagementService).changeUserRole(TEST_USER_ID, testChangeRoleDto);
    }

    @Test
    void changeUserRole_withInvalidData_shouldReturnBadRequest() {
        String errorMessage = "Invalid role";
        when(accountManagementService.changeUserRole(anyString(), any(ChangeRoleDto.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<?> response = accountManagementController.changeUserRole(TEST_USER_ID, testChangeRoleDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(accountManagementService).changeUserRole(TEST_USER_ID, testChangeRoleDto);
    }

    @Test
    void deleteUser_withValidId_shouldReturnNoContent() {
        doNothing().when(accountManagementService).deleteUser(TEST_USER_ID);

        ResponseEntity<?> response = accountManagementController.deleteUser(TEST_USER_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(accountManagementService).deleteUser(TEST_USER_ID);
    }

    @Test
    void deleteUser_withInvalidId_shouldReturnBadRequest() {
        String errorMessage = "User not found";
        doThrow(new IllegalArgumentException(errorMessage))
                .when(accountManagementService).deleteUser(anyString());

        ResponseEntity<?> response = accountManagementController.deleteUser("invalid-id");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
        verify(accountManagementService).deleteUser("invalid-id");
    }
}