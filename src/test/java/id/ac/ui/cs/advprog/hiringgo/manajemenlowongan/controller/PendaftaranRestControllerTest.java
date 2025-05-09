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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PendaftaranRestController.class)
@AutoConfigureMockMvc           // real Spring Security filters are on
@WithMockUser(                  // every request will be authenticated
        username = "testUser",
        roles = "MAHASISWA"
)
class PendaftaranRestControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private LowonganService lowonganService;
    @MockBean private PendaftaranService pendaftaranService;
    @MockBean private JwtService jwtService;

    private UUID lowonganId;
    private Lowongan lowongan;
    private Pendaftaran pendaftaran;
    private DaftarForm daftarForm;

    @BeforeEach
    void setup() {
        lowonganId = UUID.randomUUID();

        lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setIdMataKuliah("CSUI-ADVPROG");
        lowongan.setTahunAjaran("2024/2025");
        lowongan.setSemester(Semester.GANJIL.getValue());
        lowongan.setStatusLowongan(StatusLowongan.DIBUKA.getValue());
        lowongan.setJumlahAsdosDibutuhkan(3);
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);
        lowongan.setIdAsdosDiterima(new ArrayList<>());

        daftarForm = new DaftarForm();
        daftarForm.setIpk(3.75);
        daftarForm.setSks(20);

        pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(UUID.randomUUID());
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setKandidatId("testUser");
        pendaftaran.setIpk(BigDecimal.valueOf(3.75));
        pendaftaran.setSks(20);
        pendaftaran.setWaktuDaftar(LocalDateTime.now());
    }

    @Test
    void testGetLowonganDetail() throws Exception {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);

        mockMvc.perform(get("/api/lowongandaftar/{id}", lowonganId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowonganId").value(lowonganId.toString()))
                .andExpect(jsonPath("$.idMataKuliah").value("CSUI-ADVPROG"))
                .andExpect(jsonPath("$.statusLowongan").value("DIBUKA"));
    }

    @Test
    void testGetLowonganDetailNotFound() throws Exception {
        when(lowonganService.findById(lowonganId)).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/api/lowongandaftar/{id}", lowonganId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDaftarSuccess() throws Exception {
        when(pendaftaranService.daftar(
                eq(lowonganId),
                eq("testUser"),
                eq(BigDecimal.valueOf(3.75)),
                eq(20)
        )).thenReturn(pendaftaran);

        mockMvc.perform(
                        post("/api/lowongandaftar/{id}/daftar", lowonganId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(daftarForm))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Berhasil mendaftar asisten dosen"))
                .andExpect(jsonPath("$.pendaftaranId").value(pendaftaran.getPendaftaranId().toString()))
                .andExpect(jsonPath("$.lowonganId").value(lowonganId.toString()))
                .andExpect(jsonPath("$.kandidatId").value("testUser"))
                .andExpect(jsonPath("$.ipk").value(3.75))
                .andExpect(jsonPath("$.sks").value(20))
                .andExpect(jsonPath("$.waktuDaftar").exists());
    }

    @Test
    void testDaftarLowonganNotFound() throws Exception {
        when(pendaftaranService.daftar(
                eq(lowonganId),
                eq("testUser"),
                eq(BigDecimal.valueOf(3.75)),
                eq(20)
        )).thenThrow(new NoSuchElementException("Lowongan tidak ditemukan"));

        mockMvc.perform(
                        post("/api/lowongandaftar/{id}/daftar", lowonganId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(daftarForm))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void testDaftarQuotaFull() throws Exception {
        when(pendaftaranService.daftar(
                eq(lowonganId),
                eq("testUser"),
                eq(BigDecimal.valueOf(3.75)),
                eq(20)
        )).thenThrow(new IllegalStateException("Kuota lowongan sudah penuh!"));

        mockMvc.perform(
                        post("/api/lowongandaftar/{id}/daftar", lowonganId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(daftarForm))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Kuota lowongan sudah penuh!"));
    }

    @Test
    void testDaftarInvalidForm() throws Exception {
        DaftarForm invalid = new DaftarForm();
        invalid.setIpk(5.01); // > 4.0
        invalid.setSks(20);

        mockMvc.perform(
                        post("/api/lowongandaftar/{id}/daftar", lowonganId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalid))
                )
                .andExpect(status().isBadRequest());
    }
}
