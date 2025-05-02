package id.ac.ui.cs.advprog.hiringgo.authentication.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.dto.LoginUserDto;
import id.ac.ui.cs.advprog.hiringgo.authentication.dto.RegisterUserDto;
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
    }

    @Test
    void register_withValidData_shouldReturnOk() {
        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(authenticatedUser);

        ResponseEntity<?> response = authenticationController.register(validRegisterDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
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
    void verify_withValidToken_shouldReturnUser() {
        String authHeader = "Bearer " + validToken;
        when(authenticationService.verifyToken(validToken)).thenReturn(authenticatedUser);

        ResponseEntity<?> response = authenticationController.verify(authHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticatedUser, response.getBody());
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
}