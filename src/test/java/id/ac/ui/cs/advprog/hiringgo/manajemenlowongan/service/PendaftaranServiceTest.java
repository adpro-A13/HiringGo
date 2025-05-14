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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PendaftaranServiceTest {

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private PendaftaranRepository pendaftaranRepository;

    @InjectMocks
    private PendaftaranServiceImpl pendaftaranService;

    private UUID lowonganId;
    private Lowongan lowongan;
    private UUID kandidatId;
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
        kandidatId = UUID.randomUUID();
        kandidat = new Mahasiswa();
        kandidat.setId(kandidatId);
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
        assertEquals(kandidatId, result.getKandidat().getId());
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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
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
        List<Pendaftaran> dummyList = Arrays.asList(new Pendaftaran());
        when(pendaftaranRepository.findByLowonganLowonganId(lowonganId)).thenReturn(dummyList);

        List<Pendaftaran> resultList = pendaftaranService.getByLowongan(lowonganId);
        assertEquals(dummyList, resultList);
        verify(pendaftaranRepository).findByLowonganLowonganId(lowonganId);
    }
}
