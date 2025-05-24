package id.ac.ui.cs.advprog.hiringgo.dashboard.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MahasiswaDashboardResponseTest {

    @Test
    void gettersAndSettersInheritedAndOwn() {
        MahasiswaDashboardResponse dto = new MahasiswaDashboardResponse();

        // Inherited fields
        dto.setUserRole("MAHASISWA");
        assertEquals("MAHASISWA", dto.getUserRole());

        dto.setUsername("mahasiswa@example.com");
        assertEquals("mahasiswa@example.com", dto.getUsername());

        dto.setFullName("Student Name");
        assertEquals("Student Name", dto.getFullName());

        Map<String, String> features = new HashMap<>();
        features.put("applyLowongan", "/api/pendaftaran");
        dto.setAvailableFeatures(features);
        assertSame(features, dto.getAvailableFeatures());

        // Own fields: integer counts
        dto.setOpenLowonganCount(3);
        assertEquals(3, dto.getOpenLowonganCount());

        dto.setTotalLowonganCount(10);
        assertEquals(10, dto.getTotalLowonganCount());

        dto.setTotalApplicationsCount(5);
        assertEquals(5, dto.getTotalApplicationsCount());

        dto.setPendingApplicationsCount(2);
        assertEquals(2, dto.getPendingApplicationsCount());

        dto.setAcceptedApplicationsCount(1);
        assertEquals(1, dto.getAcceptedApplicationsCount());

        dto.setRejectedApplicationsCount(2);
        assertEquals(2, dto.getRejectedApplicationsCount());

        // Fix: Use BigDecimal instead of int
        BigDecimal hours = new BigDecimal("40.00");
        dto.setTotalLoggedHours(hours);
        assertEquals(hours, dto.getTotalLoggedHours());

        BigDecimal incentive = new BigDecimal("123.45");
        dto.setTotalIncentive(incentive);
        assertEquals(incentive, dto.getTotalIncentive());

        // List fields
        LowonganDTO acc1 = mock(LowonganDTO.class);
        LowonganDTO acc2 = mock(LowonganDTO.class);
        List<LowonganDTO> accepted = Arrays.asList(acc1, acc2);
        dto.setAcceptedLowongan(accepted);
        assertSame(accepted, dto.getAcceptedLowongan());

    }
}
