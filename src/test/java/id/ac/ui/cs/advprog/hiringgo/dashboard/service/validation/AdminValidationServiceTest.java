package id.ac.ui.cs.advprog.hiringgo.dashboard.service.validation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminValidationService service;

    private UUID userId;
    private Admin adminMock;
    private Dosen dosenMock;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        adminMock = mock(Admin.class);
        dosenMock = mock(Dosen.class);
    }

    @Test
    void constructor_shouldInitializeCorrectly() {
        UserRepository mockRepo = mock(UserRepository.class);
        AdminValidationService newService = new AdminValidationService(mockRepo);
        assertNotNull(newService);
    }

    @Test
    void validateAdmin_withValidAdminId_shouldNotThrow() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(adminMock));

        assertDoesNotThrow(() -> service.validateAdmin(userId));

        verify(userRepository).existsById(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void validateAdmin_withNonExistentUserId_shouldThrowNoSuchElementException() {
        when(userRepository.existsById(userId)).thenReturn(false);

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.validateAdmin(userId)
        );

        assertEquals("User tidak ditemukan dengan ID: " + userId, exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).findById(userId);
    }

    @Test
    void validateAdmin_withNonAdminUser_shouldThrowIllegalArgumentException() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateAdmin(userId)
        );

        assertEquals("User dengan ID: " + userId + " bukan admin", exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void validateAdmin_withEmptyOptional_shouldThrowIllegalArgumentException() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateAdmin(userId)
        );

        assertEquals("User dengan ID: " + userId + " bukan admin", exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void getAdminById_withValidAdminId_shouldReturnAdmin() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(adminMock));

        Admin result = service.getAdminById(userId);

        assertEquals(adminMock, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void getAdminById_withNonExistentUserId_shouldThrowNoSuchElementException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.getAdminById(userId)
        );

        assertEquals("Admin tidak ditemukan dengan ID: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void getAdminById_withNonAdminUser_shouldThrowNoSuchElementException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.getAdminById(userId)
        );

        assertEquals("Admin tidak ditemukan dengan ID: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void validateAdmin_withNullUserId_shouldHandleGracefully() {
        when(userRepository.existsById(null)).thenReturn(false);

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.validateAdmin(null)
        );

        assertEquals("User tidak ditemukan dengan ID: null", exception.getMessage());
        verify(userRepository).existsById(null);
    }

    @Test
    void getAdminById_withNullUserId_shouldThrowNoSuchElementException() {
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.getAdminById(null)
        );

        assertEquals("Admin tidak ditemukan dengan ID: null", exception.getMessage());
        verify(userRepository).findById(null);
    }

    @Test
    void validateAdmin_withRealAdminInstance_shouldWork() {
        Admin realAdmin = new Admin("admin@test.com", "password");
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(realAdmin));

        assertDoesNotThrow(() -> service.validateAdmin(userId));

        verify(userRepository).existsById(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void getAdminById_withRealAdminInstance_shouldReturnAdmin() {
        Admin realAdmin = new Admin("admin@test.com", "password");
        when(userRepository.findById(userId)).thenReturn(Optional.of(realAdmin));

        Admin result = service.getAdminById(userId);

        assertEquals(realAdmin, result);
        assertEquals("admin@test.com", result.getUsername());
        verify(userRepository).findById(userId);
    }
}