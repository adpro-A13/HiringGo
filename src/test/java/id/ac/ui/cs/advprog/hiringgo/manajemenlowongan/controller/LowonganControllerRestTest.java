package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PendaftaranRestController.class)
class PendaftaranRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LowonganService lowonganService;

    @MockBean
    private PendaftaranService pendaftaranService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("GET /lowongan/{id} - Success")
    void testGetLowonganDetailSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Lowongan lowongan = new Lowongan();
        lowongan.setId(id);
        lowongan.setJudul("Judul Test");
        Mockito.when(lowonganService.findById(id)).thenReturn(lowongan);

        mockMvc.perform(get("/lowongan/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.judul", is("Judul Test")));
    }

    @Test
    @DisplayName("GET /lowongan/{id} - Not Found")
    void testGetLowonganDetailNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(lowonganService.findById(id)).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/lowongan/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Lowongan tidak ditemukan"));
    }

    @Test
    @DisplayName("POST /lowongan/{id}/daftar - Unauthorized")
    void testDaftarUnauthorized() throws Exception {
        UUID id = UUID.randomUUID();
        DaftarForm form = new DaftarForm();
        form.setIpk(3.5);
        form.setSks(20);

        mockMvc.perform(post("/lowongan/{id}/daftar", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Anda harus login terlebih dahulu"));
    }

    @Test
    @WithMockUser(username = "user123")
    @DisplayName("POST /lowongan/{id}/daftar - Success")
    void testDaftarSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        DaftarForm form = new DaftarForm();
        form.setIpk(3.5);
        form.setSks(20);
        Pendaftaran pendaftaran = new Pendaftaran();
        pendaftaran.setId(UUID.randomUUID());

        Mockito.when(pendaftaranService.daftar(eq(id), eq("user123"), any(BigDecimal.class), eq(20)))
                .thenReturn(pendaftaran);

        mockMvc.perform(post("/lowongan/{id}/daftar", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Berhasil mendaftar")))
                .andExpect(jsonPath("$.data.id", is(pendaftaran.getId().toString())));
    }

    @Test
    @WithMockUser(username = "user123")
    @DisplayName("POST /lowongan/{id}/daftar - Not Found")
    void testDaftarLowonganNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        DaftarForm form = new DaftarForm();
        form.setIpk(3.5);
        form.setSks(20);

        Mockito.when(pendaftaranService.daftar(eq(id), anyString(), any(BigDecimal.class), anyInt()))
                .thenThrow(new NoSuchElementException());

        mockMvc.perform(post("/lowongan/{id}/daftar", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Lowongan tidak ditemukan"));
    }

    @Test
    @WithMockUser(username = "user123")
    @DisplayName("POST /lowongan/{id}/daftar - Bad Request")
    void testDaftarBadRequest() throws Exception {
        UUID id = UUID.randomUUID();
        DaftarForm form = new DaftarForm();
        form.setIpk(3.5);
        form.setSks(20);

        Mockito.when(pendaftaranService.daftar(eq(id), anyString(), any(BigDecimal.class), anyInt()))
                .thenThrow(new IllegalStateException("IPK tidak memenuhi syarat"));

        mockMvc.perform(post("/lowongan/{id}/daftar", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("IPK tidak memenuhi syarat"));
    }

    @Test
    @WithMockUser(username = "user123")
    @DisplayName("POST /lowongan/{id}/daftar - Internal Server Error")
    void testDaftarInternalError() throws Exception {
        UUID id = UUID.randomUUID();
        DaftarForm form = new DaftarForm();
        form.setIpk(3.5);
        form.setSks(20);

        Mockito.when(pendaftaranService.daftar(eq(id), anyString(), any(BigDecimal.class), anyInt()))
                .thenThrow(new RuntimeException("Server error"));

        mockMvc.perform(post("/lowongan/{id}/daftar", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Terjadi kesalahan: Server error")));
    }
}
