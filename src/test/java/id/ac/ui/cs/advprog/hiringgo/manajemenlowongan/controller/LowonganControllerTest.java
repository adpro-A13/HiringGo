package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.exception.LowonganExceptionHandler;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganFilterService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganSortService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
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
    private LowonganSortService lowonganSortService;

    @Mock
    private LowonganFilterService lowonganFilterService;

    @Mock
    private LowonganMapper lowonganMapper;

    @InjectMocks
    private LowonganController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new LowonganExceptionHandler())
                .build();
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
        lowongan.setLowonganId(id);
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
        when(lowonganService.findById(id)).thenThrow(new EntityNotFoundException("Lowongan dengan ID tersebut tidak ditemukan"));

        mockMvc.perform(get("/api/lowongan/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Lowongan dengan ID tersebut tidak ditemukan"));
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
    void whenCreateLowonganAlreadyExists_thenReturn400() throws Exception {
        // Mock mapper supaya dari DTO apapun jadi lowongan yang kamu mau
        when(lowonganMapper.toEntity( org.mockito.ArgumentMatchers.any(LowonganDTO.class))).thenReturn(lowongan);

        // Mock service untuk throw exception konflik
        when(lowonganService.createLowongan( org.mockito.ArgumentMatchers.any(Lowongan.class)))
                .thenThrow(new IllegalStateException("Lowongan sudah ada."));

        // Kirim dto (bisa minimal, asal valid JSON)
        mockMvc.perform(post("/api/lowongan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))) // dto bisa hanya ID
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid State")))
                .andExpect(jsonPath("$.message", is("Lowongan sudah ada.")));
    }



    @Test
    void whenUserNotAuthorized_thenReturn403() throws Exception {
        when(lowonganMapper.toEntity(org.mockito.ArgumentMatchers.any(LowonganDTO.class))).thenReturn(lowongan);
        when(lowonganService.createLowongan(any())).thenThrow(new AccessDeniedException("Anda bukan pengampu mata kuliah ini."));

        mockMvc.perform(post("/api/lowongan")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Forbidden")))
                .andExpect(jsonPath("$.message", is("Anda bukan pengampu mata kuliah ini.")));
    }




    @Test
    @DisplayName("DELETE /api/lowongan/{id} - Success")
    void testDeleteLowonganSuccess() throws Exception {

        doNothing().when(lowonganService).deleteLowonganById(id);

        mockMvc.perform(delete("/api/lowongan/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void whenUserNotAuthorizedToDelete_thenReturn403() throws Exception {
        UUID lowonganId = UUID.randomUUID();

        doThrow(new AccessDeniedException("Anda bukan pengampu mata kuliah ini."))
                .when(lowonganService).deleteLowonganById(lowonganId);

        mockMvc.perform(delete("/api/lowongan/" + lowonganId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Forbidden")))
                .andExpect(jsonPath("$.message", is("Anda bukan pengampu mata kuliah ini.")));
    }

    @Test
    void whenLowonganHasPendaftaran_thenReturn400() throws Exception {
        UUID lowonganId = UUID.randomUUID();

        doThrow(new IllegalStateException("Lowongan ini tidak dapat dihapus karena masih memiliki pendaftaran."))
                .when(lowonganService).deleteLowonganById(lowonganId);

        mockMvc.perform(delete("/api/lowongan/" + lowonganId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid State")))
                .andExpect(jsonPath("$.message", is("Lowongan ini tidak dapat dihapus karena masih memiliki pendaftaran.")));
    }



    private Lowongan createTestLowongan(UUID id) {
        MataKuliah mataKuliah = new MataKuliah("CS100", "AdvProg", "Design Pattern");
        Lowongan lowonganTest = new Lowongan();
        lowonganTest.setLowonganId(id);
        lowonganTest.setMataKuliah(mataKuliah);
        lowonganTest.setSemester(Semester.GENAP.getValue());
        lowonganTest.setTahunAjaran("2024");
        lowonganTest.setStatusLowongan(StatusLowongan.DIBUKA.getValue());
        lowonganTest.setJumlahAsdosDibutuhkan(2);
        lowonganTest.setJumlahAsdosDiterima(0);
        lowonganTest.setJumlahAsdosPendaftar(0);
        return lowonganTest;
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
    void whenIdMismatch_thenReturn400() throws Exception {
        UUID urlId = UUID.randomUUID();
        UUID bodyId = UUID.randomUUID(); // beda dari urlId

        LowonganDTO mismatchDto = new LowonganDTO();
        mismatchDto.setLowonganId(bodyId); // mismatch id

        mockMvc.perform(put("/api/lowongan/" + urlId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mismatchDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("ID di URL dan body tidak cocok atau ID kosong")));
    }

    @Test
    void whenNotAuthorizedToUpdate_thenReturn403() throws Exception {
        UUID id = UUID.randomUUID();
        dto.setLowonganId(id);

        when(lowonganMapper.toEntity(any())).thenReturn(lowongan);
        doThrow(new AccessDeniedException("Anda bukan pengampu mata kuliah ini."))
                .when(lowonganService).updateLowongan(eq(id), any());

        mockMvc.perform(put("/api/lowongan/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Forbidden")))
                .andExpect(jsonPath("$.message", is("Anda bukan pengampu mata kuliah ini.")));
    }

    @Test
    void whenLowonganCombinationNotUnique_thenReturn400() throws Exception {
        UUID id = UUID.randomUUID();
        dto.setLowonganId(id);

        when(lowonganMapper.toEntity(any())).thenReturn(lowongan);
        doThrow(new IllegalStateException("Lowongan dengan kombinasi ini sudah ada."))
                .when(lowonganService).updateLowongan(eq(id), any());

        mockMvc.perform(put("/api/lowongan/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid State")))
                .andExpect(jsonPath("$.message", is("Lowongan dengan kombinasi ini sudah ada.")));
    }

    @Test
    void whenTerimaPendaftarFails_thenReturn404() throws Exception {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        doThrow(new EntityNotFoundException("Lowongan atau pendaftaran tidak ditemukan"))
                .when(lowonganService).terimaPendaftar(lowonganId, pendaftaranId);

        mockMvc.perform(post("/api/lowongan/{lowonganId}/terima/{pendaftaranId}", lowonganId, pendaftaranId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Lowongan atau pendaftaran tidak ditemukan")));
    }


    @Test
    void testTolakPendaftarException() throws Exception {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Pendaftaran tidak valid"))
                .when(lowonganService).tolakPendaftar(lowonganId, pendaftaranId);

        mockMvc.perform(post("/api/lowongan/{lowonganId}/tolak/{pendaftaranId}", lowonganId, pendaftaranId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Pendaftaran tidak valid"));
    }



    @Test
    void whenTerimaPendaftarThrowsIllegalState_thenReturn400() throws Exception {
        UUID lowonganId = UUID.randomUUID();
        UUID pendaftaranId = UUID.randomUUID();

        doThrow(new IllegalStateException("Operasi tidak valid"))
                .when(lowonganService).terimaPendaftar(lowonganId, pendaftaranId);

        mockMvc.perform(post("/api/lowongan/{lowonganId}/terima/{pendaftaranId}", lowonganId, pendaftaranId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid State")))
                .andExpect(jsonPath("$.message", is("Operasi tidak valid")));
    }


}