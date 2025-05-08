package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LowonganRestController.class)
class LowonganRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LowonganService lowonganService;

    @Test
    @DisplayName("GET /api/lowongan - Success")
    void testGetAllLowongan() throws Exception {
        UUID id = UUID.randomUUID();
        Lowongan lowongan = new Lowongan();
        lowongan.setId(id);
        lowongan.setJudul("Lowongan Test");
        List<Lowongan> list = Collections.singletonList(lowongan);
        Mockito.when(lowonganService.findAll()).thenReturn(list);

        mockMvc.perform(get("/api/lowongan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(id.toString())))
                .andExpect(jsonPath("$[0].judul", is("Lowongan Test")));
    }

    @Test
    @DisplayName("GET /api/lowongan/{id} - Success")
    void testGetLowonganByIdSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Lowongan lowongan = new Lowongan();
        lowongan.setId(id);
        lowongan.setJudul("Detail Test");
        Mockito.when(lowonganService.findById(id)).thenReturn(lowongan);

        mockMvc.perform(get("/api/lowongan/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.judul", is("Detail Test")));
    }

    @Test
    @DisplayName("GET /api/lowongan/{id} - Not Found")
    void testGetLowonganByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(lowonganService.findById(id)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/lowongan/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Lowongan dengan ID " + id + " tidak ditemukan")));
    }

    @Test
    @DisplayName("POST /api/lowongan - Forbidden for non-DOSEN")
    @WithMockUser(roles = "MAHASISWA")
    void testCreateLowonganForbidden() throws Exception {
        Lowongan lowongan = new Lowongan();
        lowongan.setJudul("New");

        mockMvc.perform(post("/api/lowongan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lowongan)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/lowongan - Success")
    @WithMockUser(roles = "DOSEN")
    void testCreateLowonganSuccess() throws Exception {
        Lowongan lowongan = new Lowongan();
        lowongan.setJudul("Create Test");
        Lowongan created = new Lowongan();
        UUID id = UUID.randomUUID(); created.setId(id); created.setJudul("Create Test");
        Mockito.when(lowonganService.createLowongan(any(Lowongan.class))).thenReturn(created);

        mockMvc.perform(post("/api/lowongan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lowongan)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.judul", is("Create Test")));
    }

    @Test
    @DisplayName("POST /api/lowongan - Bad Request on Error")
    @WithMockUser(roles = "DOSEN")
    void testCreateLowonganBadRequest() throws Exception {
        Lowongan lowongan = new Lowongan(); lowongan.setJudul("Fail");
        Mockito.when(lowonganService.createLowongan(any(Lowongan.class)))
                .thenThrow(new RuntimeException("Invalid data"));

        mockMvc.perform(post("/api/lowongan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lowongan)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Gagal membuat lowongan: Invalid data")));
    }

    @Test
    @DisplayName("DELETE /api/lowongan/{id} - Forbidden for non-DOSEN")
    @WithMockUser(roles = "MAHASISWA")
    void testDeleteLowonganForbidden() throws Exception {
        mockMvc.perform(delete("/api/lowongan/{id}", UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/lowongan/{id} - Success")
    @WithMockUser(roles = "DOSEN")
    void testDeleteLowonganSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(lowonganService).deleteLowonganById(id);

        mockMvc.perform(delete("/api/lowongan/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/lowongan/{id} - Not Found")
    @WithMockUser(roles = "DOSEN")
    void testDeleteLowonganNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException()).when(lowonganService).deleteLowonganById(id);

        mockMvc.perform(delete("/api/lowongan/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Lowongan dengan ID " + id + " tidak ditemukan")));
    }

    @Test
    @DisplayName("GET /api/lowongan/enums/semester - Success")
    void testGetAllSemesters() throws Exception {
        Semester[] values = Semester.values();
        mockMvc.perform(get("/api/lowongan/enums/semester"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(values.length)))
                .andExpect(jsonPath("$[0]", is(values[0].name())));
    }

    @Test
    @DisplayName("GET /api/lowongan/enums/status - Success")
    void testGetAllStatuses() throws Exception {
        StatusLowongan[] values = StatusLowongan.values();
        mockMvc.perform(get("/api/lowongan/enums/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(values.length)))
                .andExpect(jsonPath("$[0]", is(values[0].name())));
    }
}