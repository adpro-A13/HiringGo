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

    @Test
    void signup_withEmptyEmail_shouldThrowException() {
        validRegisterDto.setEmail(""); // Empty string instead of null
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.signup(validRegisterDto));
        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    void signup_withEmptyPassword_shouldThrowException() {
        validRegisterDto.setPassword(""); // Empty string instead of null
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.signup(validRegisterDto));
        assertEquals("Password is required", exception.getMessage());
    }

    @Test
    void signup_withEmptyFullName_shouldThrowException() {
        validRegisterDto.setFullName(""); // Empty string instead of null
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.signup(validRegisterDto));
        assertEquals("Full name is required", exception.getMessage());
    }

    @Test
    void signup_withEmptyNim_shouldThrowException() {
        validRegisterDto.setNim(""); // Empty string instead of null
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.signup(validRegisterDto));
        assertEquals("NIM is required", exception.getMessage());
    }

    @Test
    void authenticate_withEmptyEmail_shouldThrowException() {
        validLoginDto.setEmail(""); // Empty string instead of null
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.authenticate(validLoginDto));
        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    void authenticate_withEmptyPassword_shouldThrowException() {
        validLoginDto.setPassword(""); // Empty string instead of null
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.authenticate(validLoginDto));
        assertEquals("Password is required", exception.getMessage());
    }

    @Test
    void signup_withDatabaseExceptionContainingNim_shouldThrowNimAlreadyExistsException() {
        // Test the first branch of the catch block: e.getMessage().contains("nim")
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            mockedFactory.when(() -> UserFactory.createUser(any(), any(), any(), any(), any()))
                    .thenReturn(mockUser);

            // Mock repository.save to throw exception with "nim" in message
            when(userRepository.save(any(User.class)))
                    .thenThrow(new RuntimeException("Duplicate entry for nim constraint"));

            Exception exception = assertThrows(IllegalArgumentException.class, () ->
                    authenticationService.signup(validRegisterDto));
            assertEquals("NIM already exists", exception.getMessage());

            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void signup_withDatabaseExceptionContainingNip_shouldThrowNipAlreadyExistsException() {
        // Test the second branch of the catch block: e.getMessage().contains("nip")
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            mockedFactory.when(() -> UserFactory.createUser(any(), any(), any(), any(), any()))
                    .thenReturn(mockUser);

            // Mock repository.save to throw exception with "nip" in message
            when(userRepository.save(any(User.class)))
                    .thenThrow(new RuntimeException("Duplicate entry for nip constraint"));

            Exception exception = assertThrows(IllegalArgumentException.class, () ->
                    authenticationService.signup(validRegisterDto));
            assertEquals("NIP already exists", exception.getMessage());

            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void signup_withDatabaseExceptionWithoutNimOrNip_shouldThrowOriginalException() {
        // Test the else branch: exception doesn't contain "nim" or "nip"
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            mockedFactory.when(() -> UserFactory.createUser(any(), any(), any(), any(), any()))
                    .thenReturn(mockUser);

            RuntimeException originalException = new RuntimeException("Database connection error");
            when(userRepository.save(any(User.class))).thenThrow(originalException);

            Exception exception = assertThrows(RuntimeException.class, () ->
                    authenticationService.signup(validRegisterDto));
            assertEquals("Database connection error", exception.getMessage());
            assertEquals(originalException, exception);

            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void signup_withDatabaseExceptionWithNullMessage_shouldThrowOriginalException() {
        // Test the case where e.getMessage() is null
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            mockedFactory.when(() -> UserFactory.createUser(any(), any(), any(), any(), any()))
                    .thenReturn(mockUser);

            // Create exception with null message
            RuntimeException nullMessageException = new RuntimeException((String) null);
            when(userRepository.save(any(User.class))).thenThrow(nullMessageException);

            Exception exception = assertThrows(RuntimeException.class, () ->
                    authenticationService.signup(validRegisterDto));
            assertNull(exception.getMessage());
            assertEquals(nullMessageException, exception);

            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void signup_withExceptionMessageContainingNimButNotExactMatch_shouldCheckBothConditions() {
        // Test to ensure both e.getMessage() != null AND e.getMessage().contains("nim") are checked
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            mockedFactory.when(() -> UserFactory.createUser(any(), any(), any(), any(), any()))
                    .thenReturn(mockUser);

            // Exception message contains "nim" - should hit first if condition
            when(userRepository.save(any(User.class)))
                    .thenThrow(new RuntimeException("Error: nim value already exists in database"));

            Exception exception = assertThrows(IllegalArgumentException.class, () ->
                    authenticationService.signup(validRegisterDto));
            assertEquals("NIM already exists", exception.getMessage());
        }
    }

    @Test
    void signup_withExceptionMessageContainingNipButNotNim_shouldCheckSecondCondition() {
        // Test to ensure the else if condition for "nip" is properly tested
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            mockedFactory.when(() -> UserFactory.createUser(any(), any(), any(), any(), any()))
                    .thenReturn(mockUser);

            // Exception message contains "nip" but not "nim" - should hit second if condition
            when(userRepository.save(any(User.class)))
                    .thenThrow(new RuntimeException("Error: nip constraint violation"));

            Exception exception = assertThrows(IllegalArgumentException.class, () ->
                    authenticationService.signup(validRegisterDto));
            assertEquals("NIP already exists", exception.getMessage());
        }
    }

    @Test
    void signup_withExceptionMessageContainingBothNimAndNip_shouldPrioritizeNim() {
        // Test precedence when message contains both "nim" and "nip"
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            mockedFactory.when(() -> UserFactory.createUser(any(), any(), any(), any(), any()))
                    .thenReturn(mockUser);


            when(userRepository.save(any(User.class)))
                    .thenThrow(new RuntimeException("Error: nim and nip constraints violated"));

            Exception exception = assertThrows(IllegalArgumentException.class, () ->
                    authenticationService.signup(validRegisterDto));
            assertEquals("NIM already exists", exception.getMessage());
        }
    }
}