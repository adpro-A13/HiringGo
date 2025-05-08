package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.hiringgo.authentication.config.JwtAuthenticationFilter;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.AuthenticationService;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LowonganController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(LowonganControllerTest.TestConfig.class)
class LowonganControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LowonganService lowonganService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LowonganService lowonganService() {
            return mock(LowonganService.class);
        }

        @Bean
        public JwtService jwtService() {
            return mock(JwtService.class);
        }

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
            return mock(JwtAuthenticationFilter.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        public AuthenticationService authenticationService() {
            return mock(AuthenticationService.class);
        }
    }

    @Test
    @WithMockUser
    void shouldCreateLowonganAndReturnJson() throws Exception {
        Lowongan requestLowongan = new Lowongan();
        requestLowongan.setIdMataKuliah("CS123");
        requestLowongan.setTahunAjaran("2024/2025");
        requestLowongan.setSemester(String.valueOf(Semester.GANJIL));
        requestLowongan.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        requestLowongan.setJumlahAsdosDibutuhkan(3);
        requestLowongan.setJumlahAsdosDiterima(0);
        requestLowongan.setJumlahAsdosPendaftar(0);

        when(lowonganService.createLowongan(any(Lowongan.class))).thenReturn(requestLowongan);

        mockMvc.perform(post("/api/lowongan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLowongan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMataKuliah").value("CS123"))
                .andExpect(jsonPath("$.semester").value("GANJIL"))
                .andExpect(jsonPath("$.statusLowongan").value("DIBUKA"));
    }

    @Test
    @WithMockUser
    void shouldReturnListOfLowongan() throws Exception {
        Lowongan l1 = new Lowongan(); l1.setIdMataKuliah("A");
        Lowongan l2 = new Lowongan(); l2.setIdMataKuliah("B");
        List<Lowongan> list = Arrays.asList(l1, l2);
        when(lowonganService.findAll()).thenReturn(list);

        mockMvc.perform(get("/api/lowongan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void shouldReturnSemesterList() throws Exception {
        mockMvc.perform(get("/api/lowongan/semester"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(Semester.values().length));
    }

    @Test
    @WithMockUser
    void shouldReturnStatusLowonganList() throws Exception {
        mockMvc.perform(get("/api/lowongan/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(StatusLowongan.values().length));
    }

    @Test
    @WithMockUser
    void shouldDeleteLowongan() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/lowongan/" + id))
                .andExpect(status().isOk());

        verify(lowonganService).deleteLowonganById(eq(id));
    }
}
