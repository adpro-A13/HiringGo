package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.Disabled;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//
//@Disabled("Temporarily disabled due to context loading issues")
//tar remove dl
@SpringBootTest
public class LowonganRegistrationTest {

    @Autowired
    private LowonganService lowonganService;

    private LowonganRepository lowonganRepository;
    // Helper: Creates and saves a Lowongan with the specified quota
    private Lowongan createTestLowongan(int quota) {
        Lowongan lowongan = new Lowongan();
        // minimal setup
        MataKuliah mataKuliah = new MataKuliah("CS100", "Advpro", "advanced programming");
        Dosen dosen = new Dosen("dosen@gmail.com", "dosen123", "dosen", "12345678");
        mataKuliah.addDosenPengampu(dosen);
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setTahunAjaran("2023");
        lowongan.setSemester(String.valueOf(Semester.GANJIL));
        lowongan.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        lowongan.setJumlahAsdosDibutuhkan(quota);
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);

        // We assume we have a create method in LowonganService
        return lowonganService.createLowongan(lowongan);
    }

    @Transactional
    @Test
    public void testSuccessfulRegistration() {
        Authentication dosenAuth = mock(Authentication.class);
        when(dosenAuth.getName()).thenReturn("dosen@gmail.com");
        SecurityContext dosenContext = mock(SecurityContext.class);
        when(dosenContext.getAuthentication()).thenReturn(dosenAuth);
        SecurityContextHolder.setContext(dosenContext);

        Lowongan lowongan = createTestLowongan(2);
        UUID lowonganId = lowongan.getLowonganId();

        Authentication candidateAuth = mock(Authentication.class);
        when(candidateAuth.getName()).thenReturn("candidate001");
        SecurityContext candidateContext = mock(SecurityContext.class);
        when(candidateContext.getAuthentication()).thenReturn(candidateAuth);
        SecurityContextHolder.setContext(candidateContext);

        // Act: Register candidate
        lowonganService.registerLowongan(lowonganId, "candidate001");

        // Assert: Pendaftar count should increase by 1
        Lowongan updated = lowonganService.findById(lowonganId);
        assertEquals(1, updated.getJumlahAsdosPendaftar(),
                "Jumlah pendaftar should increment by 1 after successful registration");
    }


    @Transactional
    @Test
    public void testRegistrationFailsWhenQuotaExceeded() {
        Authentication authDosen = mock(Authentication.class);
        when(authDosen.getName()).thenReturn("dosen@gmail.com");
        SecurityContext contextDosen = mock(SecurityContext.class);
        when(contextDosen.getAuthentication()).thenReturn(authDosen);
        SecurityContextHolder.setContext(contextDosen);

        Lowongan lowongan = createTestLowongan(1);
        UUID lowonganId = lowongan.getLowonganId();

        Authentication authCandidate1 = mock(Authentication.class);
        when(authCandidate1.getName()).thenReturn("candidate1");
        SecurityContext context1 = mock(SecurityContext.class);
        when(context1.getAuthentication()).thenReturn(authCandidate1);
        SecurityContextHolder.setContext(context1);

        lowonganService.registerLowongan(lowonganId, "candidate1");

        Authentication authCandidate2 = mock(Authentication.class);
        when(authCandidate2.getName()).thenReturn("candidate2");
        SecurityContext context2 = mock(SecurityContext.class);
        when(context2.getAuthentication()).thenReturn(authCandidate2);
        SecurityContextHolder.setContext(context2);

        assertThrows(IllegalStateException.class, () -> {
            lowonganService.registerLowongan(lowonganId, "candidate2");
        });
    }



    @Transactional
    @Test
    public void testReadLowonganDetailsAfterRegistration() {
        // Setup: Mock authentication sebagai dosen pengampu
        Authentication authDosen = mock(Authentication.class);
        when(authDosen.getName()).thenReturn("dosen@gmail.com"); // sama dengan Dosen di createTestLowongan
        SecurityContext contextDosen = mock(SecurityContext.class);
        when(contextDosen.getAuthentication()).thenReturn(authDosen);
        SecurityContextHolder.setContext(contextDosen);

        // Arrange: Create a lowongan with quota 2
        Lowongan lowongan = createTestLowongan(2);
        UUID lowonganId = lowongan.getLowonganId();

        // Setup: Authentication sebagai candidate1
        Authentication authCandidate = mock(Authentication.class);
        when(authCandidate.getName()).thenReturn("candidate1");
        SecurityContext contextCandidate = mock(SecurityContext.class);
        when(contextCandidate.getAuthentication()).thenReturn(authCandidate);
        SecurityContextHolder.setContext(contextCandidate);

        // Act: Register a candidate
        lowonganService.registerLowongan(lowonganId, "candidate1");

        // Assert: The retrieved lowongan should reflect the updated registration count
        Lowongan retrieved = lowonganService.findById(lowonganId);
        assertEquals(1, retrieved.getJumlahAsdosPendaftar());
    }
}
