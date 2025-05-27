package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.util.Pair;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LowonganServiceValidatorTest {

    @Mock
    private LowonganRepository lowonganRepository;

    @Mock
    private PendaftaranRepository pendaftaranRepository;

    @InjectMocks
    private LowonganServiceValidator validator;

    private UUID lowonganId;
    private UUID pendaftaranId;
    private Dosen dosen;
    private MataKuliah mataKuliah;
    private Lowongan lowongan;
    private Pendaftaran pendaftaran;

    @BeforeEach
    void setUp() {
        lowonganId = UUID.randomUUID();
        pendaftaranId = UUID.randomUUID();
        dosen = new Dosen();
        dosen.setUsername("dosen@example.com");
        dosen.setPassword("password");
        dosen.setNip("2306214990");

        mataKuliah = new MataKuliah("CS100", "AdvProg", "desc");
        mataKuliah.addDosenPengampu(dosen);

        lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setJumlahAsdosPendaftar(0);
        lowongan.setJumlahAsdosDibutuhkan(2);
        lowongan.setJumlahAsdosDiterima(0);

        pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(pendaftaranId);
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);

        // mock security context
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("dosen@example.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testEnsureQuotaAvailable_whenFull_throwsIllegalState() {
        lowongan.setJumlahAsdosPendaftar(2);
        lowongan.setJumlahAsdosDibutuhkan(2);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> validator.ensureQuotaAvailable(lowongan));
        assertEquals("Kuota lowongan sudah penuh!", ex.getMessage());
    }

    @Test
    void testValidatePendaftaranAndLowongan_success_returnsPair() {
        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));

        Pair<Pendaftaran, Lowongan> result = validator.validatePendaftaranAndLowongan(
                lowonganId, pendaftaranId, "dosen@example.com");

        assertEquals(pendaftaran, result.getFirst());
        assertEquals(lowongan, result.getSecond());
    }

    @Test
    void testValidatePendaftaranAndLowongan_pendaftaranNotFound_throwsIllegalArgument() {
        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validatePendaftaranAndLowongan(lowonganId, pendaftaranId, "dosen@example.com"));
        assertEquals("Pendaftaran tidak ditemukan", ex.getMessage());
    }

    @Test
    void testValidatePendaftaranAndLowongan_lowonganNotFound_throwsIllegalArgument() {
        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaran));
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validatePendaftaranAndLowongan(lowonganId, pendaftaranId, "dosen@example.com"));
        assertEquals("Lowongan tidak ditemukan", ex.getMessage());
    }

    @Test
    void testValidatePendaftaranAndLowongan_mismatch_throwsIllegalArgument() {
        // create a pendaftaran linked to a different lowongan id
        UUID otherLowonganId = UUID.randomUUID();
        Lowongan otherLowongan = new Lowongan();
        otherLowongan.setLowonganId(otherLowonganId);
        otherLowongan.setMataKuliah(mataKuliah);
        otherLowongan.setJumlahAsdosPendaftar(0);
        otherLowongan.setJumlahAsdosDibutuhkan(2);
        otherLowongan.setJumlahAsdosDiterima(0);

        Pendaftaran other = new Pendaftaran();
        other.setPendaftaranId(pendaftaranId);
        other.setLowongan(otherLowongan);
        other.setStatus(StatusPendaftaran.BELUM_DIPROSES);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(other));
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validatePendaftaranAndLowongan(lowonganId, pendaftaranId, "dosen@example.com"));
        assertEquals("Pendaftaran tidak sesuai dengan lowongan", ex.getMessage());
    }

    @Test
    void testValidatePendaftaranAndLowongan_notAuthorized_throwsAccessDenied() {
        MataKuliah mkNoDosen = new MataKuliah("CS200", "Other", "desc");
        Lowongan lowonganNoDosen = new Lowongan();
        lowonganNoDosen.setLowonganId(lowonganId);
        lowonganNoDosen.setMataKuliah(mkNoDosen);
        lowonganNoDosen.setJumlahAsdosDibutuhkan(2);
        lowonganNoDosen.setJumlahAsdosPendaftar(0);
        lowonganNoDosen.setJumlahAsdosDiterima(0);

        Pendaftaran pendaftaranNoDosen = new Pendaftaran();
        pendaftaranNoDosen.setPendaftaranId(pendaftaranId);
        pendaftaranNoDosen.setLowongan(lowonganNoDosen);
        pendaftaranNoDosen.setStatus(StatusPendaftaran.BELUM_DIPROSES);

        when(pendaftaranRepository.findById(pendaftaranId)).thenReturn(Optional.of(pendaftaranNoDosen));
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowonganNoDosen));

        assertThrows(AccessDeniedException.class,
                () -> validator.validatePendaftaranAndLowongan(lowonganId, pendaftaranId, "dosen@example.com"));
    }

    @Test
    void testValidateStatusAndCapacity_processedOrFull_throwsIllegalState() {
        // kasus sudah diproses
        pendaftaran.setStatus(StatusPendaftaran.DITERIMA);
        IllegalStateException ex1 = assertThrows(IllegalStateException.class,
                () -> validator.validateStatusAndCapacity(pendaftaran, lowongan));
        assertTrue(ex1.getMessage().contains("sudah diterima") || ex1.getMessage().contains("sudah ditolak"));

        // reset status ke belum diproses untuk menguji kasus full
        pendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);
        lowongan.setJumlahAsdosDiterima(2);
        lowongan.setJumlahAsdosDibutuhkan(2);
        IllegalStateException ex2 = assertThrows(IllegalStateException.class,
                () -> validator.validateStatusAndCapacity(pendaftaran, lowongan));
        assertEquals("Lowongan sudah penuh", ex2.getMessage());
    }

    @Test
    void testGetAuthorizedLowongan_notFoundOrForbidden() {
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> validator.getAuthorizedLowongan(lowonganId));

        MataKuliah mkNoDosen = new MataKuliah("CS200", "Other", "desc");
        Lowongan lowonganNoDosen = new Lowongan();
        lowonganNoDosen.setLowonganId(lowonganId);
        lowonganNoDosen.setMataKuliah(mkNoDosen);
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowonganNoDosen));

        assertThrows(AccessDeniedException.class,
                () -> validator.getAuthorizedLowongan(lowonganId));
    }

    @Test
    void testGetAuthorizedLowongan_success() {
        when(lowonganRepository.findById(lowonganId)).thenReturn(Optional.of(lowongan));
        Lowongan result = validator.getAuthorizedLowongan(lowonganId);
        assertEquals(lowongan, result);
    }

    @Test
    void testValidateLowonganCombinationIsUnique_duplicateOrUnique() {
        Lowongan inputLowongan = new Lowongan();
        inputLowongan.setLowonganId(UUID.randomUUID());
        inputLowongan.setMataKuliah(mataKuliah);
        inputLowongan.setSemester("Ganjil");
        inputLowongan.setTahunAjaran("2024/2025");

        Lowongan existingLowongan = new Lowongan();
        existingLowongan.setLowonganId(UUID.randomUUID());
        existingLowongan.setMataKuliah(inputLowongan.getMataKuliah());
        existingLowongan.setSemester(inputLowongan.getSemester().getValue());
        existingLowongan.setTahunAjaran(inputLowongan.getTahunAjaran());

        when(lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                any(), any(), any()))
                .thenReturn(Optional.of(existingLowongan));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateLowonganCombinationIsUnique(inputLowongan));
        assertEquals("Lowongan dengan kombinasi yang sama sudah ada.", ex.getMessage());

        when(lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                any(), any(), any()))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validateLowonganCombinationIsUnique(inputLowongan));
    }


    @Test
    void testValidateStatusAndCapacity_pendaftaranDitolak_throwsIllegalState() {
        // Test case for DITOLAK status
        pendaftaran.setStatus(StatusPendaftaran.DITOLAK);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> validator.validateStatusAndCapacity(pendaftaran, lowongan));
        assertEquals("Pendaftar ini sudah ditolak", ex.getMessage());
    }

    @Test
    void testValidateStatusAndCapacity_validStatus_noException() {
        // Test case for valid status (BELUM_DIPROSES) with available capacity
        pendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);
        lowongan.setJumlahAsdosDiterima(1);
        lowongan.setJumlahAsdosDibutuhkan(2);

        // Should not throw any exception
        assertDoesNotThrow(() -> validator.validateStatusAndCapacity(pendaftaran, lowongan));
    }

    @Test
    void testValidateStatusAndCapacity_exactCapacity_throwsIllegalState() {
        // Test case where capacity is exactly equal (edge case)
        pendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);
        lowongan.setJumlahAsdosDiterima(2);
        lowongan.setJumlahAsdosDibutuhkan(2);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> validator.validateStatusAndCapacity(pendaftaran, lowongan));
        assertEquals("Lowongan sudah penuh", ex.getMessage());
    }

    @Test
    void testValidateLowonganCombinationIsUnique_lowonganIdNull_throwsIllegalArgument() {
        Lowongan existingLowongan = new Lowongan();
        existingLowongan.setLowonganId(UUID.randomUUID());
        existingLowongan.setMataKuliah(mataKuliah);
        existingLowongan.setSemester(Semester.GENAP.getValue());
        existingLowongan.setTahunAjaran("2023/2024");

        lowongan.setLowonganId(null);
        lowongan.setSemester(Semester.GENAP.getValue());
        lowongan.setTahunAjaran("2023/2024");

        when(lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                mataKuliah, Semester.valueOf("GENAP"), "2023/2024"))
                .thenReturn(Optional.of(existingLowongan));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateLowonganCombinationIsUnique(lowongan));
        assertEquals("Lowongan dengan kombinasi yang sama sudah ada.", ex.getMessage());
    }

    @Test
    void testValidateLowonganCombinationIsUnique_existingWithDifferentId_throwsIllegalArgument() {
        UUID existingId = UUID.randomUUID();
        Lowongan existingLowongan = new Lowongan();
        existingLowongan.setLowonganId(existingId);

        lowongan.setMataKuliah(mataKuliah);
        lowongan.setSemester(Semester.GENAP.getValue());
        lowongan.setTahunAjaran("2024/2025");
        lowongan.setLowonganId(UUID.randomUUID());

        existingLowongan.setMataKuliah(lowongan.getMataKuliah());
        existingLowongan.setSemester(Semester.GENAP.getValue());
        existingLowongan.setTahunAjaran(lowongan.getTahunAjaran());

        when(lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                any(), any(), any()))
                .thenReturn(Optional.of(existingLowongan));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validateLowonganCombinationIsUnique(lowongan));
        assertEquals("Lowongan dengan kombinasi yang sama sudah ada.", ex.getMessage());
    }

    @Test
    void testValidateLowonganCombinationIsUnique_sameId_doesNotThrow() {
        UUID sameId = UUID.randomUUID();
        lowongan.setLowonganId(sameId);
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setSemester(Semester.GENAP.getValue());
        lowongan.setTahunAjaran("2024/2025");

        Lowongan existingLowongan = new Lowongan();
        existingLowongan.setLowonganId(sameId);
        existingLowongan.setMataKuliah(lowongan.getMataKuliah());
        existingLowongan.setSemester(Semester.GENAP.getValue());
        existingLowongan.setTahunAjaran(lowongan.getTahunAjaran());

        when(lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                lowongan.getMataKuliah(),
                lowongan.getSemester(),
                lowongan.getTahunAjaran()))
                .thenReturn(Optional.of(existingLowongan));

        assertDoesNotThrow(() ->
                validator.validateLowonganCombinationIsUnique(lowongan)
        );
    }
}
