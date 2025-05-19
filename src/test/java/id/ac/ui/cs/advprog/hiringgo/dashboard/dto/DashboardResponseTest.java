package id.ac.ui.cs.advprog.hiringgo.dashboard.dto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DashboardResponseTest {

    @Test
    void testGettersAndSetters() {
        DashboardResponse dto = new DashboardResponse();

        // Test userRole
        dto.setUserRole("ADMIN");
        assertEquals("ADMIN", dto.getUserRole(), "getUserRole should return value set by setUserRole");

        // Test username
        dto.setUsername("user@example.com");
        assertEquals("user@example.com", dto.getUsername(), "getUsername should return value set by setUsername");

        // Test fullName
        dto.setFullName("John Doe");
        assertEquals("John Doe", dto.getFullName(), "getFullName should return value set by setFullName");

        // Test availableFeatures map
        Map<String, String> features = new HashMap<>();
        features.put("dashboard", "/api/dashboard");
        features.put("profile", "/api/profile");
        dto.setAvailableFeatures(features);

        Map<String, String> returned = dto.getAvailableFeatures();
        assertNotNull(returned, "getAvailableFeatures should not return null after setting a map");
        assertEquals(2, returned.size(), "Map size should match the number of entries inserted");
        assertEquals("/api/dashboard", returned.get("dashboard"), "Map should contain the dashboard entry");
        assertEquals("/api/profile", returned.get("profile"), "Map should contain the profile entry");

        // Changing the returned map should affect the original (since it's the same instance)
        returned.put("settings", "/api/settings");
        assertTrue(dto.getAvailableFeatures().containsKey("settings"), "Modifying returned map should reflect in dto.getAvailableFeatures");
    }
}
