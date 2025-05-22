package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.strategy;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandardPendaftaranStrategyTest {

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private PendaftaranRepository pendaftaranRepository;

    @Mock
    private Mahasiswa kandidat;

    private StandardPendaftaranStrategy strategy;
    private UUID lowonganId;
    private BigDecimal ipk;
    private int sks;
    private Lowongan lowongan;

    @BeforeEach
    void setUp() {
        strategy = new StandardPendaftaranStrategy(lowonganRepository, pendaftaranRepository);
        lowonganId = UUID.randomUUID();
        ipk = new BigDecimal("3.75");
        sks = 120;

        // Setup lowongan
        lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setJumlahAsdosDibutuhkan(3);
        lowongan.setJumlahAsdosDiterima(1);
        lowongan.setJumlahAsdosPendaftar(2);
    }

    @Test
    void testConstructorWithNullLowonganRepository() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new StandardPendaftaranStrategy(null, pendaftaranRepository)
        );
        assertEquals("LowonganRepository cannot be null", exception.getMessage());
    }

    @Test
    void testConstructorWithNullPendaftaranRepository() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new StandardPendaftaranStrategy(lowonganRepository, null)
        );
        assertEquals("PendaftaranRepository cannot be null", exception.getMessage());
    }

    @Test
    void testExecuteSuccess() {
        // Configure mocks
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));
        when(pendaftaranRepository.save(any(Pendaftaran.class))).thenAnswer(invocation -> {
            Pendaftaran p = invocation.getArgument(0);
            p.setPendaftaranId(UUID.randomUUID());
            return p;
        });

        // Call method under test
        Pendaftaran result = strategy.execute(lowonganId, kandidat, ipk, sks);

        // Verify results
        assertNotNull(result);
        assertEquals(lowongan, result.getLowongan());
        assertEquals(kandidat, result.getKandidat());
        assertEquals(ipk, result.getIpk());
        assertEquals(sks, result.getSks());
        assertNotNull(result.getWaktuDaftar());

        // Verify lowongan was updated correctly
        verify(lowonganRepository).save(lowongan);
        assertEquals(3, lowongan.getJumlahAsdosPendaftar()); // Increased by 1
    }

    @Test
    void testExecuteWithNullLowonganId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.execute(null, kandidat, ipk, sks)
        );
        assertEquals("Lowongan ID cannot be null", exception.getMessage());

        // Verify repositories were not called
        verifyNoInteractions(lowonganRepository, pendaftaranRepository);
    }

    @Test
    void testExecuteWithNullKandidat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.execute(lowonganId, null, ipk, sks)
        );
        assertEquals("Kandidat cannot be null", exception.getMessage());

        // Verify repositories were not called
        verifyNoInteractions(lowonganRepository, pendaftaranRepository);
    }

    @Test
    void testExecuteWithNullIpk() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.execute(lowonganId, kandidat, null, sks)
        );
        assertEquals("IPK cannot be null", exception.getMessage());

        // Verify repositories were not called
        verifyNoInteractions(lowonganRepository, pendaftaranRepository);
    }

    @Test
    void testExecuteLowonganNotFound() {
        // Configure mock to return empty Optional
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.empty());

        // Call method under test and verify exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                strategy.execute(lowonganId, kandidat, ipk, sks)
        );
        assertEquals("Lowongan tidak ditemukan", exception.getMessage());

        // Verify pendaftaran was not saved
        verify(pendaftaranRepository, never()).save(any());
    }

    @Test
    void testExecuteQuotaFull() {
        // Configure lowongan to have a full quota
        lowongan.setJumlahAsdosDibutuhkan(2);
        lowongan.setJumlahAsdosDiterima(2);

        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));

        // Call method under test and verify exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                strategy.execute(lowonganId, kandidat, ipk, sks)
        );
        assertEquals("Kuota lowongan sudah penuh!", exception.getMessage());

        // Verify pendaftaran was not saved and lowongan not updated
        verify(pendaftaranRepository, never()).save(any());
        verify(lowonganRepository, never()).save(any());
    }

    @Test
    void testExecuteWithUnexpectedException() {
        // Configure mock to throw an unexpected exception
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));
        when(pendaftaranRepository.save(any(Pendaftaran.class))).thenThrow(new RuntimeException("Database error"));

        // Call method under test and verify exception is wrapped
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                strategy.execute(lowonganId, kandidat, ipk, sks)
        );
        assertTrue(exception.getMessage().contains("Gagal melakukan pendaftaran untuk lowongan"));
        assertTrue(exception.getMessage().contains("Database error"));
        assertNotNull(exception.getCause());
        assertEquals("Database error", exception.getCause().getMessage());
    }
}