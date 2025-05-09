package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDetailResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LowonganRestControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private LowonganService lowonganService;

    @InjectMocks
    private LowonganRestController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    @DisplayName("GET /api/lowongan - Success")
    void testGetAllLowongan() throws Exception {
        // Create a fully populated test lowongan
        UUID id = UUID.randomUUID();
        Lowongan lowongan = createTestLowongan(id);

        when(lowonganService.findAll()).thenReturn(Collections.singletonList(lowongan));

        mockMvc.perform(get("/api/lowongan")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lowonganId", is(id.toString())));
    }

    @Test
    @DisplayName("GET /api/lowongan/{id} - Success")
    void testGetLowonganByIdSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Lowongan lowongan = createTestLowongan(id);

        when(lowonganService.findById(eq(id))).thenReturn(lowongan);

        mockMvc.perform(get("/api/lowongan/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowonganId", is(id.toString())));
    }

    @Test
    @DisplayName("GET /api/lowongan/{id} - Not Found")
    void testGetLowonganByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(lowonganService.findById(eq(id))).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/lowongan/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/lowongan/enums/semester - Success")
    void testGetAllSemesters() throws Exception {
        mockMvc.perform(get("/api/lowongan/enums/semester"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Semester.values().length)));
    }

    @Test
    @DisplayName("GET /api/lowongan/enums/status - Success")
    void testGetAllStatuses() throws Exception {
        mockMvc.perform(get("/api/lowongan/enums/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(StatusLowongan.values().length)));
    }

    // Helper method to create a fully populated test entity
    private Lowongan createTestLowongan(UUID id) {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(id);
        lowongan.setIdMataKuliah("Test Course");
        lowongan.setSemester(Semester.GANJIL.getValue());
        lowongan.setTahunAjaran("2024/2025");
        lowongan.setStatusLowongan(StatusLowongan.DIBUKA.getValue());
        lowongan.setJumlahAsdosDibutuhkan(3);
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);
        return lowongan;
    }
}