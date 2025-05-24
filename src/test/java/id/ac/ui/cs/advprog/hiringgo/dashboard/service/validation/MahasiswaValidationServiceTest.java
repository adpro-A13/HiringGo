package id.ac.ui.cs.advprog.hiringgo.dashboard.service.validation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
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
class MahasiswaValidationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MahasiswaValidationService service;

    private UUID userId;
    private Mahasiswa mahasiswaMock;
    private Admin adminMock;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        mahasiswaMock = mock(Mahasiswa.class);
        adminMock = mock(Admin.class);
    }

    @Test
    void constructor_shouldInitializeCorrectly() {
        UserRepository mockRepo = mock(UserRepository.class);
        MahasiswaValidationService newService = new MahasiswaValidationService(mockRepo);
        assertNotNull(newService);
    }

    @Test
    void validateMahasiswa_withValidMahasiswaId_shouldNotThrow() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mahasiswaMock));

        assertDoesNotThrow(() -> service.validateMahasiswa(userId));

        verify(userRepository).existsById(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void validateMahasiswa_withNonExistentUserId_shouldThrowNoSuchElementException() {
        when(userRepository.existsById(userId)).thenReturn(false);

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.validateMahasiswa(userId)
        );

        assertEquals("User tidak ditemukan dengan ID: " + userId, exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).findById(userId);
    }

    @Test
    void validateMahasiswa_withNonMahasiswaUser_shouldThrowIllegalArgumentException() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(adminMock));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateMahasiswa(userId)
        );

        assertEquals("User dengan ID: " + userId + " bukan mahasiswa", exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void validateMahasiswa_withEmptyOptional_shouldThrowIllegalArgumentException() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateMahasiswa(userId)
        );

        assertEquals("User dengan ID: " + userId + " bukan mahasiswa", exception.getMessage());
        verify(userRepository).existsById(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void getMahasiswaById_withValidMahasiswaId_shouldReturnMahasiswa() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mahasiswaMock));

        Mahasiswa result = service.getMahasiswaById(userId);

        assertEquals(mahasiswaMock, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void getMahasiswaById_withNonExistentUserId_shouldThrowNoSuchElementException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.getMahasiswaById(userId)
        );

        assertEquals("Mahasiswa tidak ditemukan dengan ID: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void getMahasiswaById_withNonMahasiswaUser_shouldThrowNoSuchElementException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(adminMock));

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.getMahasiswaById(userId)
        );

        assertEquals("Mahasiswa tidak ditemukan dengan ID: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void validateMahasiswa_withNullUserId_shouldThrowNoSuchElementException() {
        when(userRepository.existsById(null)).thenReturn(false);

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.validateMahasiswa(null)
        );

        assertEquals("User tidak ditemukan dengan ID: null", exception.getMessage());
        verify(userRepository).existsById(null);
    }

    @Test
    void getMahasiswaById_withNullUserId_shouldThrowNoSuchElementException() {
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> service.getMahasiswaById(null)
        );

        assertEquals("Mahasiswa tidak ditemukan dengan ID: null", exception.getMessage());
        verify(userRepository).findById(null);
    }

    @Test
    void validateMahasiswa_withRealMahasiswaInstance_shouldWork() {
        Mahasiswa realMahasiswa = new Mahasiswa("mahasiswa@test.com", "password", "Mahasiswa Test", "2106123456");
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(realMahasiswa));

        assertDoesNotThrow(() -> service.validateMahasiswa(userId));

        verify(userRepository).existsById(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void getMahasiswaById_withRealMahasiswaInstance_shouldReturnMahasiswa() {
        Mahasiswa realMahasiswa = new Mahasiswa("mahasiswa@test.com", "password", "Mahasiswa Test", "2106123456");
        when(userRepository.findById(userId)).thenReturn(Optional.of(realMahasiswa));

        Mahasiswa result = service.getMahasiswaById(userId);

        assertEquals(realMahasiswa, result);
        assertEquals("mahasiswa@test.com", result.getUsername());
        assertEquals("Mahasiswa Test", result.getFullName());
        assertEquals("2106123456", result.getNim());
        verify(userRepository).findById(userId);
    }

    @Test
    void validateMahasiswa_multipleCallsWithSameId_shouldWork() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mahasiswaMock));

        assertDoesNotThrow(() -> service.validateMahasiswa(userId));
        assertDoesNotThrow(() -> service.validateMahasiswa(userId));

        verify(userRepository, times(2)).existsById(userId);
        verify(userRepository, times(2)).findById(userId);
    }

    @Test
    void getMahasiswaById_multipleCallsWithSameId_shouldReturnSameMahasiswa() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mahasiswaMock));

        Mahasiswa result1 = service.getMahasiswaById(userId);
        Mahasiswa result2 = service.getMahasiswaById(userId);

        assertEquals(mahasiswaMock, result1);
        assertEquals(mahasiswaMock, result2);
        assertEquals(result1, result2);
        verify(userRepository, times(2)).findById(userId);
    }

    @Test
    void validateMahasiswa_withDifferentIds_shouldWorkIndependently() {
        UUID userId2 = UUID.randomUUID();
        Mahasiswa mahasiswa2 = mock(Mahasiswa.class);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mahasiswaMock));
        when(userRepository.existsById(userId2)).thenReturn(true);
        when(userRepository.findById(userId2)).thenReturn(Optional.of(mahasiswa2));

        assertDoesNotThrow(() -> service.validateMahasiswa(userId));
        assertDoesNotThrow(() -> service.validateMahasiswa(userId2));

        verify(userRepository).existsById(userId);
        verify(userRepository).findById(userId);
        verify(userRepository).existsById(userId2);
        verify(userRepository).findById(userId2);
    }
}