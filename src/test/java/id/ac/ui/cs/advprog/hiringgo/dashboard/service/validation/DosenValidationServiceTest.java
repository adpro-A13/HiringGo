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
class DosenValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DosenValidationService service;

    private UUID userId;
    private Dosen dosenMock;
    private Admin adminMock;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        dosenMock = mock(Dosen.class);
        adminMock = mock(Admin.class);
    }

    @Test
    void constructor_shouldInitializeCorrectly() {
        UserRepository mockRepo = mock(UserRepository.class);
        DosenValidationService newService = new DosenValidationService(mockRepo);
        assertNotNull(newService);
    }

    @Test
    void validateDosen_withValidDosenId_shouldNotThrow() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));

        assertDoesNotThrow(() -> service.validateDosen(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void validateDosen_withNonExistentUserId_shouldThrowNoSuchElementException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.validateDosen(userId)
        );

        assertEquals("User tidak ditemukan dengan ID: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void validateDosen_withNonDosenUser_shouldThrowIllegalArgumentException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(adminMock));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateDosen(userId)
        );

        assertEquals("User dengan ID: " + userId + " bukan seorang Dosen", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void getDosenById_withValidDosenId_shouldReturnDosen() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));

        Dosen result = service.getDosenById(userId);

        assertEquals(dosenMock, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void getDosenById_withNonExistentUserId_shouldThrowNoSuchElementException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.getDosenById(userId)
        );

        assertEquals("Dosen tidak ditemukan", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void getDosenById_withNonDosenUser_shouldThrowNoSuchElementException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(adminMock));

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.getDosenById(userId)
        );

        assertEquals("Dosen tidak ditemukan", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void validateDosen_withNullUserId_shouldThrowNoSuchElementException() {
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.validateDosen(null)
        );

        assertEquals("User tidak ditemukan dengan ID: null", exception.getMessage());
        verify(userRepository).findById(null);
    }

    @Test
    void getDosenById_withNullUserId_shouldThrowNoSuchElementException() {
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.getDosenById(null)
        );

        assertEquals("Dosen tidak ditemukan", exception.getMessage());
        verify(userRepository).findById(null);
    }

    @Test
    void validateDosen_withRealDosenInstance_shouldWork() {
        Dosen realDosen = new Dosen("dosen@test.com", "password", "Dr. Dosen", "Computer Science");
        when(userRepository.findById(userId)).thenReturn(Optional.of(realDosen));

        assertDoesNotThrow(() -> service.validateDosen(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void getDosenById_withRealDosenInstance_shouldReturnDosen() {
        Dosen realDosen = new Dosen("dosen@test.com", "password", "Dr. Dosen", "Computer Science");
        when(userRepository.findById(userId)).thenReturn(Optional.of(realDosen));

        Dosen result = service.getDosenById(userId);

        assertEquals(realDosen, result);
        assertEquals("dosen@test.com", result.getUsername());
        assertEquals("Dr. Dosen", result.getFullName());
        verify(userRepository).findById(userId);
    }

    @Test
    void validateDosen_multipleCallsWithSameId_shouldWork() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));

        assertDoesNotThrow(() -> service.validateDosen(userId));
        assertDoesNotThrow(() -> service.validateDosen(userId));

        verify(userRepository, times(2)).findById(userId);
    }

    @Test
    void getDosenById_multipleCallsWithSameId_shouldReturnSameDosen() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(dosenMock));

        Dosen result1 = service.getDosenById(userId);
        Dosen result2 = service.getDosenById(userId);

        assertEquals(dosenMock, result1);
        assertEquals(dosenMock, result2);
        assertEquals(result1, result2);
        verify(userRepository, times(2)).findById(userId);
    }
}