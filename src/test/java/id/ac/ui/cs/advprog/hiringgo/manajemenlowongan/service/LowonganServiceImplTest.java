package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.event.NotifikasiEvent;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.util.Pair;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.context.ApplicationEventPublisher;


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
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private LowonganServiceValidator validator;


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

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> lowonganService.findById(id1)
        );
        assertEquals("Lowongan tidak ditemukan", ex.getMessage());

        verifyNoInteractions(validator);
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

        when(validator.isNotAuthorizedDosenPengampu(newLowongan, "dosen@example.com"))
                .thenReturn(false);

        when(lowonganRepository.save(any(Lowongan.class)))
                .thenAnswer(i -> i.getArgument(0));

        Lowongan createdLowongan = lowonganService.createLowongan(newLowongan);

        assertNotNull(createdLowongan);
        assertEquals(0, createdLowongan.getJumlahAsdosDiterima());
        assertEquals(0, createdLowongan.getJumlahAsdosPendaftar());

        verify(lowonganRepository).findByMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran());
        verify(validator).isNotAuthorizedDosenPengampu(newLowongan, "dosen@example.com");
        verify(lowonganRepository).save(newLowongan);
    }



    @Test
    void testCreateLowonganWhenLowonganAlreadyExists() {
        Lowongan newLowongan = new Lowongan();
        mataKuliah = new MataKuliah("CS100", "Advpro", "advanced programming");
        newLowongan.setMataKuliah(mataKuliah);
        newLowongan.setSemester("GANJIL");
        newLowongan.setTahunAjaran("2023");

        Lowongan existingLowongan = new Lowongan();
        when(lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                newLowongan.getMataKuliah(),
                newLowongan.getSemester(),
                newLowongan.getTahunAjaran())
        ).thenReturn(Optional.of(existingLowongan));

        assertThrows(IllegalStateException.class, () -> {
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
        // Arrange
        MataKuliah mataKuliahAwal = createMataKuliah("CS100", "Advpro",
                "advanced programming", dosenPengampu);
        Lowongan existingLowongan = createLowongan(id1, mataKuliahAwal, 5, 0);

        MataKuliah mataKuliahBaru = createMataKuliah("CS100", "Sister",
                "sistem interaksi", dosenPengampu);
        Lowongan updatedLowongan = createLowongan(null, mataKuliahBaru, 8, 0);
        updatedLowongan.setTahunAjaran("2025");
        String semesterBaru = Semester.GENAP.name();
        updatedLowongan.setSemester(semesterBaru);
        String statusBaru = StatusLowongan.DITUTUP.getValue();
        updatedLowongan.setStatusLowongan(statusBaru);

        when(validator.getAuthorizedLowongan(id1)).thenReturn(existingLowongan);
        when(lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaranAndJumlahAsdosDibutuhkan(
                mataKuliahBaru, Semester.valueOf(semesterBaru), "2025", 8))
                .thenReturn(Optional.empty());
        when(lowonganRepository.save(any(Lowongan.class)))
                .thenAnswer(i -> i.getArgument(0));

        Lowongan result = lowonganService.updateLowongan(id1, updatedLowongan);

        assertEquals("2025", result.getTahunAjaran());
        assertEquals(semesterBaru, result.getSemester().getValue());
        assertEquals(statusBaru, result.getStatusLowongan().getValue());
        assertEquals(8, result.getJumlahAsdosDibutuhkan());

        verify(validator).getAuthorizedLowongan(id1);
        verify(lowonganRepository).save(existingLowongan);
    }


    @Test
    void testUpdateLowonganFail() {
        MataKuliah mataKuliahBaru = createMataKuliah(
                "CS102", "Sister", "sistem interaksi", dosenPengampu);
        Lowongan updatedLowongan = createLowongan(null, mataKuliahBaru, 8, 4);
        updatedLowongan.setTahunAjaran("2025/2026");
        updatedLowongan.setSemester(String.valueOf(Semester.GENAP));
        updatedLowongan.setStatusLowongan(StatusLowongan.DITUTUP.getValue());

        when(validator.getAuthorizedLowongan(id1))
                .thenThrow(new AccessDeniedException("Anda bukan pengampu mata kuliah ini."));

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> lowonganService.updateLowongan(id1, updatedLowongan)
        );
        assertEquals("Anda bukan pengampu mata kuliah ini.", ex.getMessage());

        verify(validator).getAuthorizedLowongan(id1);
        verifyNoMoreInteractions(validator, lowonganRepository);
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
        when(validator.validatePendaftaranAndLowongan( id2, id1, "dosen@example.com"
        )).thenThrow(new IllegalArgumentException("Pendaftaran tidak ditemukan"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> lowonganService.terimaPendaftar(id2, id1)
        );
        assertEquals("Pendaftaran tidak ditemukan", exception.getMessage());

        verify(validator).validatePendaftaranAndLowongan(id2, id1, "dosen@example.com");
        verifyNoInteractions(pendaftaranRepository, lowonganRepository, eventPublisher);
    }


    @Test
    void testTolakPendaftarThrowsIfNotFound() {
        when(validator.validatePendaftaranAndLowongan(id1, id2, "dosen@example.com"))
                .thenThrow(new IllegalArgumentException("Pendaftaran tidak ditemukan"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> lowonganService.tolakPendaftar(id1, id2)
        );
        assertEquals("Pendaftaran tidak ditemukan", exception.getMessage());

        verify(validator).validatePendaftaranAndLowongan(id1, id2, "dosen@example.com");
        verifyNoInteractions(pendaftaranRepository, lowonganRepository, eventPublisher);
    }


    @Test
    void testTerimaPendaftar_Success() {
        MataKuliah mk = createMataKuliah("CS100", "Advpro", "advanced programming", dosenPengampu);
        Lowongan lowongan = createLowongan(id1, mk, 2, 1);
        lowongan.setTahunAjaran("2023/2024");
        lowongan.setSemester(String.valueOf(Semester.GENAP));
        Pendaftaran pendaftaran = createPendaftaran(id2, lowongan, StatusPendaftaran.BELUM_DIPROSES);

        LowonganServiceImpl spyService = Mockito.spy(lowonganService);
        Mockito.doReturn(Pair.of(pendaftaran, lowongan))
                .when(validator)
                .validatePendaftaranAndLowongan(id1, id2, "dosen@example.com");
        Mockito.doReturn(lowongan).when(validator).getAuthorizedLowongan(id1);

        when(pendaftaranRepository.save(any(Pendaftaran.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lowonganRepository.save(any(Lowongan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        spyService.terimaPendaftar(id1, id2);

        assertEquals(StatusPendaftaran.DITERIMA, pendaftaran.getStatus());
        verify(pendaftaranRepository).save(pendaftaran);

        assertEquals(2, lowongan.getJumlahAsdosDiterima());
        assertEquals((StatusLowongan.DITUTUP), lowongan.getStatusLowongan());
        verify(lowonganRepository).save(lowongan);

        ArgumentCaptor<NotifikasiEvent> eventCaptor = ArgumentCaptor.forClass(NotifikasiEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        NotifikasiEvent publishedEvent = eventCaptor.getValue();
        assertEquals(pendaftaran.getKandidat(), publishedEvent.getMahasiswa());
        assertEquals(mk, publishedEvent.getMataKuliah());
        assertEquals("2023/2024", publishedEvent.getTahunAjaran());
        assertEquals(Semester.GENAP, publishedEvent.getSemester());
        assertEquals("DITERIMA", publishedEvent.getStatus());
    }

    @Test
    void testTerimaPendaftar_ThrowsWhenLowonganFull() {
        MataKuliah mk = createMataKuliah("CS100", "Advpro", "advanced programming", dosenPengampu);
        Lowongan lowongan = createLowongan(id1, mk, 1, 1);
        Pendaftaran pendaftaran = createPendaftaran(id2, lowongan, StatusPendaftaran.BELUM_DIPROSES);

        when(validator.validatePendaftaranAndLowongan(id1, id2, "dosen@example.com"))
                .thenReturn(Pair.of(pendaftaran, lowongan));
        doThrow(new IllegalStateException("Lowongan sudah penuh"))
                .when(validator).validateStatusAndCapacity(pendaftaran, lowongan);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            lowonganService.terimaPendaftar(id1, id2);
        });

        assertEquals("Lowongan sudah penuh", ex.getMessage());

        verify(pendaftaranRepository, never()).save(any());
        verify(lowonganRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }



    @Test
    void testTerimaPendaftarPendaftaranNotFound() {
        when(validator.validatePendaftaranAndLowongan(id2, id1, "dosen@example.com"))
                .thenThrow(new IllegalArgumentException("Pendaftaran tidak ditemukan"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> lowonganService.terimaPendaftar(id2, id1)
        );
        assertEquals("Pendaftaran tidak ditemukan", ex.getMessage());

        verify(validator).validatePendaftaranAndLowongan(id2, id1, "dosen@example.com");
        verifyNoInteractions(pendaftaranRepository, lowonganRepository, eventPublisher);
    }



    @Test
    void testTerimaPendaftarLowonganNotFound() {
        when(validator.validatePendaftaranAndLowongan( id1, id2, "dosen@example.com"))
                .thenThrow(new IllegalArgumentException("Lowongan tidak ditemukan"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> lowonganService.terimaPendaftar(id1, id2)
        );
        assertEquals("Lowongan tidak ditemukan", ex.getMessage());

        verify(validator).validatePendaftaranAndLowongan(id1, id2, "dosen@example.com");
        verifyNoInteractions(pendaftaranRepository, lowonganRepository, eventPublisher);
    }



    @Test
    void testTerimaPendaftarIdMismatch() {
        when(validator.validatePendaftaranAndLowongan( id1, id2, "dosen@example.com"))
                .thenThrow(new IllegalArgumentException("Pendaftaran tidak sesuai dengan lowongan"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> lowonganService.terimaPendaftar(id1, id2)
        );
        assertEquals("Pendaftaran tidak sesuai dengan lowongan", ex.getMessage());

        verify(validator).validatePendaftaranAndLowongan(id1, id2, "dosen@example.com");
        verifyNoInteractions(pendaftaranRepository, lowonganRepository, eventPublisher);
    }


    @Test
    void testTerimaPendaftarLowonganFull() {
        MataKuliah mk = new MataKuliah("CS100", "Advpro", "advanced programming");
        mk.addDosenPengampu(dosenPengampu);
        Lowongan lowongan = createLowongan(id1, mk, 1, 1);
        Pendaftaran pendaftaran = createPendaftaran(id2, lowongan, StatusPendaftaran.BELUM_DIPROSES);

        when(validator.validatePendaftaranAndLowongan(id1, id2, "dosen@example.com"))
                .thenReturn(Pair.of(pendaftaran, lowongan));
        doThrow(new IllegalStateException("Lowongan sudah penuh"))
                .when(validator).validateStatusAndCapacity(pendaftaran, lowongan);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> lowonganService.terimaPendaftar(id1, id2)
        );
        assertEquals("Lowongan sudah penuh", ex.getMessage());

        verify(validator).validatePendaftaranAndLowongan(id1, id2, "dosen@example.com");
        verify(validator).validateStatusAndCapacity(pendaftaran, lowongan);
        verifyNoInteractions(pendaftaranRepository, lowonganRepository, eventPublisher);
    }




    @Test
    void testTolakPendaftarPendaftaranNotFound() {
        when(validator.validatePendaftaranAndLowongan(id1, id2, "dosen@example.com"))
                .thenThrow(new IllegalArgumentException("Pendaftaran tidak ditemukan"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> lowonganService.tolakPendaftar(id1, id2)
        );
        assertEquals("Pendaftaran tidak ditemukan", ex.getMessage());

        verify(validator).validatePendaftaranAndLowongan(id1, id2, "dosen@example.com");
        verifyNoInteractions(pendaftaranRepository, lowonganRepository, eventPublisher);
    }




    @Test
    void testTolakPendaftarLowonganNotFound() {
        when(validator.validatePendaftaranAndLowongan(id1, id2,"dosen@example.com"))
                .thenThrow(new IllegalArgumentException("Lowongan tidak ditemukan"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> lowonganService.tolakPendaftar(id1, id2)
        );
        assertEquals("Lowongan tidak ditemukan", ex.getMessage());

        verify(validator).validatePendaftaranAndLowongan(id1, id2, "dosen@example.com");
        verifyNoInteractions(pendaftaranRepository, lowonganRepository, eventPublisher);
    }



    @Test
    void testTolakPendaftarIdMismatch() {
        when(validator.validatePendaftaranAndLowongan( id2, id1, "dosen@example.com"
        )).thenThrow(new IllegalArgumentException("Pendaftaran tidak sesuai dengan lowongan"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> lowonganService.tolakPendaftar(id2, id1)
        );
        assertEquals("Pendaftaran tidak sesuai dengan lowongan", ex.getMessage());

        verify(validator).validatePendaftaranAndLowongan(id2, id1, "dosen@example.com");
        verifyNoInteractions(pendaftaranRepository, lowonganRepository, eventPublisher);
    }


    @Test
    void testTolakPendaftarSuccess() {
        // Arrange
        Lowongan lowongan = createLowongan(id1, mataKuliah, 2, 1);
        Pendaftaran pendaftaran = createPendaftaran(id2, lowongan, StatusPendaftaran.BELUM_DIPROSES);

        when(validator.validatePendaftaranAndLowongan(id1, id2, "dosen@example.com"))
                .thenReturn(Pair.of(pendaftaran, lowongan));
        doNothing().when(validator).validateStatusAndCapacity(pendaftaran, lowongan);
        when(pendaftaranRepository.save(any(Pendaftaran.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        lowonganService.tolakPendaftar(id1, id2);

        assertEquals(StatusPendaftaran.DITOLAK, pendaftaran.getStatus());
        verify(validator).validatePendaftaranAndLowongan(id1, id2, "dosen@example.com");
        verify(validator).validateStatusAndCapacity(pendaftaran, lowongan);
        verify(pendaftaranRepository).save(pendaftaran);
        // Karena eventPublisher adalah mock, publishEvent() hanya no-op:
        verify(eventPublisher, never()).publishEvent(any());
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

        when(lowonganRepository.findById(lowonganId))
                .thenReturn(Optional.of(lowongan));

        doThrow(new IllegalStateException("Kuota lowongan sudah penuh!"))
                .when(validator).ensureQuotaAvailable(lowongan);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> lowonganService.registerLowongan(lowonganId, "candidate1")
        );
        assertEquals("Kuota lowongan sudah penuh!", ex.getMessage());

        verify(validator).ensureQuotaAvailable(lowongan);
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
        mataKuliah = new MataKuliah(kode, namaSingkat, deskripsi);
        mataKuliah.addDosenPengampu(dosenPengampu);
        return mataKuliah;
    }
}