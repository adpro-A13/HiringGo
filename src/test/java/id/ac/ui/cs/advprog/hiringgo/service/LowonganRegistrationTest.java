package id.ac.ui.cs.advprog.hiringgo.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LowonganRegistrationTest {

    @Autowired
    private LowonganService lowonganService;

    // Helper: Creates and saves a Lowongan with the specified quota
    private Lowongan createTestLowongan(int quota) {
        Lowongan lowongan = new Lowongan();
        // minimal setup
        lowongan.setIdMataKuliah("MK101");
        lowongan.setTahunAjaran("2023/2024");
        lowongan.setSemester(String.valueOf(Semester.GANJIL));
        lowongan.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        lowongan.setJumlahAsdosDibutuhkan(quota);
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);

        // We assume we have a create method in LowonganService
        return lowonganService.createLowongan(lowongan);
    }

    @Test
    public void testSuccessfulRegistration() {
        // Arrange
        Lowongan lowongan = createTestLowongan(2); // Quota = 2
        UUID lowonganId = lowongan.getLowonganId();

        // Act
        // We'll create a method like registerLowongan(UUID lowonganId, String candidateId)
        lowonganService.registerLowongan(lowonganId, "candidate001");

        // Assert
        Lowongan updated = lowonganService.findById(lowonganId);
        assertEquals(1, updated.getJumlahAsdosPendaftar(),
                "Jumlah pendaftar should increment by 1 after successful registration");
    }

    @Test
    public void testRegistrationFailsWhenQuotaExceeded() {
        // Arrange: Create a lowongan with quota 1
        Lowongan lowongan = createTestLowongan(1);
        UUID lowonganId = lowongan.getLowonganId();

        // Act: Register the first candidate (should work)
        lowonganService.registerLowongan(lowonganId, "candidate1");

        // Assert: Attempting to register a second candidate should throw an exception
        assertThrows(IllegalStateException.class, () -> {
            lowonganService.registerLowongan(lowonganId, "candidate2");
        });
    }

    @Test
    public void testReadLowonganDetailsAfterRegistration() {
        // Arrange: Create a lowongan with quota 2
        Lowongan lowongan = createTestLowongan(2);
        UUID lowonganId = lowongan.getLowonganId();

        // Act: Register a candidate
        lowonganService.registerLowongan(lowonganId, "candidate1");

        // Assert: The retrieved lowongan should reflect the updated registration count
        Lowongan retrieved = lowonganService.findById(lowonganId);
        assertEquals(1, retrieved.getJumlahAsdosPendaftar());
    }


}
