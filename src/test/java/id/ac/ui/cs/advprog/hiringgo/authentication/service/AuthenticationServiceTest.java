package id.ac.ui.cs.advprog.hiringgo.authentication.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.Enums.UserRoleEnums;
import id.ac.ui.cs.advprog.hiringgo.authentication.dto.LoginUserDto;
import id.ac.ui.cs.advprog.hiringgo.authentication.dto.RegisterUserDto;
import id.ac.ui.cs.advprog.hiringgo.authentication.factory.UserFactory;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterUserDto validRegisterDto;
    private LoginUserDto validLoginDto;
    private User mockUser;
    private final String rawPassword = "password";
    private final String encodedPassword = "encodedPassword";
    private final String userEmail = "test@example.com";    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new Mahasiswa();
        mockUser.setUsername(userEmail);
        mockUser.setPassword(encodedPassword);

        validRegisterDto = new RegisterUserDto();
        validRegisterDto.setEmail(userEmail);
        validRegisterDto.setPassword(rawPassword);
        validRegisterDto.setConfirmPassword(rawPassword);
        validRegisterDto.setFullName("Test User");
        validRegisterDto.setNim("12345678");

        validLoginDto = new LoginUserDto();
        validLoginDto.setEmail(userEmail);
        validLoginDto.setPassword(rawPassword);

        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
    }

    @Test
    void signup_withValidInput_shouldCreateAndReturnUser() {
        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            mockedFactory.when(() -> UserFactory.createUser(
                            eq(UserRoleEnums.MAHASISWA),
                            eq(userEmail),
                            eq(encodedPassword),
                            eq("Test User"),
                            eq("12345678")))
                    .thenReturn(mockUser);

            User result = authenticationService.signup(validRegisterDto);

            assertNotNull(result);
            assertEquals(mockUser, result);
            verify(userRepository).save(mockUser);
            verify(passwordEncoder).encode(rawPassword);
        }
    }

    @Test
    void signup_withPasswordMismatch_shouldThrowException() {
        validRegisterDto.setConfirmPassword("differentPassword");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.signup(validRegisterDto);
        });

        assertEquals("Password and confirm password do not match", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signup_withNullEmail_shouldThrowException() {
        validRegisterDto.setEmail(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authenticationService.signup(validRegisterDto));
        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    void signup_withNullPassword_shouldThrowException() {
        validRegisterDto.setPassword(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authenticationService.signup(validRegisterDto));
        assertEquals("Password is required", exception.getMessage());
    }

    @Test
    void signup_withNullFullName_shouldThrowException() {
        validRegisterDto.setFullName(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authenticationService.signup(validRegisterDto));
        assertEquals("Full name is required", exception.getMessage());
    }

    @Test
    void signup_withNullNim_shouldThrowException() {
        validRegisterDto.setNim(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authenticationService.signup(validRegisterDto));
        assertEquals("NIM is required", exception.getMessage());
    }

    @Test
    void signup_withExistingEmail_shouldThrowException() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            mockedFactory.when(() -> UserFactory.createUser(any(), any(), any(), any(), any())).thenReturn(mockUser);
            Exception exception = assertThrows(IllegalArgumentException.class, () -> authenticationService.signup(validRegisterDto));
            assertEquals("User already exists", exception.getMessage());
        }
    }

    @Test
    void authenticate_withValidCredentials_shouldReturnUser() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        User result = authenticationService.authenticate(validLoginDto);

        assertNotNull(result);
        assertEquals(mockUser, result);
        verify(userRepository).findByEmail(userEmail);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void authenticate_withInvalidEmail_shouldThrowException() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.authenticate(validLoginDto);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail(userEmail);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticate_withInvalidPassword_shouldThrowException() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.authenticate(validLoginDto);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail(userEmail);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void authenticate_withNullEmail_shouldThrowException() {
        validLoginDto.setEmail(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authenticationService.authenticate(validLoginDto));
        assertEquals("Email is required", exception.getMessage());
    }    @Test
    void authenticate_withNullPassword_shouldThrowException() {
        validLoginDto.setPassword(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authenticationService.authenticate(validLoginDto));
        assertEquals("Password is required", exception.getMessage());
    }
}