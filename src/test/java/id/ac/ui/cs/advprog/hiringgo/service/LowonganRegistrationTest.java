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
        lowongan.setSemester(Semester.GANJIL);
        lowongan.setStatusLowongan(StatusLowongan.DIBUKA);
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
}
