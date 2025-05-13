package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDetailResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LowonganControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LowonganService lowonganService;

    @InjectMocks
    private LowonganController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    @DisplayName("GET /api/lowongan - Success tanpa filter")
    void testGetAllLowonganWithoutFilter() throws Exception {
        UUID id = UUID.randomUUID();
        Lowongan lowongan = createTestLowongan(id);
        when(lowonganService.findAll()).thenReturn(List.of(lowongan));

        mockMvc.perform(get("/api/lowongan")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lowonganId", is(id.toString())));
    }

    @Test
    @DisplayName("GET /api/lowongan dengan filter semester dan status - Success")
    void testGetAllLowonganWithFilters() throws Exception {
        UUID id = UUID.randomUUID();
        Lowongan lowongan = createTestLowongan(id);
        when(lowonganService.findAll()).thenReturn(List.of(lowongan));

        mockMvc.perform(get("/api/lowongan")
                        .param("semester", "GENAP")
                        .param("status", "DIBUKA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lowonganId", is(id.toString())));
    }

    @Test
    @DisplayName("GET /api/lowongan/{id} - Success")
    void testGetLowonganByIdSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Lowongan lowongan = createTestLowongan(id);
        when(lowonganService.findById(id)).thenReturn(lowongan);

        mockMvc.perform(get("/api/lowongan/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowonganId", is(id.toString())));
    }

    @Test
    @DisplayName("GET /api/lowongan/{id} - Not Found")
    void testGetLowonganByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(lowonganService.findById(id)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/lowongan/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("tidak ditemukan")));
    }

    @Test
    @DisplayName("GET /api/lowongan/enums/semester - Success")
    void testGetAllSemesters() throws Exception {
        mockMvc.perform(get("/api/lowongan/enums/semester"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Semester.values().length)))
                .andExpect(jsonPath("$[0]", is(Semester.values()[0].name())));
    }

    @Test
    @DisplayName("GET /api/lowongan/enums/status - Success")
    void testGetAllStatuses() throws Exception {
        mockMvc.perform(get("/api/lowongan/enums/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(StatusLowongan.values().length)))
                .andExpect(jsonPath("$[0]", is(StatusLowongan.values()[0].name())));
    }

    @Test
    @DisplayName("POST /api/lowongan - Success")
    void testCreateLowonganSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Lowongan inputLowongan = createTestLowongan(null);
        Lowongan createdLowongan = createTestLowongan(id);

        when(lowonganService.createLowongan(any())).thenReturn(createdLowongan);

        mockMvc.perform(post("/api/lowongan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputLowongan)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lowonganId", is(id.toString())));
    }

    @Test
    @DisplayName("POST /api/lowongan - Failure")
    void testCreateLowonganFailure() throws Exception {
        Lowongan inputLowongan = createTestLowongan(null);

        when(lowonganService.createLowongan(any())).thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(post("/api/lowongan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputLowongan)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("\"Gagal membuat lowongan: Something went wrong\""));
        ;
    }

    @Test
    @DisplayName("DELETE /api/lowongan/{id} - Success")
    void testDeleteLowonganSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(lowonganService).deleteLowonganById(id);

        mockMvc.perform(delete("/api/lowongan/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/lowongan/{id} - Not Found")
    void testDeleteLowonganNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new RuntimeException("Data tidak ditemukan")).when(lowonganService).deleteLowonganById(id);

        mockMvc.perform(delete("/api/lowongan/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Lowongan dengan ID " + id + " tidak ditemukan\""));
    }


    private Lowongan createTestLowongan(UUID id) {
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(id);
        lowongan.setIdMataKuliah("CS101");
        lowongan.setSemester(Semester.GENAP.getValue());
        lowongan.setTahunAjaran("2024");
        lowongan.setStatusLowongan(StatusLowongan.DIBUKA.getValue());
        lowongan.setJumlahAsdosDibutuhkan(2);
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);
        return lowongan;
    }

    @Test
    @DisplayName("POST /api/lowongan/{lowonganId}/terima/{pendaftaranId} - Success")
    void testTerimaPendaftarSuccess() throws Exception {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        doNothing().when(lowonganService).terimaPendaftar(lowonganId, pendaftaranId);

        mockMvc.perform(post("/api/lowongan/{lowonganId}/terima/{pendaftaranId}", lowonganId, pendaftaranId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/lowongan/tolak/{pendaftaranId} - Success")
    void testTolakPendaftarSuccess() throws Exception {
        UUID pendaftaranId = UUID.randomUUID();

        doNothing().when(lowonganService).tolakPendaftar(pendaftaranId);

        mockMvc.perform(delete("/api/lowongan/tolak/{pendaftaranId}", pendaftaranId))
                .andExpect(status().isOk());
    }
}
