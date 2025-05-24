package id.ac.ui.cs.advprog.hiringgo.dashboard.dto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdminDashboardResponseTest {

    @Test
    void testGettersAndSetters_InheritedAndNewFields() {
        AdminDashboardResponse dto = new AdminDashboardResponse();

        // Inherited fields
        dto.setUserRole("ADMIN");
        assertEquals("ADMIN", dto.getUserRole(), "Inherited getUserRole should return value set by setUserRole");

        dto.setUsername("admin@example.com");
        assertEquals("admin@example.com", dto.getUsername(), "Inherited getUsername should return value set by setUsername");

        dto.setFullName("Administrator");
        assertEquals("Administrator", dto.getFullName(), "Inherited getFullName should return value set by setFullName");

        Map<String, String> features = new HashMap<>();
        features.put("manageUsers", "/api/admin/users");
        features.put("viewReports", "/api/admin/reports");
        dto.setAvailableFeatures(features);

        Map<String, String> returnedFeatures = dto.getAvailableFeatures();
        assertNotNull(returnedFeatures, "Inherited getAvailableFeatures should not return null after setting a map");
        assertEquals(2, returnedFeatures.size(), "Map size should match the number of entries inserted");
        assertEquals("/api/admin/users", returnedFeatures.get("manageUsers"));
        assertEquals("/api/admin/reports", returnedFeatures.get("viewReports"));

        // New fields specific to AdminDashboardResponse
        dto.setDosenCount(5);
        assertEquals(5, dto.getDosenCount(), "getDosenCount should return value set by setDosenCount");

        dto.setMahasiswaCount(120);
        assertEquals(120, dto.getMahasiswaCount(), "getMahasiswaCount should return value set by setMahasiswaCount");

        dto.setCourseCount(8);
        assertEquals(8, dto.getCourseCount(), "getCourseCount should return value set by setCourseCount");

        dto.setLowonganCount(20);
        assertEquals(20, dto.getLowonganCount(), "getLowonganCount should return value set by setLowonganCount");
    }
}