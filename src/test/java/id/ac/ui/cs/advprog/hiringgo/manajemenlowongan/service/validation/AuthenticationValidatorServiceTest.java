package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationValidatorServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private AuthenticationValidatorService authenticationValidatorService;

    private Mahasiswa testMahasiswa;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testEmail = "mahasiswa@test.com";

        testMahasiswa = new Mahasiswa();
        testMahasiswa.setId(UUID.randomUUID());
        testMahasiswa.setUsername(testEmail);
        testMahasiswa.setFullName("Test Mahasiswa");
    }

    @Test
    void testValidateAndGetCurrentUserSuccess() {
        // Arrange
        when(principal.getName()).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testMahasiswa));

        // Act
        Mahasiswa result = authenticationValidatorService.validateAndGetCurrentUser(principal);

        // Assert
        assertNotNull(result);
        assertEquals(testMahasiswa, result);
        assertEquals(testEmail, result.getUsername());
        assertEquals("Test Mahasiswa", result.getFullName());

        verify(principal).getName();
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void testValidateAndGetCurrentUserWithNullPrincipal() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationValidatorService.validateAndGetCurrentUser(null);
        });

        assertEquals("Anda harus login terlebih dahulu", exception.getMessage());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void testValidateAndGetCurrentUserWithUserNotFound() {
        // Arrange
        when(principal.getName()).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationValidatorService.validateAndGetCurrentUser(principal);
        });

        assertEquals("User tidak ditemukan: " + testEmail, exception.getMessage());
        verify(principal).getName();
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void testValidateAuthenticationWithNullPrincipal() {
        // This tests the private validateAuthentication method through the public method
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationValidatorService.validateAndGetCurrentUser(null);
        });

        assertEquals("Anda harus login terlebih dahulu", exception.getMessage());
    }

    @Test
    void testGetCurrentMahasiswaWithValidEmail() {
        // Arrange
        when(principal.getName()).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testMahasiswa));

        // Act
        Mahasiswa result = authenticationValidatorService.validateAndGetCurrentUser(principal);

        // Assert
        assertNotNull(result);
        assertEquals(testMahasiswa.getId(), result.getId());
        assertEquals(testMahasiswa.getUsername(), result.getUsername());
    }

    @Test
    void testGetCurrentMahasiswaWithInvalidEmail() {
        // Arrange
        String invalidEmail = "invalid@test.com";
        when(principal.getName()).thenReturn(invalidEmail);
        when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationValidatorService.validateAndGetCurrentUser(principal);
        });

        assertEquals("User tidak ditemukan: " + invalidEmail, exception.getMessage());
    }

    @Test
    void testValidateAndGetCurrentUserWithEmptyEmail() {
        // Arrange
        String emptyEmail = "";
        when(principal.getName()).thenReturn(emptyEmail);
        when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationValidatorService.validateAndGetCurrentUser(principal);
        });

        assertEquals("User tidak ditemukan: " + emptyEmail, exception.getMessage());
    }

    @Test
    void testValidateAndGetCurrentUserWithNullEmailFromPrincipal() {
        // Arrange
        when(principal.getName()).thenReturn(null);
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationValidatorService.validateAndGetCurrentUser(principal);
        });

        assertEquals("User tidak ditemukan: null", exception.getMessage());
    }

    @Test
    void testValidateAndGetCurrentUserVerifyRepositoryInteraction() {
        // Arrange
        when(principal.getName()).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testMahasiswa));

        // Act
        authenticationValidatorService.validateAndGetCurrentUser(principal);

        // Assert - Verify exact number of interactions
        verify(principal, times(1)).getName();
        verify(userRepository, times(1)).findByEmail(testEmail);
        verifyNoMoreInteractions(principal, userRepository);
    }

    @Test
    void testValidateAndGetCurrentUserMultipleCalls() {
        // Arrange
        when(principal.getName()).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testMahasiswa));

        // Act - Call multiple times
        Mahasiswa result1 = authenticationValidatorService.validateAndGetCurrentUser(principal);
        Mahasiswa result2 = authenticationValidatorService.validateAndGetCurrentUser(principal);

        // Assert - Both calls should return the same user
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(testMahasiswa, result1);
        assertEquals(testMahasiswa, result2);
        assertEquals(result1.getId(), result2.getId());

        // Verify repository is called for each validation
        verify(principal, times(2)).getName();
        verify(userRepository, times(2)).findByEmail(testEmail);
    }

    @Test
    void testValidateAndGetCurrentUserReturnTypeIsCorrect() {
        // Arrange
        when(principal.getName()).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testMahasiswa));

        // Act
        Mahasiswa result = authenticationValidatorService.validateAndGetCurrentUser(principal);

        // Assert - Verify the returned object is indeed a Mahasiswa instance
        assertNotNull(result);
        assertInstanceOf(Mahasiswa.class, result);
        assertTrue(result instanceof Mahasiswa);

        verify(principal).getName();
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void testValidateAndGetCurrentUserWithRepositoryException() {
        // Arrange
        when(principal.getName()).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationValidatorService.validateAndGetCurrentUser(principal);
        });

        assertEquals("Database error", exception.getMessage());
        verify(principal).getName();
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void testConstructorDependencyInjection() {
        // This test verifies that the service can be constructed with dependencies
        // The @InjectMocks annotation already tests this, but we can add explicit verification

        // Act - Create service manually to test constructor
        AuthenticationValidatorService service = new AuthenticationValidatorService(userRepository);

        // Assert - Service should be created successfully
        assertNotNull(service);
    }

    @Test
    void testServiceAnnotationPresence() {
        // Verify that the class has @Service annotation (this is more of a structural test)
        boolean hasServiceAnnotation = AuthenticationValidatorService.class
                .isAnnotationPresent(org.springframework.stereotype.Service.class);

        assertTrue(hasServiceAnnotation, "AuthenticationValidatorService should have @Service annotation");
    }

    @Test
    void testValidateAndGetCurrentUserWithDifferentUserTypes() {
        // Test edge case where UserRepository might return different user types
        // This is more of a defensive programming test

        // Arrange
        when(principal.getName()).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testMahasiswa));

        // Act
        Mahasiswa result = authenticationValidatorService.validateAndGetCurrentUser(principal);

        // Assert - Ensure we get specifically a Mahasiswa, not just any User
        assertNotNull(result);
        assertEquals(Mahasiswa.class, result.getClass());
        assertEquals(testMahasiswa.getUsername(), result.getUsername());
    }

    @Test
    void testValidateAndGetCurrentUserErrorMessageFormat() {
        // Test that error messages are properly formatted

        // Test case 1: Null principal
        IllegalArgumentException nullPrincipalException = assertThrows(IllegalArgumentException.class, () -> {
            authenticationValidatorService.validateAndGetCurrentUser(null);
        });
        assertTrue(nullPrincipalException.getMessage().contains("login"));

        // Test case 2: User not found
        String nonExistentEmail = "nonexistent@example.com";
        when(principal.getName()).thenReturn(nonExistentEmail);
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        IllegalArgumentException userNotFoundException = assertThrows(IllegalArgumentException.class, () -> {
            authenticationValidatorService.validateAndGetCurrentUser(principal);
        });
        assertTrue(userNotFoundException.getMessage().contains("tidak ditemukan"));
        assertTrue(userNotFoundException.getMessage().contains(nonExistentEmail));
    }

    @Test
    void testPrivateMethodBehaviorThroughPublicInterface() {
        IllegalArgumentException authException = assertThrows(IllegalArgumentException.class, () -> {
            authenticationValidatorService.validateAndGetCurrentUser(null);
        });
        assertEquals("Anda harus login terlebih dahulu", authException.getMessage());

        // Test getCurrentMahasiswa through successful call
        when(principal.getName()).thenReturn(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testMahasiswa));

        Mahasiswa result = authenticationValidatorService.validateAndGetCurrentUser(principal);
        assertEquals(testMahasiswa, result);
    }

}