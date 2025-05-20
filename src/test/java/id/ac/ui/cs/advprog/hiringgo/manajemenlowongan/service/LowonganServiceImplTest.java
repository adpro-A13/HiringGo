package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterBySemester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.FilterByStatus;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LowonganServiceImplTest {

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private PendaftaranRepository pendaftaranRepository;
    @InjectMocks
    private LowonganServiceImpl lowonganService;

    private UUID id1;
    private UUID id2;
    private Dosen dosenPengampu;
    private MataKuliah mataKuliah;
    private SecurityContext securityContext;
    private Authentication auth;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dosenPengampu = new Dosen();
        dosenPengampu.setUsername("dosen@example.com");
        dosenPengampu.setNip("12345678");

        // Setup mata kuliah default
        mataKuliah = new MataKuliah("CS100", "Advpro", "advanced programming");
        mataKuliah.addDosenPengampu(dosenPengampu);

        // Setup security context mock
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("dosen@example.com");

        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();
    }
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testFindByIdReturnsLowongan() {
        Lowongan dummy = new Lowongan();
        dummy.setLowonganId(id1);
        when(lowonganRepository.findById(id1)).thenReturn(java.util.Optional.of(dummy));

        Lowongan result = lowonganService.findById(id1);

        assertEquals(dummy, result);
        verify(lowonganRepository).findById(id1);
    }

    @Test
    void testFindByIdNotFound() {

        when(lowonganRepository.findById(id1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            lowonganService.findById(id1);
        });

        assertEquals("Lowongan tidak ditemukan", ex.getMessage());
    }

    @Test
    void testCreateLowonganWhenLowonganDoesNotExist() {
        Lowongan newLowongan = new Lowongan();
        newLowongan.setMataKuliah(mataKuliah);
        newLowongan.setSemester("GANJIL");
        newLowongan.setTahunAjaran("2023");

        when(lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran())
        ).thenReturn(Optional.empty());

        when(lowonganRepository.save(any(Lowongan.class))).thenAnswer(i -> i.getArguments()[0]);

        Lowongan createdLowongan = lowonganService.createLowongan(newLowongan);

        assertNotNull(createdLowongan);
        assertEquals(0, createdLowongan.getJumlahAsdosDiterima());
        assertEquals(0, createdLowongan.getJumlahAsdosPendaftar());

        verify(lowonganRepository).findByMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran());
        verify(lowonganRepository).save(newLowongan);
    }


    @Test
    void testCreateLowonganWhenLowonganAlreadyExists() {
        Lowongan newLowongan = new Lowongan();
        MataKuliah mataKuliah = new MataKuliah("CS100", "Advpro", "advanced programming");
        newLowongan.setMataKuliah(mataKuliah);
        newLowongan.setSemester("GANJIL");
        newLowongan.setTahunAjaran("2023");

        Lowongan existingLowongan = new Lowongan();
        when(lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran())
        ).thenReturn(Optional.of(existingLowongan));

        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> {
            lowonganService.createLowongan(newLowongan);
        });

        verify(lowonganRepository).findByMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran());
        verify(lowonganRepository, times(0)).save(any(Lowongan.class)); // Ensure save was not called
    }
    @Test
    void testFindAllReturnsList() {
        List<Lowongan> dummyList = List.of(new Lowongan(), new Lowongan());
        when(lowonganRepository.findAll()).thenReturn(dummyList);

        List<Lowongan> result = lowonganService.findAll();

        assertEquals(dummyList, result);
    }

    @Test
    void testUpdateLowonganSuccess() {
        MataKuliah mataKuliahAwal = createMataKuliah("CS100", "Advpro", "advanced programming", dosenPengampu);
        Lowongan existingLowongan = createLowongan(id1, mataKuliahAwal, 5, 0);

        MataKuliah mataKuliahBaru = createMataKuliah("CS100", "Sister", "sistem interaksi", dosenPengampu);
        Lowongan updatedLowongan = createLowongan(null, mataKuliahBaru, 8, 0);
        updatedLowongan.setTahunAjaran("2025");
        updatedLowongan.setSemester(String.valueOf(Semester.GENAP));
        updatedLowongan.setStatusLowongan(StatusLowongan.DITUTUP.getValue());

        when(lowonganRepository.findById(id1)).thenReturn(Optional.of(existingLowongan));
        when(lowonganRepository.save(any(Lowongan.class))).thenAnswer(i -> i.getArgument(0));

        Lowongan result = lowonganService.updateLowongan(id1, updatedLowongan);

        assertEquals("2025", result.getTahunAjaran());
        assertEquals((Semester.GENAP), result.getSemester());
        assertEquals(StatusLowongan.DITUTUP, result.getStatusLowongan());
        assertEquals(8, result.getJumlahAsdosDibutuhkan());

        verify(lowonganRepository).findById(id1);
        verify(lowonganRepository).save(existingLowongan);
    }

    @Test
    void testUpdateLowonganFail() {
        Dosen unauthorizedDosen = new Dosen();
        unauthorizedDosen.setNip("123456789");
        unauthorizedDosen.setUsername("unauthorized@example.com");
        MataKuliah mataKuliahLama = createMataKuliah("CS100", "Advpro", "advanced programming", dosenPengampu);
        Lowongan existingLowongan = createLowongan(id1, mataKuliahLama, 5, 2);
        MataKuliah mataKuliahBaru = createMataKuliah("CS102", "Sister", "sistem terdistribusi", unauthorizedDosen);

        Lowongan updatedLowongan = createLowongan(null, mataKuliahBaru, 8, 4);
        updatedLowongan.setTahunAjaran("2025/2026");
        updatedLowongan.setSemester(String.valueOf(Semester.GENAP));
        updatedLowongan.setStatusLowongan(StatusLowongan.DITUTUP.getValue());

        when(auth.getName()).thenReturn("unauthorized@example.com");
        when(lowonganRepository.findById(id1)).thenReturn(Optional.of(existingLowongan));

        assertThrows(AccessDeniedException.class, () -> {
            lowonganService.updateLowongan(id1, updatedLowongan);
        });

        verify(lowonganRepository).findById(id1);
        verify(lowonganRepository, never()).save(any());
    }

    @Test
    void testFilterByStatusLowongan() {
        Lowongan aktif = new Lowongan();
        aktif.setStatusLowongan(StatusLowongan.DIBUKA.getValue());

        Lowongan tidakAktif = new Lowongan();
        tidakAktif.setStatusLowongan(StatusLowongan.DITUTUP.getValue());

        when(lowonganRepository.findAll()).thenReturn(List.of(aktif, tidakAktif));

        var strategy = new FilterByStatus(StatusLowongan.DIBUKA);
        List<Lowongan> result = lowonganService.filterLowongan(strategy);

        assertEquals(1, result.size());
        assertEquals(StatusLowongan.DIBUKA, result.get(0).getStatusLowongan());
    }

    @Test
    void testFilterBySemester() {
        Lowongan genap = new Lowongan();
        genap.setSemester(Semester.GENAP.getValue());

        Lowongan ganjil = new Lowongan();
        ganjil.setSemester(Semester.GANJIL.getValue());

        when(lowonganRepository.findAll()).thenReturn(List.of(genap, ganjil));

        var strategy = new FilterBySemester(Semester.GANJIL);
        List<Lowongan> result = lowonganService.filterLowongan(strategy);

        assertEquals(1, result.size());
        assertEquals(Semester.GANJIL, result.get(0).getSemester());
    }

    @Test
    void testDeleteLowonganByIdSuccess() {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(id1);
        lowongan.setMataKuliah(mataKuliah);

        when(lowonganRepository.findById(id1)).thenReturn(Optional.of(lowongan));

        lowonganService.deleteLowonganById(id1);

        verify(lowonganRepository).findById(id1);
        verify(lowonganRepository).deleteById(id1);
    }

    @Test
    void testDeleteLowonganByIdThrowsWhenNotFound() {
        when(lowonganRepository.findById(id1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lowonganService.deleteLowonganById(id1);
        });

        assertEquals("Lowongan tidak ditemukan", exception.getMessage());

        verify(lowonganRepository).findById(id1);
        verify(lowonganRepository, never()).deleteById(any());
    }


    @Test
    void testTerimaPendaftarThrowsIfPendaftaranNotFound() {
        when(pendaftaranRepository.findById(id1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.terimaPendaftar(id2, id1);
        });

        assertEquals("Pendaftaran tidak ditemukan", exception.getMessage());
        verify(pendaftaranRepository).findById(id1);
    }

    @Test
    void testTolakPendaftarThrowsIfNotFound() {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(id1);
        lowongan.setMataKuliah(mataKuliah);

        when(lowonganRepository.findById(id1)).thenReturn(Optional.of(lowongan));

        when(pendaftaranRepository.findById(id2)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.tolakPendaftar(id1, id2);
        });

        assertEquals("Pendaftaran tidak ditemukan", exception.getMessage());

        verify(pendaftaranRepository).findById(id2);
    }

    @Test
    void testTerimaPendaftarSuccess() {
        MataKuliah mataKuliah = createMataKuliah("CS123", "Algoritma", "Dasar pemrograman", dosenPengampu);
        Lowongan lowongan = createLowongan(id1, mataKuliah, 2, 1);
        lowongan.setStatusLowongan(StatusLowongan.DIBUKA.getValue());

        Pendaftaran pendaftaran = createPendaftaran(id2, lowongan, StatusPendaftaran.BELUM_DIPROSES);

        when(pendaftaranRepository.findById(id2)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(id1)).thenReturn(Optional.of(lowongan));
        when(pendaftaranRepository.save(any(Pendaftaran.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lowonganRepository.save(any(Lowongan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        lowonganService.terimaPendaftar(id1, id2);

        assertEquals(StatusPendaftaran.DITERIMA, pendaftaran.getStatus());
        assertEquals(2, lowongan.getJumlahAsdosDiterima());
        assertEquals(StatusLowongan.DITUTUP, lowongan.getStatusLowongan());

        verify(pendaftaranRepository).findById(id2);
        verify(lowonganRepository).save(lowongan);
    }


    @Test
    void testTolakPendaftarSuccess() {
        MataKuliah mataKuliah = createMataKuliah("CS123", "Algoritma", "Dasar pemrograman", dosenPengampu);
        Lowongan lowongan = createLowongan(id1, mataKuliah, 2, 1);
        Pendaftaran pendaftaran = createPendaftaran(id2, lowongan, StatusPendaftaran.BELUM_DIPROSES);

        when(pendaftaranRepository.findById(id2)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(id1)).thenReturn(Optional.of(lowongan));
        when(pendaftaranRepository.save(any(Pendaftaran.class))).thenAnswer(invocation -> invocation.getArgument(0));

        lowonganService.tolakPendaftar(id1, id2);

        assertEquals(StatusPendaftaran.DITOLAK, pendaftaran.getStatus());
        verify(pendaftaranRepository).findById(id2);
        verify(lowonganRepository, times(2)).findById(id1);
        verify(pendaftaranRepository).save(pendaftaran);
    }



    @Test
    void testTerimaPendaftarPendaftaranNotFound() {
        when(pendaftaranRepository.findById(id1)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.terimaPendaftar(id2, id1);
        });

        assertEquals("Pendaftaran tidak ditemukan", ex.getMessage());

        verify(pendaftaranRepository).findById(id1);
        verifyNoMoreInteractions(pendaftaranRepository, lowonganRepository);
    }


    @Test
    void testTerimaPendaftarLowonganNotFound() {
        MataKuliah mataKuliah = createMataKuliah("CS321", "Pemrograman Lanjut", "Advanced Java", dosenPengampu);
        Lowongan fakeLowongan = createLowongan(id1, mataKuliah, 2, 0);
        Pendaftaran pendaftaran = createPendaftaran(id2, fakeLowongan, StatusPendaftaran.BELUM_DIPROSES);

        when(pendaftaranRepository.findById(id2)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(id1)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.terimaPendaftar(id1, id2);
        });

        assertEquals("Lowongan tidak ditemukan", ex.getMessage());

        verify(pendaftaranRepository).findById(id2);
        verify(lowonganRepository).findById(id1);
        verifyNoMoreInteractions(pendaftaranRepository, lowonganRepository);
    }


    @Test
    void testTerimaPendaftarIdMismatch() {
        Lowongan lowonganInPendaftaran = new Lowongan();
        lowonganInPendaftaran.setLowonganId(UUID.randomUUID());

        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setLowongan(lowonganInPendaftaran);

        Lowongan actualLowongan = new Lowongan();
        actualLowongan.setLowonganId(id1);

        when(pendaftaranRepository.findById(id2)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(id1)).thenReturn(Optional.of(actualLowongan));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.terimaPendaftar(id1, id2);
        });

        assertEquals("Pendaftaran tidak sesuai dengan lowongan", ex.getMessage());

        verify(pendaftaranRepository).findById(id2);
        verify(lowonganRepository).findById(id1);
        verifyNoMoreInteractions(pendaftaranRepository, lowonganRepository);
    }

    @Test
    void testTerimaPendaftarLowonganFull() {
        MataKuliah mataKuliah = new MataKuliah("CS100", "Advpro", "advanced programming");
        mataKuliah.addDosenPengampu(dosenPengampu);
        Lowongan lowongan = createLowongan(id1, mataKuliah, 1, 1);
        Pendaftaran pendaftaran = createPendaftaran(id2, lowongan, StatusPendaftaran.BELUM_DIPROSES);

        when(pendaftaranRepository.findById(id2)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(id1)).thenReturn(Optional.of(lowongan));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            lowonganService.terimaPendaftar(id1, id2);
        });

        assertEquals("Lowongan sudah penuh", ex.getMessage());

        verify(pendaftaranRepository).findById(id2);
        verify(lowonganRepository, times(2)).findById(id1);
    }



    @Test
    void testTolakPendaftarPendaftaranNotFound() {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(id1);
        lowongan.setMataKuliah(mataKuliah);

        when(lowonganRepository.findById(id1)).thenReturn(Optional.of(lowongan));
        when(pendaftaranRepository.findById(id2)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.tolakPendaftar(id1, id2);
        });

        assertEquals("Pendaftaran tidak ditemukan", ex.getMessage());

        verify(pendaftaranRepository).findById(id2);
        verifyNoMoreInteractions(pendaftaranRepository, lowonganRepository);
    }



    @Test
    void testTolakPendaftarLowonganNotFound() {
        MataKuliah mk = createMataKuliah("CS999", "Dummy", "dummy", dosenPengampu);
        Lowongan dummyLowongan = createLowongan(id1, mk, 0, 0);
        Pendaftaran pendaftaran = createPendaftaran(id2, dummyLowongan, null);

        when(pendaftaranRepository.findById(id2)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(id1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            lowonganService.tolakPendaftar(id1, id2);
        });

        assertEquals("Lowongan tidak ditemukan", ex.getMessage());

        verify(pendaftaranRepository).findById(id2);
        verify(lowonganRepository).findById(id1);
        verifyNoMoreInteractions(pendaftaranRepository, lowonganRepository);
    }


    @Test
    void testTolakPendaftarIdMismatch() {
        UUID differentLowonganId = UUID.randomUUID();

        MataKuliah mk = createMataKuliah("CS999", "Dummy", "dummy", dosenPengampu);
        Lowongan lowonganInPendaftaran = createLowongan(differentLowonganId, mk, 0, 0);
        Pendaftaran pendaftaran = createPendaftaran(id1, lowonganInPendaftaran, null);
        Lowongan targetLowongan = createLowongan(id2, mk, 0, 0);

        when(pendaftaranRepository.findById(id1)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(id2)).thenReturn(Optional.of(targetLowongan));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.tolakPendaftar(id2, id1);
        });

        assertEquals("Pendaftaran tidak sesuai dengan lowongan", ex.getMessage());

        verify(pendaftaranRepository).findById(id1);
        verify(lowonganRepository, times(1)).findById(id2);
        verifyNoMoreInteractions(pendaftaranRepository, lowonganRepository);
    }



    @Test
    void testRegisterLowonganSuccess() {
        UUID lowonganId = UUID.randomUUID();
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setJumlahAsdosPendaftar(0);
        lowongan.setJumlahAsdosDibutuhkan(2);

        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));
        when(lowonganRepository.save(any(Lowongan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        lowonganService.registerLowongan(lowonganId, "candidate1");

        assertEquals(1, lowongan.getJumlahAsdosPendaftar());
        verify(lowonganRepository).save(lowongan);
    }

    @Test
    void testRegisterLowonganQuotaFull() {
        UUID lowonganId = UUID.randomUUID();
        Lowongan lowongan = createLowongan(lowonganId, null, 3, 3);
        lowongan.setJumlahAsdosPendaftar(3);

        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            lowonganService.registerLowongan(lowonganId, "candidate1");
        });

        assertEquals("Kuota lowongan sudah penuh!", ex.getMessage());
        verify(lowonganRepository, never()).save(any());
    }

    @Test
    void testFindAllByDosenUsername() {
        Lowongan lowonganCocok = new Lowongan();
        lowonganCocok.setMataKuliah(mataKuliah);
        Dosen dosenLain = new Dosen();
        dosenLain.setUsername("lain@example.com");
        dosenLain.setNip("2345678");
        MataKuliah mataKuliahLain = new MataKuliah("CS200", "Basis Data", "database");
        mataKuliahLain.addDosenPengampu(dosenLain);

        Lowongan lowonganLain = new Lowongan();
        lowonganLain.setMataKuliah(mataKuliahLain);

        when(lowonganRepository.findAll()).thenReturn(List.of(lowonganCocok, lowonganLain));

        List<Lowongan> hasil = lowonganService.findAllByDosenUsername("dosen@example.com");

        assertEquals(1, hasil.size());
        assertTrue(hasil.contains(lowonganCocok));
        assertFalse(hasil.contains(lowonganLain));

        verify(lowonganRepository).findAll();
    }

    private Lowongan createLowongan(UUID lowonganId, MataKuliah mataKuliah, int dibutuhkan, int diterima) {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setJumlahAsdosDibutuhkan(dibutuhkan);
        lowongan.setJumlahAsdosDiterima(diterima);
        return lowongan;
    }

    private Pendaftaran createPendaftaran(UUID pendaftaranId, Lowongan lowongan, StatusPendaftaran status) {
        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(pendaftaranId);
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setStatus(status);
        return pendaftaran;
    }

    private MataKuliah createMataKuliah(String kode, String namaSingkat, String deskripsi, Dosen dosenPengampu) {
        MataKuliah mataKuliah = new MataKuliah(kode, namaSingkat, deskripsi);
        mataKuliah.addDosenPengampu(dosenPengampu);
        return mataKuliah;
    }
}