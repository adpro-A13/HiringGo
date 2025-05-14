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
    }    @Test
    void register_withValidData_shouldReturnOk() {
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(authenticatedUser);
        when(jwtService.generateToken(anyMap(), eq(authenticatedUser))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof LoginResponse);
        
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertEquals(validToken, loginResponse.getToken());
        assertEquals(3600000L, loginResponse.getExpiresIn());
        
        Map<String, Object> userInfo = loginResponse.getUser();
        assertNotNull(userInfo);
        assertEquals("test@example.com", userInfo.get("email"));
        assertEquals("Test User", userInfo.get("fullName"));
        assertEquals("12345678", userInfo.get("nim"));
        assertFalse(userInfo.containsKey("password"), "Response should not contain password");
        
        verify(authenticationService, times(1)).signup(validRegisterDto);
        verify(jwtService, times(1)).generateToken(anyMap(), eq(authenticatedUser));
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
    }    @Test
    void authenticate_withValidCredentials_shouldReturnToken() {
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(authenticatedUser);
        when(jwtService.generateToken(anyMap(), eq(authenticatedUser))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<?> response = authenticationController.authenticate(validLoginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertEquals(validToken, loginResponse.getToken());
        assertEquals(3600000L, loginResponse.getExpiresIn());
        
        // Check user data in the response
        assertNotNull(loginResponse.getUser());
        assertEquals("test@example.com", loginResponse.getUser().get("email"));
        assertEquals("Test User", loginResponse.getUser().get("fullName"));
        assertEquals("12345678", loginResponse.getUser().get("nim"));
        
        verify(authenticationService, times(1)).authenticate(validLoginDto);
        verify(jwtService, times(1)).generateToken(anyMap(), eq(authenticatedUser));
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
    }    @Test
    void authenticate_withGenericException_shouldReturnForbidden() {
        when(authenticationService.authenticate(any(LoginUserDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = authenticationController.authenticate(validLoginDto);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Authorization failed", response.getBody());
        verify(authenticationService, times(1)).authenticate(validLoginDto);
    }    @Test
    void sanitizeUser_withMahasiswaUser_shouldReturnCorrectMap() {
        Mahasiswa mahasiswaUser = new Mahasiswa();
        mahasiswaUser.setUsername("student@example.com");
        mahasiswaUser.setPassword("hashedPassword");
        mahasiswaUser.setFullName("Student User");
        mahasiswaUser.setNim("12345678");
        
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(mahasiswaUser);
        when(jwtService.generateToken(anyMap(), eq(mahasiswaUser))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);
        
        ResponseEntity<?> response = authenticationController.register(validRegisterDto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        
        Map<String, Object> userInfo = loginResponse.getUser();
        assertNotNull(userInfo);
        assertEquals("student@example.com", userInfo.get("email"));
        assertEquals("Student User", userInfo.get("fullName"));
        assertEquals("12345678", userInfo.get("nim"));
        assertFalse(userInfo.containsKey("password"));
    }
      @Test
    void sanitizeUser_withDosenUser_shouldReturnCorrectMap() {
        Dosen dosenUser = new Dosen();
        dosenUser.setUsername("lecturer@example.com");
        dosenUser.setPassword("hashedPassword");
        dosenUser.setFullName("Lecturer User");
        dosenUser.setNip("87654321");
        
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(dosenUser);
        when(jwtService.generateToken(anyMap(), eq(dosenUser))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);
        
        ResponseEntity<?> response = authenticationController.register(validRegisterDto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertNotNull(loginResponse);
        Map<String, Object> userInfo = loginResponse.getUser();
        assertNotNull(userInfo);
        
        assertEquals("lecturer@example.com", userInfo.get("email"));
        assertEquals("Lecturer User", userInfo.get("fullName"));
        assertEquals("87654321", userInfo.get("nip"));
        assertFalse(userInfo.containsKey("password"));
    }
    
    @Test
    void sanitizeUser_withAdminUser_shouldReturnCorrectMap() {
        Admin adminUser = new Admin();
        adminUser.setUsername("admin@example.com");
        adminUser.setPassword("hashedPassword");        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(adminUser);
        when(jwtService.generateToken(anyMap(), eq(adminUser))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);
        
        ResponseEntity<?> response = authenticationController.register(validRegisterDto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertNotNull(loginResponse);
        Map<String, Object> userInfo = loginResponse.getUser();
        assertNotNull(userInfo);
        
        assertEquals("admin@example.com", userInfo.get("email"));
        assertFalse(userInfo.containsKey("password"));
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
    }    @Test
    void sanitizeUser_withUnknownUserType_shouldHaveBasicInfo() {
        User customUser = new User() {
            @Override
            public List<SimpleGrantedAuthority> getAuthorities() {
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
        };        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(customUser);
        when(jwtService.generateToken(anyMap(), eq(customUser))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);
        
        ResponseEntity<?> response = authenticationController.register(validRegisterDto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertNotNull(loginResponse);
        Map<String, Object> userInfo = loginResponse.getUser();
        assertNotNull(userInfo);
        
        assertEquals("custom@example.com", userInfo.get("email"));
        assertEquals("CUSTOM", userInfo.get("role"));
        assertFalse(userInfo.containsKey("fullName"));
        assertFalse(userInfo.containsKey("nim"));
        assertFalse(userInfo.containsKey("nip"));
    }
}