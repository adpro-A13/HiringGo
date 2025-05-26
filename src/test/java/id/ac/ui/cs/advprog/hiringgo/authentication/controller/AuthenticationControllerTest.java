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
import java.util.UUID;
import java.util.Arrays;
import org.springframework.mock.web.MockHttpServletResponse;
import jakarta.servlet.http.Cookie;
import static org.mockito.ArgumentMatchers.argThat;

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

    // Add these additional tests to AuthenticationControllerTest.java to achieve 100% coverage

    @Test
    void sanitizeUser_withMahasiswaUser_shouldIncludeAllMahasiswaFields() {
        // Test all branches of the sanitizeUser method for Mahasiswa
        Mahasiswa mahasiswaUser = new Mahasiswa();
        mahasiswaUser.setId(UUID.randomUUID());
        mahasiswaUser.setUsername("complete.student@example.com");
        mahasiswaUser.setPassword("hashedPassword");
        mahasiswaUser.setFullName("Complete Student User");
        mahasiswaUser.setNim("20231234567");

        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(mahasiswaUser);
        when(jwtService.generateToken(anyMap(), eq(mahasiswaUser))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();

        Map<String, Object> userInfo = loginResponse.getUser();
        assertNotNull(userInfo);
        assertEquals(mahasiswaUser.getId(), userInfo.get("id"));
        assertEquals("complete.student@example.com", userInfo.get("email"));
        assertEquals("Complete Student User", userInfo.get("fullName"));
        assertEquals("20231234567", userInfo.get("nim"));
        assertEquals("MAHASISWA", userInfo.get("role"));
        assertFalse(userInfo.containsKey("password"));
        assertFalse(userInfo.containsKey("nip")); // Should not contain Dosen-specific field
    }

    @Test
    void sanitizeUser_withDosenUser_shouldIncludeAllDosenFields() {
        // Test all branches of the sanitizeUser method for Dosen
        Dosen dosenUser = new Dosen();
        dosenUser.setId(UUID.randomUUID());
        dosenUser.setUsername("complete.lecturer@example.com");
        dosenUser.setPassword("hashedPassword");
        dosenUser.setFullName("Complete Lecturer User");
        dosenUser.setNip("198701012345678901");

        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(dosenUser);
        when(jwtService.generateToken(anyMap(), eq(dosenUser))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();

        Map<String, Object> userInfo = loginResponse.getUser();
        assertNotNull(userInfo);
        assertEquals(dosenUser.getId(), userInfo.get("id"));
        assertEquals("complete.lecturer@example.com", userInfo.get("email"));
        assertEquals("Complete Lecturer User", userInfo.get("fullName"));
        assertEquals("198701012345678901", userInfo.get("nip"));
        assertEquals("DOSEN", userInfo.get("role"));
        assertFalse(userInfo.containsKey("password"));
        assertFalse(userInfo.containsKey("nim")); // Should not contain Mahasiswa-specific field
    }

    @Test
    void sanitizeUser_withAdminUser_shouldIncludeBasicAdminFields() {
        // Test the Admin branch of sanitizeUser method
        Admin adminUser = new Admin();
        adminUser.setId(UUID.randomUUID());
        adminUser.setUsername("complete.admin@example.com");
        adminUser.setPassword("hashedPassword");

        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(adminUser);
        when(jwtService.generateToken(anyMap(), eq(adminUser))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();

        Map<String, Object> userInfo = loginResponse.getUser();
        assertNotNull(userInfo);
        assertEquals(adminUser.getId(), userInfo.get("id"));
        assertEquals("complete.admin@example.com", userInfo.get("email"));
        assertEquals("ADMIN", userInfo.get("role"));
        assertFalse(userInfo.containsKey("password"));
        assertFalse(userInfo.containsKey("fullName")); // Admin doesn't have fullName in sanitizeUser
        assertFalse(userInfo.containsKey("nim"));
        assertFalse(userInfo.containsKey("nip"));
    }

    @Test
    void sanitizeUser_withUserHavingNoAuthorities_shouldReturnUnknownRole() {
        // Test the case where user has no authorities (orElse("UNKNOWN") branch)
        User userWithNoAuthorities = new User() {
            @Override
            public List<SimpleGrantedAuthority> getAuthorities() {
                return Collections.emptyList(); // No authorities
            }

            @Override
            public String getPassword() {
                return "password";
            }

            @Override
            public String getUsername() {
                return "noauth@example.com";
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
        userWithNoAuthorities.setId(UUID.randomUUID());

        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(userWithNoAuthorities);
        when(jwtService.generateToken(anyMap(), eq(userWithNoAuthorities))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();

        Map<String, Object> userInfo = loginResponse.getUser();
        assertNotNull(userInfo);
        assertEquals("UNKNOWN", userInfo.get("role")); // Should default to UNKNOWN
        assertEquals("noauth@example.com", userInfo.get("email"));
        assertFalse(userInfo.containsKey("password"));
    }

    @Test
    void logout_shouldClearCookiesAndReturnSuccessResponse() {
        // Test the logout endpoint
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<Map<String, Object>> result = authenticationController.logout(response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());

        Map<String, Object> responseBody = result.getBody();
        assertTrue(responseBody.containsKey("data"));

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertEquals(200, data.get("status_code"));
        assertEquals("Anda berhasil logout!!", data.get("message"));

        // Verify cookies were cleared
        Cookie[] cookies = response.getCookies();
        assertNotNull(cookies);
        assertEquals(2, cookies.length);

        // Check authToken cookie
        Cookie authTokenCookie = Arrays.stream(cookies)
                .filter(cookie -> "authToken".equals(cookie.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(authTokenCookie);
        assertNull(authTokenCookie.getValue());
        assertEquals("/", authTokenCookie.getPath());
        assertEquals(0, authTokenCookie.getMaxAge());

        // Check token cookie
        Cookie tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> "token".equals(cookie.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(tokenCookie);
        assertNull(tokenCookie.getValue());
        assertEquals("/", tokenCookie.getPath());
        assertEquals(0, tokenCookie.getMaxAge());
    }

    @Test
    void register_withValidMahasiswaData_shouldCreateJwtWithCorrectClaims() {
        // Test JWT generation with specific user data
        Mahasiswa mahasiswaUser = new Mahasiswa();
        mahasiswaUser.setId(UUID.randomUUID());
        mahasiswaUser.setUsername("jwt.test@example.com");
        mahasiswaUser.setFullName("JWT Test User");
        mahasiswaUser.setNim("20230001");

        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(mahasiswaUser);
        when(jwtService.generateToken(anyMap(), eq(mahasiswaUser))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(7200000L);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertNotNull(loginResponse);
        assertEquals(validToken, loginResponse.getToken());
        assertEquals(7200000L, loginResponse.getExpiresIn());

        // Verify JWT service was called with correct user info
        verify(jwtService).generateToken(argThat(userInfo -> {
            Map<String, Object> map = (Map<String, Object>) userInfo;
            return "jwt.test@example.com".equals(map.get("email")) &&
                    "JWT Test User".equals(map.get("fullName")) &&
                    "20230001".equals(map.get("nim")) &&
                    "MAHASISWA".equals(map.get("role"));
        }), eq(mahasiswaUser));
    }

    @Test
    void authenticate_withValidDosenCredentials_shouldReturnTokenWithDosenInfo() {
        // Test authentication with Dosen user
        Dosen authenticatedDosen = new Dosen();
        authenticatedDosen.setId(UUID.randomUUID());
        authenticatedDosen.setUsername("auth.dosen@example.com");
        authenticatedDosen.setFullName("Authenticated Dosen");
        authenticatedDosen.setNip("199001011234567890");

        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(authenticatedDosen);
        when(jwtService.generateToken(anyMap(), eq(authenticatedDosen))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<?> response = authenticationController.authenticate(validLoginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertNotNull(loginResponse);

        Map<String, Object> userInfo = loginResponse.getUser();
        assertEquals("auth.dosen@example.com", userInfo.get("email"));
        assertEquals("Authenticated Dosen", userInfo.get("fullName"));
        assertEquals("199001011234567890", userInfo.get("nip"));
        assertEquals("DOSEN", userInfo.get("role"));
        assertFalse(userInfo.containsKey("nim"));
    }

    @Test
    void authenticate_withValidAdminCredentials_shouldReturnTokenWithAdminInfo() {
        // Test authentication with Admin user
        Admin authenticatedAdmin = new Admin();
        authenticatedAdmin.setId(UUID.randomUUID());
        authenticatedAdmin.setUsername("auth.admin@example.com");

        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(authenticatedAdmin);
        when(jwtService.generateToken(anyMap(), eq(authenticatedAdmin))).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<?> response = authenticationController.authenticate(validLoginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertNotNull(loginResponse);

        Map<String, Object> userInfo = loginResponse.getUser();
        assertEquals("auth.admin@example.com", userInfo.get("email"));
        assertEquals("ADMIN", userInfo.get("role"));
        assertFalse(userInfo.containsKey("fullName"));
        assertFalse(userInfo.containsKey("nim"));
        assertFalse(userInfo.containsKey("nip"));
    }

    @Test
    void sanitizeUser_coverageForAllUserTypeBranches() {
        // Comprehensive test to ensure all instanceof branches are covered

        // Test 1: Mahasiswa instance
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setId(UUID.randomUUID());
        mahasiswa.setUsername("m@test.com");
        mahasiswa.setFullName("M User");
        mahasiswa.setNim("123");

        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(mahasiswa);
        when(jwtService.generateToken(anyMap(), any())).thenReturn(validToken);
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<?> response1 = authenticationController.register(validRegisterDto);
        assertEquals(HttpStatus.OK, response1.getStatusCode());

        // Test 2: Dosen instance
        Dosen dosen = new Dosen();
        dosen.setId(UUID.randomUUID());
        dosen.setUsername("d@test.com");
        dosen.setFullName("D User");
        dosen.setNip("456");

        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(dosen);

        ResponseEntity<?> response2 = authenticationController.register(validRegisterDto);
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        // Test 3: Admin instance (neither Mahasiswa nor Dosen)
        Admin admin = new Admin();
        admin.setId(UUID.randomUUID());
        admin.setUsername("a@test.com");

        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(admin);

        ResponseEntity<?> response3 = authenticationController.register(validRegisterDto);
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        // Verify all user types were processed correctly
        verify(jwtService, times(3)).generateToken(anyMap(), any(User.class));
    }
}