package id.ac.ui.cs.advprog.hiringgo.dashboard.dto;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganResponse;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DosenDashboardResponseTest {

    @Test
    void gettersAndSettersInheritedAndOwn() {
        DosenDashboardResponse dto = new DosenDashboardResponse();

        // Inherited fields
        dto.setUserRole("DOSEN");
        assertEquals("DOSEN", dto.getUserRole());

        dto.setUsername("dosen@example.com");
        assertEquals("dosen@example.com", dto.getUsername());

        dto.setFullName("Dr. Dosen");
        assertEquals("Dr. Dosen", dto.getFullName());

        Map<String, String> features = new HashMap<>();
        features.put("viewCourses", "/api/dosen/courses");
        dto.setAvailableFeatures(features);
        assertSame(features, dto.getAvailableFeatures());

        // Own fields
        dto.setCourseCount(4);
        assertEquals(4, dto.getCourseCount());

        dto.setAcceptedAssistantCount(2);
        assertEquals(2, dto.getAcceptedAssistantCount());

        dto.setOpenPositionCount(1);
        assertEquals(1, dto.getOpenPositionCount());

        // Courses list
        MataKuliahDTO m1 = mock(MataKuliahDTO.class);
        MataKuliahDTO m2 = mock(MataKuliahDTO.class);
        List<MataKuliahDTO> courses = Arrays.asList(m1, m2);
        dto.setCourses(courses);
        assertSame(courses, dto.getCourses());

        // OpenPositions list
        LowonganResponse p1 = mock(LowonganResponse.class);
        LowonganResponse p2 = mock(LowonganResponse.class);
        List<LowonganResponse> positions = Arrays.asList(p1, p2);
        dto.setOpenPositions(positions);
        assertSame(positions, dto.getOpenPositions());
    }
}
