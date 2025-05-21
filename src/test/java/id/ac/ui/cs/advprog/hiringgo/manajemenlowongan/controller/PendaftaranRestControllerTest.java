package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.PendaftaranService;
import jakarta.persistence.EntityNotFoundException;
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
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@WebMvcTest(PendaftaranRestController.class)
@AutoConfigureMockMvc
@WithMockUser(
        username = "testUser",
        roles = {"MAHASISWA"}
)
class PendaftaranRestControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private LowonganService lowonganService;
    @MockBean private PendaftaranService pendaftaranService;
    @MockBean private JwtService jwtService;
    @MockBean private UserRepository userRepository;
    @MockBean private PendaftaranRepository pendaftaranRepository;
    @MockBean private LowonganMapper lowonganMapper;

    private UUID lowonganId;
    private Lowongan lowongan;
    private Pendaftaran pendaftaran;
    private DaftarForm daftarForm;
    private Mahasiswa mahasiswaMock;

    @BeforeEach
    void setup() {
        lowonganId = UUID.randomUUID();

        lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        MataKuliah mataKuliah = new MataKuliah("CS100", "AdvProg", "sigma sigma boy");
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setTahunAjaran("2024/2025");
        lowongan.setSemester(Semester.GANJIL.getValue());
        lowongan.setStatusLowongan(StatusLowongan.DIBUKA.getValue());
        lowongan.setJumlahAsdosDibutuhkan(3);
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);

        daftarForm = new DaftarForm();
        daftarForm.setIpk(3.75);
        daftarForm.setSks(20);

        mahasiswaMock = mock(Mahasiswa.class);
        when(mahasiswaMock.getFullName()).thenReturn("Mock Mahasiswa");
        when(mahasiswaMock.getId()).thenReturn(UUID.randomUUID());

        pendaftaran = new Pendaftaran();
        pendaftaran.setPendaftaranId(UUID.randomUUID());
        pendaftaran.setKandidat(mahasiswaMock);
        pendaftaran.setLowongan(lowongan);
        pendaftaran.setIpk(BigDecimal.valueOf(3.75));
        pendaftaran.setSks(20);
        pendaftaran.setWaktuDaftar(LocalDateTime.now());
        pendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);
    }

    @Test
    void testGetLowonganDetail() throws Exception {
        when(lowonganService.findById(lowonganId)).thenReturn(lowongan);

        mockMvc.perform(get("/api/lowongandaftar/{id}", lowonganId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowonganId").value(lowonganId.toString()))
                .andExpect(jsonPath("$.idMataKuliah").value("CS100"))
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
        // Create a Principal mock
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("testUser");

        // Mock the behavior of userRepository.findByEmail to return the mahasiswaMock
        when(userRepository.findByEmail(eq("testUser"))).thenReturn(Optional.of(mahasiswaMock));

        // Mock pendaftaranRepository to return an empty list (user hasn't applied yet)
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(
                eq(mahasiswaMock.getId()), eq(lowonganId))).thenReturn(new ArrayList<>());

        when(pendaftaranService.daftar(
                eq(lowonganId),
                any(Mahasiswa.class),
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
                .andExpect(jsonPath("$.ipk").value(3.75))
                .andExpect(jsonPath("$.sks").value(20))
                .andExpect(jsonPath("$.waktuDaftar").exists());
    }

    @Test
    void testDaftarUserAlreadyRegistered() throws Exception {
        when(userRepository.findByEmail(eq("testUser"))).thenReturn(Optional.of(mahasiswaMock));

        // Mock that user has already applied
        List<Pendaftaran> existingPendaftaran = List.of(pendaftaran);
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(
                eq(mahasiswaMock.getId()), eq(lowonganId))).thenReturn(existingPendaftaran);

        mockMvc.perform(
                        post("/api/lowongandaftar/{id}/daftar", lowonganId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(daftarForm))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Anda sudah mendaftar untuk lowongan ini"));
    }

    @Test
    void testDaftarLowonganNotFound() throws Exception {
        when(userRepository.findByEmail(eq("testUser"))).thenReturn(Optional.of(mahasiswaMock));
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(
                eq(mahasiswaMock.getId()), eq(lowonganId))).thenReturn(new ArrayList<>());

        when(pendaftaranService.daftar(
                eq(lowonganId),
                any(Mahasiswa.class),
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
        when(userRepository.findByEmail(eq("testUser"))).thenReturn(Optional.of(mahasiswaMock));
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(
                eq(mahasiswaMock.getId()), eq(lowonganId))).thenReturn(new ArrayList<>());

        when(pendaftaranService.daftar(
                eq(lowonganId),
                any(Mahasiswa.class),
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
    void testDaftarUserNotFound() throws Exception {
        when(userRepository.findByEmail(eq("testUser"))).thenThrow(
                new EntityNotFoundException("User not found: testUser"));

        mockMvc.perform(
                        post("/api/lowongandaftar/{id}/daftar", lowonganId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(daftarForm))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User tidak ditemukan: User not found: testUser"));
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

    @Test
    void testDaftarGeneralException() throws Exception {
        when(userRepository.findByEmail(eq("testUser"))).thenReturn(Optional.of(mahasiswaMock));
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(
                eq(mahasiswaMock.getId()), eq(lowonganId))).thenReturn(new ArrayList<>());

        when(pendaftaranService.daftar(
                eq(lowonganId),
                any(Mahasiswa.class),
                eq(BigDecimal.valueOf(3.75)),
                eq(20)
        )).thenThrow(new RuntimeException("Unexpected server error"));

        mockMvc.perform(
                        post("/api/lowongandaftar/{id}/daftar", lowonganId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(daftarForm))
                )
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Terjadi kesalahan: Unexpected server error"));
    }

    @Test
    void testDaftarWithNullPrincipal() {
        // Create the controller instance with all the required dependencies
        PendaftaranRestController controller = new PendaftaranRestController(
                lowonganService,
                pendaftaranService,
                jwtService,
                userRepository,
                pendaftaranRepository,
                lowonganMapper
        );

        // Call the daftar method with a null Principal
        ResponseEntity<?> response = controller.daftar(lowonganId, daftarForm, null);

        // Verify the response
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Anda harus login terlebih dahulu", response.getBody());
    }

    @Test
    void testGetLowonganApplyStatusWithNullPrincipal() {
        PendaftaranRestController controller = new PendaftaranRestController(
                lowonganService,
                pendaftaranService,
                jwtService,
                userRepository,
                pendaftaranRepository,
                lowonganMapper
        );

        ResponseEntity<?> response = controller.getLowonganApplyStatus(lowonganId, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Anda harus login terlebih dahulu", response.getBody());
    }

    @Test
    void testGetLowonganApplyStatusUserNotFound() throws Exception {
        when(userRepository.findByEmail(eq("testUser"))).thenThrow(
                new EntityNotFoundException("User not found: testUser"));

        mockMvc.perform(get("/api/lowongandaftar/{id}/status", lowonganId))
                .andExpect(status().isUnauthorized())  // Changed from isInternalServerError()
                .andExpect(content().string("User tidak ditemukan: User not found: testUser"));
    }

    @Test
    void testGetLowonganApplyStatusUserHasNotApplied() throws Exception {
        when(userRepository.findByEmail(eq("testUser"))).thenReturn(Optional.of(mahasiswaMock));
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(
                eq(mahasiswaMock.getId()), eq(lowonganId))).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/lowongandaftar/{id}/status", lowonganId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasApplied").value(false))
                .andExpect(jsonPath("$.status").value("BELUM_DAFTAR"));
    }

    @Test
    void testGetLowonganApplyStatusUserHasApplied() throws Exception {
        when(userRepository.findByEmail(eq("testUser"))).thenReturn(Optional.of(mahasiswaMock));
        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(
                eq(mahasiswaMock.getId()), eq(lowonganId))).thenReturn(List.of(pendaftaran));

        mockMvc.perform(get("/api/lowongandaftar/{id}/status", lowonganId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasApplied").value(true))
                .andExpect(jsonPath("$.status").value("BELUM_DIPROSES"))
                .andExpect(jsonPath("$.pendaftaranId").value(pendaftaran.getPendaftaranId().toString()));
    }

    @Test
    void testGetAllLowonganForMahasiswa() throws Exception {
        // Create test data
        Lowongan lowongan1 = new Lowongan();
        lowongan1.setLowonganId(UUID.randomUUID());
        MataKuliah mk1 = new MataKuliah("MK001", "Test Course 1", "Desc1");
        lowongan1.setMataKuliah(mk1);

        Lowongan lowongan2 = new Lowongan();
        lowongan2.setLowonganId(UUID.randomUUID());
        MataKuliah mk2 = new MataKuliah("MK002", "Test Course 2", "Desc2");
        lowongan2.setMataKuliah(mk2);

        List<Lowongan> lowonganList = Arrays.asList(lowongan1, lowongan2);

        // Create DTOs that will be returned by the mapper
        LowonganDTO dto1 = new LowonganDTO();
        dto1.setLowonganId(lowongan1.getLowonganId());
        dto1.setIdMataKuliah(mk1.getKode());  // Using idMataKuliah instead of namaMatakuliah
        dto1.setStatusLowongan(StatusLowongan.DIBUKA);

        LowonganDTO dto2 = new LowonganDTO();
        dto2.setLowonganId(lowongan2.getLowonganId());
        dto2.setIdMataKuliah(mk2.getKode());  // Using idMataKuliah instead of namaMatakuliah
        dto2.setStatusLowongan(StatusLowongan.DIBUKA);

        List<LowonganDTO> lowonganDTOList = Arrays.asList(dto1, dto2);

        // Mock service and mapper
        when(lowonganService.findAll()).thenReturn(lowonganList);
        when(lowonganMapper.toDtoList(lowonganList)).thenReturn(lowonganDTOList);

        // Perform request and verify response
        mockMvc.perform(get("/api/lowongandaftar/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].lowonganId", is(dto1.getLowonganId().toString())))
                .andExpect(jsonPath("$[0].idMataKuliah", is(dto1.getIdMataKuliah())))
                .andExpect(jsonPath("$[1].lowonganId", is(dto2.getLowonganId().toString())))
                .andExpect(jsonPath("$[1].idMataKuliah", is(dto2.getIdMataKuliah())));
    }

    @Test
    void testGetLowonganApplyStatusGeneralException() throws Exception {
        when(userRepository.findByEmail(eq("testUser"))).thenReturn(Optional.of(mahasiswaMock));

        when(pendaftaranRepository.findByKandidatIdAndLowonganLowonganId(
                eq(mahasiswaMock.getId()), eq(lowonganId)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/lowongandaftar/{id}/status", lowonganId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Terjadi kesalahan: Database connection failed"));
    }

    @Test
    void testDaftarUserNotFoundViaEmptyOptional() throws Exception {
        when(userRepository.findByEmail(eq("testUser"))).thenReturn(Optional.empty());

        mockMvc.perform(
                        post("/api/lowongandaftar/{id}/daftar", lowonganId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(daftarForm))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User tidak ditemukan: User not found: testUser"));
    }

    @Test
    void testGetLowonganApplyStatusUserNotFoundViaEmptyOptional() throws Exception {
        when(userRepository.findByEmail(eq("testUser"))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/lowongandaftar/{id}/status", lowonganId))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User tidak ditemukan: User not found: testUser"));
    }
}