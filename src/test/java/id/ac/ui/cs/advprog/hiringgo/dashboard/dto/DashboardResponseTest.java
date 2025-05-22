package id.ac.ui.cs.advprog.hiringgo.dashboard.dto;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DosenDashboardResponseTest {

    @Test
    @DisplayName("Test inherited and own fields with getters and setters")
    void testGettersAndSettersForAllFields() {
        DosenDashboardResponse response = new DosenDashboardResponse();

        response.setUserRole("DOSEN");
        response.setUsername("dosen@example.com");
        response.setFullName("Dr. John Smith");

        Map<String, String> features = new HashMap<>();
        features.put("manajemenlowongan", "/api/manajemenlowongan");
        features.put("manajemenAsdos", "/api/asdos");
        features.put("profile", "/api/profile");
        features.put("periksaLog", "/api/log");
        response.setAvailableFeatures(features);

        response.setCourseCount(5);
        response.setAcceptedAssistantCount(8);
        response.setOpenPositionCount(3);


        List<MataKuliahDTO> courses = new ArrayList<>();
        courses.add(mock(MataKuliahDTO.class));
        courses.add(mock(MataKuliahDTO.class));
        response.setCourses(courses);

        List<LowonganDTO> openPositions = new ArrayList<>();
        openPositions.add(mock(LowonganDTO.class));
        response.setOpenPositions(openPositions);

        List<MataKuliahDTO> coursesTaught = new ArrayList<>();
        coursesTaught.add(mock(MataKuliahDTO.class));
        coursesTaught.add(mock(MataKuliahDTO.class));
        coursesTaught.add(mock(MataKuliahDTO.class));
        response.setCoursesTaught(coursesTaught);


        Map<String, List<LowonganDTO>> lowonganPerCourse = new HashMap<>();
        lowonganPerCourse.put("CS101", Arrays.asList(mock(LowonganDTO.class)));
        lowonganPerCourse.put("CS102", Arrays.asList(mock(LowonganDTO.class), mock(LowonganDTO.class)));
        response.setLowonganPerCourse(lowonganPerCourse);

        Map<String, Integer> acceptedAssistantsPerCourse = new HashMap<>();
        acceptedAssistantsPerCourse.put("CS101", 2);
        acceptedAssistantsPerCourse.put("CS102", 3);
        acceptedAssistantsPerCourse.put("CS103", 1);
        response.setAcceptedAssistantsPerCourse(acceptedAssistantsPerCourse);


        assertEquals("DOSEN", response.getUserRole());
        assertEquals("dosen@example.com", response.getUsername());
        assertEquals("Dr. John Smith", response.getFullName());
        assertEquals(4, response.getAvailableFeatures().size());
        assertEquals("/api/manajemenlowongan", response.getAvailableFeatures().get("manajemenlowongan"));
        assertEquals("/api/asdos", response.getAvailableFeatures().get("manajemenAsdos"));
        assertEquals("/api/profile", response.getAvailableFeatures().get("profile"));
        assertEquals("/api/log", response.getAvailableFeatures().get("periksaLog"));

        assertEquals(5, response.getCourseCount());
        assertEquals(8, response.getAcceptedAssistantCount());
        assertEquals(3, response.getOpenPositionCount());

        assertEquals(2, response.getCourses().size());
        assertEquals(1, response.getOpenPositions().size());
        assertEquals(3, response.getCoursesTaught().size());

        assertEquals(2, response.getLowonganPerCourse().size());
        assertEquals(1, response.getLowonganPerCourse().get("CS101").size());
        assertEquals(2, response.getLowonganPerCourse().get("CS102").size());

        assertEquals(3, response.getAcceptedAssistantsPerCourse().size());
        assertEquals(Integer.valueOf(2), response.getAcceptedAssistantsPerCourse().get("CS101"));
        assertEquals(Integer.valueOf(3), response.getAcceptedAssistantsPerCourse().get("CS102"));
        assertEquals(Integer.valueOf(1), response.getAcceptedAssistantsPerCourse().get("CS103"));
    }

    @Test
    @DisplayName("Test collection immutability")
    void testCollectionImmutability() {
        DosenDashboardResponse response = new DosenDashboardResponse();

        List<MataKuliahDTO> coursesTaught = new ArrayList<>();
        coursesTaught.add(mock(MataKuliahDTO.class));
        response.setCoursesTaught(coursesTaught);

        Map<String, List<LowonganDTO>> lowonganPerCourse = new HashMap<>();
        lowonganPerCourse.put("CS101", new ArrayList<>());
        response.setLowonganPerCourse(lowonganPerCourse);

        Map<String, Integer> acceptedAssistantsPerCourse = new HashMap<>();
        acceptedAssistantsPerCourse.put("CS101", 2);
        response.setAcceptedAssistantsPerCourse(acceptedAssistantsPerCourse);

        List<MataKuliahDTO> returnedCoursesTaught = response.getCoursesTaught();
        assertEquals(1, returnedCoursesTaught.size());
        returnedCoursesTaught.add(mock(MataKuliahDTO.class));
        assertEquals(2, response.getCoursesTaught().size(), "Changes to returned list should affect internal state");

        Map<String, List<LowonganDTO>> returnedLowonganPerCourse = response.getLowonganPerCourse();
        returnedLowonganPerCourse.put("CS102", new ArrayList<>());
        assertEquals(2, response.getLowonganPerCourse().size(), "Changes to returned map should affect internal state");

        Map<String, Integer> returnedAcceptedAssistantsPerCourse = response.getAcceptedAssistantsPerCourse();
        returnedAcceptedAssistantsPerCourse.put("CS102", 3);
        assertEquals(2, response.getAcceptedAssistantsPerCourse().size(), "Changes to returned map should affect internal state");
    }
}