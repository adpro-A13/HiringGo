package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private LowonganDTO dto;
    private UUID id;
    Lowongan lowongan;
    LowonganDTO lowonganDto;
    @Mock
    private LowonganService lowonganService;
    @Mock
    private PendaftaranService pendaftaranService;

    @Mock
    private LowonganMapper lowonganMapper;
    @InjectMocks
    private LowonganController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        dto = new LowonganDTO();
        id = UUID.randomUUID();
        lowongan = createTestLowongan(id);
        lowonganDto = new LowonganDTO();
        dto.setLowonganId(id);
    }

    @Test
    void testGetAllLowonganWithoutFilter() throws Exception {
        when(lowonganService.findAllByDosenUsername("dosen@example.com")).thenReturn(List.of(lowongan));

        when(lowonganMapper.toDtoList(any())).thenReturn(List.of(dto));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("dosen@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/lowongan")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lowonganId", is(id.toString())));
    }


    @Test
    @DisplayName("GET /api/lowongan dengan filter semester dan status - Success")
    void testGetAllLowonganWithFilters() throws Exception {
        when(lowonganService.findAllByDosenUsername("dosen@example.com")).thenReturn(List.of(lowongan));
        when(lowonganMapper.toDtoList(anyList())).thenReturn(List.of(dto));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("dosen@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

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

        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(id);

        LowonganDTO dto = new LowonganDTO();
        dto.setLowonganId(id);

        List<Pendaftaran> daftarPendaftaran = List.of(new Pendaftaran(), new Pendaftaran());
        daftarPendaftaran.get(0).setPendaftaranId(UUID.randomUUID());
        daftarPendaftaran.get(1).setPendaftaranId(UUID.randomUUID());

        // Mock setup
        when(lowonganService.findById(id)).thenReturn(lowongan);
        when(lowonganMapper.toDto(lowongan)).thenReturn(dto);
        when(pendaftaranService.getByLowongan(id)).thenReturn(daftarPendaftaran);

        mockMvc.perform(get("/api/lowongan/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowonganId", is(id.toString())));
    }


    @Test
    @DisplayName("GET /api/lowongan/{id} - Not Found")
    void testGetLowonganByIdNotFound() throws Exception {
        when(lowonganService.findById(id)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/lowongan/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("tidak ditemukan")));
    }

    @Test
    @DisplayName("POST /api/lowongan - Success")
    void testCreateLowonganSuccess() throws Exception {
        Lowongan inputLowongan = createTestLowongan(null);

        when(lowonganService.createLowongan(any())).thenReturn(lowongan);
        when(lowonganMapper.toDto(lowongan)).thenReturn(dto);

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
                .andExpect(content().string("Gagal membuat lowongan: Something went wrong"));
        ;
    }

    @Test
    @DisplayName("DELETE /api/lowongan/{id} - Success")
    void testDeleteLowonganSuccess() throws Exception {

        doNothing().when(lowonganService).deleteLowonganById(id);

        mockMvc.perform(delete("/api/lowongan/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/lowongan/{id} - Not Found")
    void testDeleteLowonganNotFound() throws Exception {

        doThrow(new RuntimeException("Data tidak ditemukan")).when(lowonganService).deleteLowonganById(id);

        mockMvc.perform(delete("/api/lowongan/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Lowongan dengan ID " + id + " tidak ditemukan"));
    }


    private Lowongan createTestLowongan(UUID id) {
        Lowongan lowongan = new Lowongan();
        MataKuliah mataKuliah = new MataKuliah("CS100", "AdvProg", "Design Pattern");
        lowongan.setLowonganId(id);
        lowongan.setMataKuliah(mataKuliah);
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
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();
        doNothing().when(lowonganService).tolakPendaftar(lowonganId, pendaftaranId);

        mockMvc.perform(post("/api/lowongan/{lowonganId}/tolak/{pendaftaranId}",lowonganId, pendaftaranId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/lowongan/{id} - Success")
    void testUpdateLowonganSuccess() throws Exception {
        Lowongan updatedLowongan = createTestLowongan(id);
        LowonganDTO updatedLowonganDto = new LowonganDTO();
        updatedLowonganDto.setLowonganId(id);
        updatedLowonganDto.setTahunAjaran("2024/2025");
        updatedLowonganDto.setSemester(String.valueOf(updatedLowongan.getSemester()));
        updatedLowonganDto.setJumlahAsdosDibutuhkan(5);

        when(lowonganService.updateLowongan(eq(id), ArgumentMatchers.<Lowongan>any()))
                .thenReturn(updatedLowongan);
        when(lowonganMapper.toDto(updatedLowongan)).thenReturn(updatedLowonganDto);

        mockMvc.perform(put("/api/lowongan/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLowonganDto))) // Kirim DTO, bukan entity
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowonganId").value(id.toString()));
    }



    @Test
    @DisplayName("PUT /api/lowongan/{id} - ID mismatch")
    void testUpdateLowonganIdMismatch() throws Exception {
        UUID pathId = UUID.randomUUID();
        UUID bodyId = UUID.randomUUID(); // different ID
        Lowongan updatedLowongan = createTestLowongan(bodyId);

        mockMvc.perform(put("/api/lowongan/{id}", pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedLowongan)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("ID di URL dan body tidak cocok atau ID kosong"));
    }

    @Test
    void testTerimaPendaftarException() throws Exception {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Pendaftaran tidak valid"))
                .when(lowonganService).terimaPendaftar(lowonganId, pendaftaranId);

        mockMvc.perform(post("/api/lowongan/{lowonganId}/terima/{pendaftaranId}", lowonganId, pendaftaranId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Gagal menerima pendaftar: Pendaftaran tidak valid"));
    }

    @Test
    void testTolakPendaftarException() throws Exception {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Pendaftaran tidak valid"))
                .when(lowonganService).tolakPendaftar(lowonganId, pendaftaranId);

        mockMvc.perform(post("/api/lowongan/{lowonganId}/tolak/{pendaftaranId}", lowonganId, pendaftaranId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Gagal menolak pendaftar: Pendaftaran tidak valid"));
    }


    @Test
    void testUpdateLowonganException() throws Exception {
        dto.setLowonganId(id);
        dto.setSemester(String.valueOf(Semester.GANJIL));
        dto.setTahunAjaran("2023");
        dto.setJumlahAsdosDibutuhkan(3);
        dto.setJumlahAsdosPendaftar(1);
        dto.setJumlahAsdosDiterima(0);
        dto.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        MataKuliah mk = new MataKuliah("CS100", "Advpro", "Advanced Programming");
        dto.setIdMataKuliah(mk.getKode());

        Lowongan dummyLowongan = new Lowongan();
        dummyLowongan.setLowonganId(id);

        when(lowonganMapper.toEntity(org.mockito.ArgumentMatchers.any(LowonganDTO.class))).thenReturn(dummyLowongan);
        when(lowonganService.updateLowongan(eq(id), org.mockito.ArgumentMatchers.any(Lowongan.class)))
                .thenThrow(new RuntimeException("Update gagal"));

        mockMvc.perform(put("/api/lowongan/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Gagal memperbarui lowongan: Update gagal"));
    }
}