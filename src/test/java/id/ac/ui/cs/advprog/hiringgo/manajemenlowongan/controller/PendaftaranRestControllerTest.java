package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PendaftaranRestControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private LowonganService lowonganService;

    @Mock
    private PendaftaranService pendaftaranService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private PendaftaranRestController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    @DisplayName("GET /lowongan/{id} - Success")
    void testGetLowonganDetailSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Lowongan lowongan = createTestLowongan(id);

        when(lowonganService.findById(eq(id))).thenReturn(lowongan);

        mockMvc.perform(get("/lowongan/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowonganId", is(id.toString())));
    }

    @Test
    @DisplayName("GET /lowongan/{id} - Not Found")
    void testGetLowonganDetailNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(lowonganService.findById(eq(id))).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/lowongan/{id}", id))
                .andExpect(status().isNotFound());
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
        lowongan.setIdAsdosDiterima(new ArrayList<>());
        return lowongan;
    }

    private Pendaftaran createTestPendaftaran(UUID id, Lowongan lowongan) {
        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(id);
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setKandidatId("user123");
        pendaftaran.setIpk(BigDecimal.valueOf(3.5));
        pendaftaran.setSks(20);
        pendaftaran.setWaktuDaftar(LocalDateTime.now());
        return pendaftaran;
    }
}