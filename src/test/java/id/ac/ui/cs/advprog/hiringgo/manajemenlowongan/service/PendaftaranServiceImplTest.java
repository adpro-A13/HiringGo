package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PendaftaranServiceImplTest {

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private PendaftaranRepository pendaftaranRepository;

    @InjectMocks
    private PendaftaranServiceImpl pendaftaranService;

    private UUID lowonganId;
    private Lowongan lowongan;
    private Mahasiswa kandidat;
    private BigDecimal ipk;
    private int sks;

    @BeforeEach
    void setUp() {
        lowonganId = UUID.randomUUID();
        lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setJumlahAsdosDibutuhkan(5);
        lowongan.setJumlahAsdosPendaftar(2);
        kandidat = new Mahasiswa();
        ipk = new BigDecimal("3.5");
        sks = 100;
    }

    @Test
    void testDaftarSuccess() {
        // Stub repository: Lowongan ditemukan dan simpan Pendaftaran berhasil
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));
        when(pendaftaranRepository.save(any(Pendaftaran.class))).thenAnswer(invocation -> {
            Pendaftaran saved = invocation.getArgument(0);
            saved.setPendaftaranId(UUID.randomUUID());  // mimic generated ID
            return saved;
        });

        Pendaftaran result = pendaftaranService.daftar(lowonganId, kandidat, ipk, sks);

        // Pastikan hasil tidak null dan field sesuai
        assertNotNull(result);
        assertEquals(lowongan, result.getLowongan());
        assertEquals(kandidat, result.getKandidat());
        assertEquals(ipk, result.getIpk());
        assertEquals(sks, result.getSks());
        assertNotNull(result.getWaktuDaftar());
        // jumlahAsdosPendaftar di Lowongan seharusnya bertambah 1
        assertEquals(3, lowongan.getJumlahAsdosPendaftar());
        // Verifikasi interaksi repository
        verify(lowonganRepository).findById(lowonganId);
        verify(pendaftaranRepository).save(any(Pendaftaran.class));
        verify(lowonganRepository).save(lowongan);
    }

    @Test
    void testDaftarLowonganNotFound() {
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            pendaftaranService.daftar(lowonganId, kandidat, ipk, sks);
        });
        assertEquals("Lowongan tidak ditemukan", exception.getMessage());

        // Repository pendaftaran dan simpan lowongan tidak dipanggil
        verify(pendaftaranRepository, never()).save(any(Pendaftaran.class));
        verify(lowonganRepository, never()).save(any(Lowongan.class));
    }

    @Test
    void testDaftarLowonganFull() {
        // Kondisi lowongan sudah penuh (pendaftar >= dibutuhkan)
        lowongan.setJumlahAsdosPendaftar(5);
        lowongan.setJumlahAsdosDibutuhkan(5);
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            pendaftaranService.daftar(lowonganId, kandidat, ipk, sks);
        });
        assertEquals("Kuota lowongan sudah penuh!", exception.getMessage());
        // Pastikan pendaftaran tidak disimpan
        verify(pendaftaranRepository, never()).save(any(Pendaftaran.class));
        verify(lowonganRepository, never()).save(any(Lowongan.class));
    }

    @Test
    void testGetByLowongan() {
        List<Pendaftaran> expectedPendaftaran = Arrays.asList(
                createPendaftaran(UUID.randomUUID(), lowongan, new Mahasiswa()),
                createPendaftaran(UUID.randomUUID(), lowongan, new Mahasiswa())
        );

        when(pendaftaranRepository.findByLowonganLowonganId(lowonganId)).thenReturn(expectedPendaftaran);

        List<Pendaftaran> resultList = pendaftaranService.getByLowongan(lowonganId);

        assertEquals(expectedPendaftaran, resultList);
        assertEquals(2, resultList.size());
        verify(pendaftaranRepository).findByLowonganLowonganId(lowonganId);
    }

    @Test
    void testGetByLowonganEmptyResult() {
        when(pendaftaranRepository.findByLowonganLowonganId(lowonganId)).thenReturn(Collections.emptyList());

        List<Pendaftaran> resultList = pendaftaranService.getByLowongan(lowonganId);

        assertTrue(resultList.isEmpty());
        verify(pendaftaranRepository).findByLowonganLowonganId(lowonganId);
    }

    private Pendaftaran createPendaftaran(UUID id, Lowongan lowongan, Mahasiswa kandidat) {
        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(id);
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setKandidat(kandidat);
        pendaftaran.setIpk(new BigDecimal("3.5"));
        pendaftaran.setSks(100);
        pendaftaran.setWaktuDaftar(LocalDateTime.now());
        return pendaftaran;
    }
}