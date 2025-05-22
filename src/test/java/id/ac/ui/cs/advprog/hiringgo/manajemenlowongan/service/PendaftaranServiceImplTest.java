package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.strategy.PendaftaranStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PendaftaranServiceImplTest {

    @Mock
    private PendaftaranRepository pendaftaranRepository;

    @Mock
    private PendaftaranStrategy standardPendaftaranStrategy;

    @Mock
    private PendaftaranStrategy alternativeStrategy;

    private PendaftaranServiceImpl pendaftaranService;
    private UUID lowonganId;
    private Mahasiswa kandidat;
    private BigDecimal ipk;
    private int sks;
    private Pendaftaran expectedPendaftaran;

    @BeforeEach
    void setUp() {
        // Initialize service with the mocked standard strategy
        pendaftaranService = new PendaftaranServiceImpl(pendaftaranRepository, standardPendaftaranStrategy);

        // Test data
        lowonganId = UUID.randomUUID();
        kandidat = new Mahasiswa();
        kandidat.setId(UUID.randomUUID()); // Set ID for error message
        ipk = new BigDecimal("3.75");
        sks = 100;

        // Expected pendaftaran result
        expectedPendaftaran = new Pendaftaran();
        expectedPendaftaran.setPendaftaranId(UUID.randomUUID());
        expectedPendaftaran.setIpk(ipk);
        expectedPendaftaran.setSks(sks);
        expectedPendaftaran.setKandidat(kandidat);
    }

    @Test
    void testConstructorWithNullRepository() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new PendaftaranServiceImpl(null, standardPendaftaranStrategy)
        );
        assertEquals("PendaftaranRepository cannot be null", exception.getMessage());
    }

    @Test
    void testConstructorWithNullStrategy() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new PendaftaranServiceImpl(pendaftaranRepository, null)
        );
        assertEquals("PendaftaranStrategy cannot be null", exception.getMessage());
    }

    @Test
    void testConstructorValidParameters() {
        PendaftaranServiceImpl service = new PendaftaranServiceImpl(pendaftaranRepository, standardPendaftaranStrategy);
        assertNotNull(service);

        // Test the service works by making a call
        when(standardPendaftaranStrategy.execute(any(), any(), any(), anyInt()))
                .thenReturn(new Pendaftaran());

        service.daftar(UUID.randomUUID(), kandidat, ipk, sks);
        verify(standardPendaftaranStrategy).execute(any(), any(), any(), anyInt());
    }

    @Test
    void testDaftarDelegatesCorrectlyToStrategy() {
        // Configure the mock strategy
        when(standardPendaftaranStrategy.execute(lowonganId, kandidat, ipk, sks))
                .thenReturn(expectedPendaftaran);

        // Call the service method
        Pendaftaran result = pendaftaranService.daftar(lowonganId, kandidat, ipk, sks);

        // Verify that strategy was called with correct parameters
        verify(standardPendaftaranStrategy).execute(lowonganId, kandidat, ipk, sks);

        // Verify the result
        assertEquals(expectedPendaftaran, result);
    }

    @Test
    void testDaftarExceptionHandling() {
        // Test that exceptions from the strategy are properly wrapped
        RuntimeException strategyException = new RuntimeException("Strategy error");
        when(standardPendaftaranStrategy.execute(lowonganId, kandidat, ipk, sks))
                .thenThrow(strategyException);

        // The service should wrap the exception in an IllegalStateException
        IllegalStateException actualException = assertThrows(IllegalStateException.class, () -> {
            pendaftaranService.daftar(lowonganId, kandidat, ipk, sks);
        });

        // Verify the exception message and cause
        assertTrue(actualException.getMessage().contains("Error during pendaftaran"));
        assertEquals(strategyException, actualException.getCause());
    }

    @Test
    void testSetPendaftaranStrategy() {
        // Configure alternative strategy
        when(alternativeStrategy.execute(lowonganId, kandidat, ipk, sks))
                .thenReturn(expectedPendaftaran);

        // Switch to alternative strategy
        pendaftaranService.setPendaftaranStrategy(alternativeStrategy);

        // Call the service method
        Pendaftaran result = pendaftaranService.daftar(lowonganId, kandidat, ipk, sks);

        // Verify alternative strategy was used
        verify(alternativeStrategy).execute(lowonganId, kandidat, ipk, sks);
        verify(standardPendaftaranStrategy, never()).execute(any(), any(), any(), anyInt());

        // Verify the result
        assertEquals(expectedPendaftaran, result);
    }

    @Test
    void testSetPendaftaranStrategyWithNull() {
        // Test setting null strategy (should throw IllegalArgumentException)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pendaftaranService.setPendaftaranStrategy(null);
        });

        assertEquals("PendaftaranStrategy cannot be null", exception.getMessage());

        // Verify the original strategy is still used
        when(standardPendaftaranStrategy.execute(any(), any(), any(), anyInt()))
                .thenReturn(new Pendaftaran());

        pendaftaranService.daftar(lowonganId, kandidat, ipk, sks);
        verify(standardPendaftaranStrategy).execute(any(), any(), any(), anyInt());
    }

    @Test
    void testGetByLowongan() {
        // Create test data
        List<Pendaftaran> expectedList = new ArrayList<>();
        expectedList.add(new Pendaftaran());
        expectedList.add(new Pendaftaran());

        // Configure repository mock
        when(pendaftaranRepository.findByLowonganLowonganId(lowonganId)).thenReturn(expectedList);

        // Call the service method
        List<Pendaftaran> result = pendaftaranService.getByLowongan(lowonganId);

        // Verify repository interaction
        verify(pendaftaranRepository).findByLowonganLowonganId(lowonganId);

        // Verify the result
        assertEquals(expectedList, result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetByLowonganWithEmptyResult() {
        // Configure repository mock to return empty list
        when(pendaftaranRepository.findByLowonganLowonganId(lowonganId)).thenReturn(new ArrayList<>());

        // Call the service method
        List<Pendaftaran> result = pendaftaranService.getByLowongan(lowonganId);

        // Verify repository interaction
        verify(pendaftaranRepository).findByLowonganLowonganId(lowonganId);

        // Verify the result
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByLowonganExceptionHandling() {
        // Test that exceptions from the repository are properly wrapped
        RuntimeException repoException = new RuntimeException("Database error");
        when(pendaftaranRepository.findByLowonganLowonganId(lowonganId))
                .thenThrow(repoException);

        // The service should wrap the exception in an IllegalStateException
        IllegalStateException actualException = assertThrows(IllegalStateException.class, () -> {
            pendaftaranService.getByLowongan(lowonganId);
        });

        // Verify the exception message and cause
        assertTrue(actualException.getMessage().contains("Failed to retrieve pendaftaran"));
        assertEquals(repoException, actualException.getCause());
    }

    @Test
    void testGetByLowonganWithNullParameter() {
        // Test with null lowonganId
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pendaftaranService.getByLowongan(null);
        });

        assertEquals("Lowongan ID cannot be null", exception.getMessage());
        verify(pendaftaranRepository, never()).findByLowonganLowonganId(null);
    }

    @Test
    void testDaftarWithNullLowonganId() {
        // Test with null lowonganId
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pendaftaranService.daftar(null, kandidat, ipk, sks);
        });

        assertEquals("Lowongan ID cannot be null", exception.getMessage());
        verify(standardPendaftaranStrategy, never()).execute(any(), any(), any(), anyInt());
    }

    @Test
    void testDaftarWithNullKandidat() {
        // Test with null kandidat
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pendaftaranService.daftar(lowonganId, null, ipk, sks);
        });

        assertEquals("Kandidat cannot be null", exception.getMessage());
        verify(standardPendaftaranStrategy, never()).execute(any(), any(), any(), anyInt());
    }

    @Test
    void testDaftarWithNullIpk() {
        // Test with null ipk
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pendaftaranService.daftar(lowonganId, kandidat, null, sks);
        });

        assertEquals("IPK cannot be null", exception.getMessage());
        verify(standardPendaftaranStrategy, never()).execute(any(), any(), any(), anyInt());
    }
}