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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PendaftaranServiceTest {

    @Mock
    private PendaftaranRepository pendaftaranRepository;

    @Mock
    private PendaftaranStrategy pendaftaranStrategy;

    private PendaftaranService pendaftaranService;

    private UUID lowonganId;
    private Mahasiswa kandidat;
    private BigDecimal ipk;
    private int sks;
    private Pendaftaran expectedPendaftaran;

    @BeforeEach
    void setUp() {
        // Use the actual implementation with mocked dependencies
        pendaftaranService = new PendaftaranServiceImpl(pendaftaranRepository, pendaftaranStrategy);

        lowonganId = UUID.randomUUID();
        kandidat = new Mahasiswa();
        kandidat.setId(UUID.randomUUID());
        kandidat.setFullName("Test Mahasiswa");
        ipk = new BigDecimal("3.75");
        sks = 110;

        expectedPendaftaran = new Pendaftaran();
        expectedPendaftaran.setPendaftaranId(UUID.randomUUID());
        expectedPendaftaran.setKandidat(kandidat);
        expectedPendaftaran.setIpk(ipk);
        expectedPendaftaran.setSks(sks);
    }

    @Test
    void daftar_shouldRegisterCandidateForPosition() {
        // Setup strategy to return expected pendaftaran
        when(pendaftaranStrategy.execute(lowonganId, kandidat, ipk, sks))
                .thenReturn(expectedPendaftaran);

        // Call the actual service implementation
        Pendaftaran result = pendaftaranService.daftar(lowonganId, kandidat, ipk, sks);

        // Verify results
        assertNotNull(result);
        assertEquals(expectedPendaftaran, result);
        verify(pendaftaranStrategy).execute(lowonganId, kandidat, ipk, sks);
    }

    @Test
    void daftar_withNullLowonganId_shouldThrowException() {
        // No need to setup mocks for exception cases
        assertThrows(IllegalArgumentException.class, () -> {
            pendaftaranService.daftar(null, kandidat, ipk, sks);
        });

        // Verify strategy was never called
        verifyNoInteractions(pendaftaranStrategy);
    }

    @Test
    void daftar_withNullKandidat_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            pendaftaranService.daftar(lowonganId, null, ipk, sks);
        });

        verifyNoInteractions(pendaftaranStrategy);
    }

    @Test
    void daftar_withNullIpk_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            pendaftaranService.daftar(lowonganId, kandidat, null, sks);
        });

        verifyNoInteractions(pendaftaranStrategy);
    }

    @Test
    void getByLowongan_shouldReturnListOfApplicationsForPosition() {
        List<Pendaftaran> expectedList = Arrays.asList(
                expectedPendaftaran,
                new Pendaftaran()
        );

        // Setup repository to return expected list
        when(pendaftaranRepository.findByLowonganLowonganId(lowonganId)).thenReturn(expectedList);

        // Call the actual service implementation
        List<Pendaftaran> result = pendaftaranService.getByLowongan(lowonganId);

        // Verify results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(pendaftaranRepository).findByLowonganLowonganId(lowonganId);
    }

    @Test
    void getByLowongan_withNullLowonganId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            pendaftaranService.getByLowongan(null);
        });

        verifyNoInteractions(pendaftaranRepository);
    }

    @Test
    void getByLowongan_withEmptyResult_shouldReturnEmptyList() {
        // Setup repository to return empty list
        when(pendaftaranRepository.findByLowonganLowonganId(lowonganId)).thenReturn(List.of());

        List<Pendaftaran> result = pendaftaranService.getByLowongan(lowonganId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(pendaftaranRepository).findByLowonganLowonganId(lowonganId);
    }
}