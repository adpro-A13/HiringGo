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
    }
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testFindByIdReturnsLowongan() {
        UUID id = UUID.randomUUID();
        Lowongan dummy = new Lowongan();
        dummy.setLowonganId(id);
        when(lowonganRepository.findById(id)).thenReturn(java.util.Optional.of(dummy));

        Lowongan result = lowonganService.findById(id);

        assertEquals(dummy, result);
        verify(lowonganRepository).findById(id);
    }

    @Test
    void testFindByIdNotFound() {
        UUID id = UUID.randomUUID();

        when(lowonganRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            lowonganService.findById(id);
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
        UUID id = UUID.randomUUID();

        Lowongan existingLowongan = new Lowongan();
        existingLowongan.setLowonganId(id);
        existingLowongan.setMataKuliah(mataKuliah);
        existingLowongan.setTahunAjaran("2024");
        existingLowongan.setSemester(String.valueOf(Semester.GANJIL));
        existingLowongan.setStatusLowongan(StatusLowongan.DIBUKA.getValue());
        existingLowongan.setJumlahAsdosDibutuhkan(5);
        existingLowongan.setJumlahAsdosDiterima(0);
        existingLowongan.setJumlahAsdosPendaftar(10);

        MataKuliah mataKuliah2 = new MataKuliah("CS102", "Sister", "sistem interaksi");
        mataKuliah2.addDosenPengampu(dosenPengampu);

        Lowongan updatedLowongan = new Lowongan();
        updatedLowongan.setMataKuliah(mataKuliah2);
        updatedLowongan.setTahunAjaran("2025");
        updatedLowongan.setSemester(String.valueOf(Semester.GENAP));
        updatedLowongan.setStatusLowongan(StatusLowongan.DITUTUP.getValue());
        updatedLowongan.setJumlahAsdosDibutuhkan(8);
        updatedLowongan.setJumlahAsdosDiterima(4);
        updatedLowongan.setJumlahAsdosPendaftar(15);

        when(lowonganRepository.findById(id)).thenReturn(Optional.of(existingLowongan));
        when(lowonganRepository.save(any(Lowongan.class))).thenAnswer(i -> i.getArguments()[0]);

        Lowongan result = lowonganService.updateLowongan(id, updatedLowongan);

        assertEquals("CS102", result.getMataKuliah().getKode());
        assertEquals("2025", result.getTahunAjaran());
        assertEquals(Semester.GENAP, result.getSemester());
        assertEquals(StatusLowongan.DITUTUP, result.getStatusLowongan());
        assertEquals(8, result.getJumlahAsdosDibutuhkan());
        assertEquals(4, result.getJumlahAsdosDiterima());
        assertEquals(15, result.getJumlahAsdosPendaftar());

        verify(lowonganRepository).findById(id);
        verify(lowonganRepository).save(existingLowongan);
    }

    @Test
    void testUpdateLowonganFail() {
        UUID id = UUID.randomUUID();

        MataKuliah mataKuliahBaru = new MataKuliah("CS100", "Advpro", "advanced programming");
        Dosen unauthorizedDosen = new Dosen();
        unauthorizedDosen.setUsername("otherdosen@example.com");
        unauthorizedDosen.setNip("1234567890");
        mataKuliahBaru.addDosenPengampu(unauthorizedDosen);

        Lowongan updatedLowongan = new Lowongan();
        updatedLowongan.setMataKuliah(mataKuliahBaru);
        updatedLowongan.setTahunAjaran("2025/2026");
        updatedLowongan.setSemester(String.valueOf(Semester.GENAP));
        updatedLowongan.setStatusLowongan(StatusLowongan.DITUTUP.getValue());
        updatedLowongan.setJumlahAsdosDibutuhkan(8);
        updatedLowongan.setJumlahAsdosDiterima(4);
        updatedLowongan.setJumlahAsdosPendaftar(15);

        when(auth.getName()).thenReturn("unauthorized@example.com");
        when(lowonganRepository.findById(id)).thenReturn(Optional.of(updatedLowongan));

        assertThrows(AccessDeniedException.class, () -> {
            lowonganService.updateLowongan(id, updatedLowongan);
        });

        verify(lowonganRepository).findById(id);
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
        UUID id = UUID.randomUUID();

        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(id);
        lowongan.setMataKuliah(mataKuliah);

        when(lowonganRepository.findById(id)).thenReturn(Optional.of(lowongan));

        lowonganService.deleteLowonganById(id);

        verify(lowonganRepository).findById(id);
        verify(lowonganRepository).deleteById(id);
    }





    @Test
    void testDeleteLowonganByIdThrowsWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(lowonganRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lowonganService.deleteLowonganById(id);
        });

        assertEquals("Lowongan tidak ditemukan", exception.getMessage());

        verify(lowonganRepository).findById(id);
        verify(lowonganRepository, never()).deleteById(any());
    }


    @Test
    void testTerimaPendaftarThrowsIfPendaftaranNotFound() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        // Mocking pendaftaranRepository.findById untuk return empty Optional supaya exception dilempar
        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.empty());

        // Karena di service kamu lempar IllegalArgumentException jika pendaftaran tidak ditemukan
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.terimaPendaftar(lowonganId, pendaftaranId);
        });

        assertEquals("Pendaftaran tidak ditemukan", exception.getMessage());

        // Verifikasi findById dipanggil
        verify(pendaftaranRepository).findById(pendaftaranId);
    }

    @Test
    void testTolakPendaftarThrowsIfNotFound() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();
        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.tolakPendaftar(lowonganId, pendaftaranId);
        });

        assertEquals("Pendaftaran tidak ditemukan", exception.getMessage());

        verify(pendaftaranRepository).findById(pendaftaranId);
    }


    @Test
    void testTerimaPendaftarSuccess() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setJumlahAsdosDibutuhkan(2);
        lowongan.setJumlahAsdosDiterima(1);
        lowongan.setStatusLowongan(StatusLowongan.DIBUKA.getValue());

        mataKuliah.addDosenPengampu(dosenPengampu);

        lowongan.setMataKuliah(mataKuliah);

        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(pendaftaranId);
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));
        when(pendaftaranRepository.save(any(Pendaftaran.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lowonganRepository.save(any(Lowongan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        lowonganService.terimaPendaftar(lowonganId, pendaftaranId);

        assertEquals(StatusPendaftaran.DITERIMA, pendaftaran.getStatus());
        assertEquals(2, lowongan.getJumlahAsdosDiterima());
        assertEquals(StatusLowongan.DITUTUP, lowongan.getStatusLowongan());

        verify(pendaftaranRepository).findById(pendaftaranId);
        verify(lowonganRepository).findById(lowonganId);
        verify(pendaftaranRepository).save(pendaftaran);
        verify(lowonganRepository).save(lowongan);
    }


    @Test
    void testTolakPendaftarSuccess() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        mataKuliah.addDosenPengampu(dosenPengampu);

        lowongan.setMataKuliah(mataKuliah);

        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(pendaftaranId);
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));
        when(pendaftaranRepository.save(any(Pendaftaran.class))).thenAnswer(invocation -> invocation.getArgument(0));

        lowonganService.tolakPendaftar(lowonganId, pendaftaranId);

        assertEquals(StatusPendaftaran.DITOLAK, pendaftaran.getStatus());
        verify(pendaftaranRepository).findById(pendaftaranId);
        verify(lowonganRepository).findById(lowonganId);
        verify(pendaftaranRepository).save(pendaftaran);
    }


    @Test
    void testTerimaPendaftarPendaftaranNotFound() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.terimaPendaftar(lowonganId, pendaftaranId);
        });

        assertEquals("Pendaftaran tidak ditemukan", ex.getMessage());

        verify(pendaftaranRepository).findById(pendaftaranId);
        verifyNoMoreInteractions(pendaftaranRepository, lowonganRepository);
    }


    @Test
    void testTerimaPendaftarLowonganNotFound() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        Lowongan fakeLowongan = new Lowongan();
        fakeLowongan.setLowonganId(lowonganId);
        fakeLowongan.setMataKuliah(mataKuliah);

        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(pendaftaranId);
        pendaftaran.setLowongan(fakeLowongan);
        pendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.terimaPendaftar(lowonganId, pendaftaranId);
        });

        assertEquals("Lowongan tidak ditemukan", ex.getMessage());

        verify(pendaftaranRepository).findById(pendaftaranId);
        verify(lowonganRepository).findById(lowonganId);
        verifyNoMoreInteractions(pendaftaranRepository, lowonganRepository);
    }

    @Test
    void testTerimaPendaftarIdMismatch() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        // Lowongan dalam pendaftaran berbeda ID dengan parameter
        Lowongan lowonganInPendaftaran = new Lowongan();
        lowonganInPendaftaran.setLowonganId(UUID.randomUUID()); // ID beda dari lowonganId

        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setLowongan(lowonganInPendaftaran);

        Lowongan actualLowongan = new Lowongan();
        actualLowongan.setLowonganId(lowonganId);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(actualLowongan));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.terimaPendaftar(lowonganId, pendaftaranId);
        });

        assertEquals("Pendaftaran tidak sesuai dengan lowongan", ex.getMessage());

        verify(pendaftaranRepository).findById(pendaftaranId);
        verify(lowonganRepository).findById(lowonganId);
        verifyNoMoreInteractions(pendaftaranRepository, lowonganRepository);
    }


    @Test
    void testTerimaPendaftarLowonganFull() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();
        MataKuliah mataKuliah = new MataKuliah("CS100", "Advpro", "advanced programming");
        mataKuliah.addDosenPengampu(dosenPengampu);

        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setJumlahAsdosDibutuhkan(1);
        lowongan.setJumlahAsdosDiterima(1);
        lowongan.setMataKuliah(mataKuliah);

        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(pendaftaranId);
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            lowonganService.terimaPendaftar(lowonganId, pendaftaranId);
        });

        assertEquals("Lowongan sudah penuh", ex.getMessage());

        verify(pendaftaranRepository).findById(pendaftaranId);
        verify(lowonganRepository).findById(lowonganId);
    }


    @Test
    void testTolakPendaftarPendaftaranNotFound() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.tolakPendaftar(lowonganId, pendaftaranId);
        });

        assertEquals("Pendaftaran tidak ditemukan", ex.getMessage());

        verify(pendaftaranRepository).findById(pendaftaranId);
        verifyNoMoreInteractions(pendaftaranRepository, lowonganRepository);
    }


    @Test
    void testTolakPendaftarLowonganNotFound() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        Lowongan dummyLowongan = new Lowongan();
        dummyLowongan.setLowonganId(lowonganId);
        MataKuliah mk = new MataKuliah("CS999", "Dummy", "dummy");
        dummyLowongan.setMataKuliah(mk);
        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setLowongan(dummyLowongan);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.tolakPendaftar(lowonganId, pendaftaranId);
        });

        assertEquals("Lowongan tidak ditemukan", ex.getMessage());

        verify(pendaftaranRepository).findById(pendaftaranId);
        verify(lowonganRepository).findById(lowonganId);
        verifyNoMoreInteractions(pendaftaranRepository, lowonganRepository);
    }

    @Test
    void testTolakPendaftarIdMismatch() {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        UUID differentLowonganId = UUID.randomUUID();
        Lowongan lowonganInPendaftaran = new Lowongan();
        lowonganInPendaftaran.setLowonganId(differentLowonganId);
        MataKuliah mk = new MataKuliah("CS999", "Dummy", "dummy");
        mk.addDosenPengampu(dosenPengampu);
        lowonganInPendaftaran.setMataKuliah(mk);

        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setLowongan(lowonganInPendaftaran);

        Lowongan targetLowongan = new Lowongan();
        targetLowongan.setLowonganId(lowonganId);
        targetLowongan.setMataKuliah(mk);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(targetLowongan));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.tolakPendaftar(lowonganId, pendaftaranId);
        });

        assertEquals("Pendaftaran tidak sesuai dengan lowongan", ex.getMessage());

        verify(pendaftaranRepository).findById(pendaftaranId);
        verify(lowonganRepository).findById(lowonganId);
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
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setJumlahAsdosPendaftar(3);
        lowongan.setJumlahAsdosDibutuhkan(3);

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
}