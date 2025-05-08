package id.ac.ui.cs.advprog.hiringgo.authentication.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.dto.LoginUserDto;
import id.ac.ui.cs.advprog.hiringgo.authentication.dto.RegisterUserDto;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.response.LoginResponse;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.AuthenticationService;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private RegisterUserDto validRegisterDto;
    private LoginUserDto validLoginDto;
    private User authenticatedUser;
    private final String validToken = "valid.jwt.token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validRegisterDto = new RegisterUserDto();
        validRegisterDto.setEmail("test@example.com");
        validRegisterDto.setPassword("password");
        validRegisterDto.setConfirmPassword("password");
        validRegisterDto.setFullName("Test User");
        validRegisterDto.setNim("12345678");

        validLoginDto = new LoginUserDto();
        validLoginDto.setEmail("test@example.com");
        validLoginDto.setPassword("password");

        authenticatedUser = new Mahasiswa();
        authenticatedUser.setUsername("test@example.com");
        ((Mahasiswa) authenticatedUser).setFullName("Test User");
        ((Mahasiswa) authenticatedUser).setNim("12345678");
    }

    @Test
    void register_withValidData_shouldReturnOk() {
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(authenticatedUser);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("test@example.com", responseBody.get("email"));
        assertEquals("Test User", responseBody.get("fullName"));
        assertEquals("12345678", responseBody.get("nim"));
        assertFalse(responseBody.containsKey("password"), "Response should not contain password");
        
        verify(authenticationService, times(1)).signup(validRegisterDto);
    }

    @Test
    void register_withMissingEmail_shouldReturnBadRequest() {
        validRegisterDto.setEmail("");

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is required", response.getBody());
        verify(authenticationService, never()).signup(any(RegisterUserDto.class));
    }

    @Test
    void register_withPasswordMismatch_shouldReturnBadRequest() {
        validRegisterDto.setConfirmPassword("different");

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Passwords do not match", response.getBody());
        verify(authenticationService, never()).signup(any(RegisterUserDto.class));
    }

    @Test
    void register_withServiceException_shouldReturnBadRequest() {
        String expectedErrorMessage = "Email already exists";
        when(authenticationService.signup(any(RegisterUserDto.class)))
                .thenThrow(new IllegalArgumentException(expectedErrorMessage));

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody());
        verify(authenticationService, times(1)).signup(validRegisterDto);
    }

    @Test
    void register_withMissingPassword_shouldReturnBadRequest() {
        validRegisterDto.setPassword("");

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Password is required", response.getBody());
        verify(authenticationService, never()).signup(any(RegisterUserDto.class));
    }

    @Test
    void register_withMissingConfirmPassword_shouldReturnBadRequest() {
        validRegisterDto.setConfirmPassword("");

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Confirm password is required", response.getBody());
        verify(authenticationService, never()).signup(any(RegisterUserDto.class));
    }

    @Test
    void register_withMissingFullName_shouldReturnBadRequest() {
        validRegisterDto.setFullName("");

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Full name is required", response.getBody());
        verify(authenticationService, never()).signup(any(RegisterUserDto.class));
    }

    @Test
    void register_withMissingNim_shouldReturnBadRequest() {
        validRegisterDto.setNim("");

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("NIM is required", response.getBody());
        verify(authenticationService, never()).signup(any(RegisterUserDto.class));
    }

    @Test
    void register_withGenericException_shouldReturnBadRequest() {
        when(authenticationService.signup(any(RegisterUserDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Register failed, please try again.", response.getBody());
        verify(authenticationService, times(1)).signup(validRegisterDto);
    }

    @Test
    void authenticate_withValidCredentials_shouldReturnToken() {
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(authenticatedUser);
        when(jwtService.generateToken(authenticatedUser)).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<?> response = authenticationController.authenticate(validLoginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertEquals(validToken, loginResponse.getToken());
        assertEquals(3600000L, loginResponse.getExpiresIn());
        verify(authenticationService, times(1)).authenticate(validLoginDto);
        verify(jwtService, times(1)).generateToken(authenticatedUser);
    }

    @Test
    void authenticate_withMissingEmail_shouldReturnBadRequest() {
        validLoginDto.setEmail("");

        ResponseEntity<?> response = authenticationController.authenticate(validLoginDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is required", response.getBody());
        verify(authenticationService, never()).authenticate(any(LoginUserDto.class));
    }

    @Test
    void authenticate_withMissingPassword_shouldReturnBadRequest() {
        validLoginDto.setPassword("");

        ResponseEntity<?> response = authenticationController.authenticate(validLoginDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Password is required", response.getBody());
        verify(authenticationService, never()).authenticate(any(LoginUserDto.class));
    }

    @Test
    void authenticate_withInvalidCredentials_shouldReturnUnauthorized() {
        String expectedErrorMessage = "Invalid email or password";
        when(authenticationService.authenticate(any(LoginUserDto.class)))
                .thenThrow(new IllegalArgumentException(expectedErrorMessage));

        ResponseEntity<?> response = authenticationController.authenticate(validLoginDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody());
        verify(authenticationService, times(1)).authenticate(validLoginDto);
    }

    @Test
    void authenticate_withGenericException_shouldReturnForbidden() {
        when(authenticationService.authenticate(any(LoginUserDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = authenticationController.authenticate(validLoginDto);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Authorization failed", response.getBody());
        verify(authenticationService, times(1)).authenticate(validLoginDto);
    }

    @Test
    void verify_withValidToken_shouldReturnUser() {
        String authHeader = "Bearer " + validToken;
        when(authenticationService.verifyToken(validToken)).thenReturn(authenticatedUser);

        ResponseEntity<?> response = authenticationController.verify(authHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("test@example.com", responseBody.get("email"));
        assertEquals("Test User", responseBody.get("fullName"));
        assertEquals("12345678", responseBody.get("nim"));
        assertFalse(responseBody.containsKey("password"), "Response should not contain password");
        
        verify(authenticationService, times(1)).verifyToken(validToken);
    }

    @Test
    void verify_withInvalidToken_shouldReturnUnauthorized() {
        String authHeader = "Bearer " + validToken;
        when(authenticationService.verifyToken(validToken)).thenReturn(null);

        ResponseEntity<?> response = authenticationController.verify(authHeader);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid token", response.getBody());
        verify(authenticationService, times(1)).verifyToken(validToken);
    }

    @Test
    void verify_withMissingToken_shouldReturnBadRequest() {
        String authHeader = null;

        ResponseEntity<?> response = authenticationController.verify(authHeader);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing or invalid Authorization header", response.getBody());
        verify(authenticationService, never()).verifyToken(anyString());
    }

    @Test
    void verify_withInvalidAuthHeader_shouldReturnBadRequest() {
        String authHeader = "InvalidHeader";

        ResponseEntity<?> response = authenticationController.verify(authHeader);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing or invalid Authorization header", response.getBody());
        verify(authenticationService, never()).verifyToken(anyString());
    }

    @Test
    void sanitizeUser_withMahasiswaUser_shouldReturnCorrectMap() {
        Mahasiswa mahasiswaUser = new Mahasiswa();
        mahasiswaUser.setUsername("student@example.com");
        mahasiswaUser.setPassword("hashedPassword");
        mahasiswaUser.setFullName("Student User");
        mahasiswaUser.setNim("12345678");
        
        ResponseEntity<?> response = authenticationController.register(validRegisterDto);
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(mahasiswaUser);
        
        response = authenticationController.register(validRegisterDto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        
        assertEquals("student@example.com", responseBody.get("email"));
        assertEquals("Student User", responseBody.get("fullName"));
        assertEquals("12345678", responseBody.get("nim"));
        assertFalse(responseBody.containsKey("password"));
    }
    
    @Test
    void sanitizeUser_withDosenUser_shouldReturnCorrectMap() {
        Dosen dosenUser = new Dosen();
        dosenUser.setUsername("lecturer@example.com");
        dosenUser.setPassword("hashedPassword");
        dosenUser.setFullName("Lecturer User");
        dosenUser.setNip("87654321");
        
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(dosenUser);
        
        ResponseEntity<?> response = authenticationController.register(validRegisterDto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        
        assertEquals("lecturer@example.com", responseBody.get("email"));
        assertEquals("Lecturer User", responseBody.get("fullName"));
        assertEquals("87654321", responseBody.get("nip"));
        assertFalse(responseBody.containsKey("password"));
    }
    
    @Test
    void sanitizeUser_withAdminUser_shouldReturnCorrectMap() {
        Admin adminUser = new Admin();
        adminUser.setUsername("admin@example.com");
        adminUser.setPassword("hashedPassword");
        
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(adminUser);
        
        ResponseEntity<?> response = authenticationController.register(validRegisterDto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        
        assertEquals("admin@example.com", responseBody.get("email"));
        assertEquals("admin", responseBody.get("type"));
        assertFalse(responseBody.containsKey("password"));
    }

    @Test
    void register_withNullEmail_shouldReturnBadRequest() {
        validRegisterDto.setEmail(null);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is required", response.getBody());
        verify(authenticationService, never()).signup(any(RegisterUserDto.class));
    }

    @Test
    void register_withNullPassword_shouldReturnBadRequest() {
        validRegisterDto.setPassword(null);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Password is required", response.getBody());
        verify(authenticationService, never()).signup(any(RegisterUserDto.class));
    }

    @Test
    void register_withNullConfirmPassword_shouldReturnBadRequest() {
        validRegisterDto.setConfirmPassword(null);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Confirm password is required", response.getBody());
        verify(authenticationService, never()).signup(any(RegisterUserDto.class));
    }

    @Test
    void register_withNullFullName_shouldReturnBadRequest() {
        validRegisterDto.setFullName(null);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Full name is required", response.getBody());
        verify(authenticationService, never()).signup(any(RegisterUserDto.class));
    }

    @Test
    void register_withNullNim_shouldReturnBadRequest() {
        validRegisterDto.setNim(null);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("NIM is required", response.getBody());
        verify(authenticationService, never()).signup(any(RegisterUserDto.class));
    }

    @Test
    void authenticate_withNullEmail_shouldReturnBadRequest() {
        validLoginDto.setEmail(null);

        ResponseEntity<?> response = authenticationController.authenticate(validLoginDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is required", response.getBody());
        verify(authenticationService, never()).authenticate(any(LoginUserDto.class));
    }

    @Test
    void authenticate_withNullPassword_shouldReturnBadRequest() {
        validLoginDto.setPassword(null);

        ResponseEntity<?> response = authenticationController.authenticate(validLoginDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Password is required", response.getBody());
        verify(authenticationService, never()).authenticate(any(LoginUserDto.class));
    }

    @Test
    void sanitizeUser_withNoAuthorities_shouldHandleGracefully() {
        User userWithNoAuthorities = mock(User.class);
        when(userWithNoAuthorities.getUsername()).thenReturn("noauth@example.com");
        when(userWithNoAuthorities.getAuthorities()).thenReturn(Collections.emptyList());
        
        when(authenticationService.verifyToken(validToken)).thenReturn(userWithNoAuthorities);
        
        String authHeader = "Bearer " + validToken;
        ResponseEntity<?> response = authenticationController.verify(authHeader);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        
        assertEquals("noauth@example.com", responseBody.get("email"));
        assertEquals("UNKNOWN", responseBody.get("role"));
    }

    @Test
    void sanitizeUser_withUnknownUserType_shouldHaveBasicInfo() {
        User customUser = new User() {
            @Override
            public List getAuthorities() {
                return Collections.singletonList(new SimpleGrantedAuthority("CUSTOM"));
            }
            
            @Override
            public String getPassword() {
                return "password";
            }
            
            @Override
            public String getUsername() {
                return "custom@example.com";
            }
            
            @Override
            public boolean isAccountNonExpired() {
                return true;
            }
            
            @Override
            public boolean isAccountNonLocked() {
                return true;
            }
            
            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }
            
            @Override
            public boolean isEnabled() {
                return true;
            }
        };
        
        when(authenticationService.verifyToken(validToken)).thenReturn(customUser);
        
        String authHeader = "Bearer " + validToken;
        ResponseEntity<?> response = authenticationController.verify(authHeader);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        
        assertEquals("custom@example.com", responseBody.get("email"));
        assertEquals("CUSTOM", responseBody.get("role"));
        assertFalse(responseBody.containsKey("fullName"));
        assertFalse(responseBody.containsKey("nim"));
        assertFalse(responseBody.containsKey("nip"));
        assertFalse(responseBody.containsKey("type"));
    }
}