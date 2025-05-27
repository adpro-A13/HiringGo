package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarForm;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDetailResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.exception.LowonganExceptionHandler;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.ApplicationProcessingService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.ApplicationStatusService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.AuthenticationValidatorService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.ResponseBuilderService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.validation.DaftarFormBusinessValidator; // Tambahkan import ini
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
class PendaftaranRestControllerTest {

    @Mock
    private LowonganService lowonganService;

    @Mock
    private ApplicationProcessingService applicationProcessingService;

    @Mock
    private ApplicationStatusService applicationStatusService;

    @Mock
    private AuthenticationValidatorService authenticationValidatorService;

    @Mock
    private ResponseBuilderService responseBuilderService;

    @Mock
    private LowonganMapper lowonganMapper;

    @Mock
    private DaftarFormBusinessValidator daftarFormBusinessValidator; // Tambahkan mock ini

    @InjectMocks
    private PendaftaranRestController pendaftaranRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID testLowonganId;
    private Lowongan testLowongan;
    private Mahasiswa testMahasiswa;
    private DaftarForm testDaftarForm;
    private Pendaftaran testPendaftaran;
    private LowonganDetailResponse testLowonganDetailResponse;

    @BeforeEach
    void setUp() {
        // Configure MockMvc with GlobalExceptionHandler
        mockMvc = MockMvcBuilders.standaloneSetup(pendaftaranRestController)
                .setControllerAdvice(new LowonganExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        testLowonganId = UUID.randomUUID();

        testLowongan = new Lowongan();
        testLowongan.setLowonganId(testLowonganId);
        MataKuliah mataKuliah = new MataKuliah("CS101", "Advanced Programming", "Advanced Programming Course");
        testLowongan.setMataKuliah(mataKuliah);
        testLowongan.setTahunAjaran("2024/2025");
        testLowongan.setSemester(Semester.GANJIL.getValue());
        testLowongan.setStatusLowongan(StatusLowongan.DIBUKA.getValue());
        testLowongan.setJumlahAsdosDibutuhkan(3);

        testMahasiswa = new Mahasiswa();
        testMahasiswa.setId(UUID.randomUUID());
        testMahasiswa.setUsername("test@ui.ac.id");
        testMahasiswa.setFullName("Test Student");

        testDaftarForm = new DaftarForm();
        testDaftarForm.setIpk(3.75);
        testDaftarForm.setSks(20);

        testPendaftaran = new Pendaftaran();
        testPendaftaran.setPendaftaranId(UUID.randomUUID());
        testPendaftaran.setKandidat(testMahasiswa);
        testPendaftaran.setLowongan(testLowongan);
        testPendaftaran.setIpk(BigDecimal.valueOf(3.75));
        testPendaftaran.setSks(20);
        testPendaftaran.setWaktuDaftar(LocalDateTime.now());
        testPendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);

        testLowonganDetailResponse = new LowonganDetailResponse(testLowongan);
    }

    @Test
    void testGetLowonganDetail_Success() throws Exception {
        when(lowonganService.findById(testLowonganId)).thenReturn(testLowongan);

        Map<String, Object> successResponse = createMockResponse(200, "Lowongan detail retrieved successfully");
        successResponse.put("lowongan", testLowonganDetailResponse);

        when(responseBuilderService.buildSuccessResponse(
                eq("Lowongan detail retrieved successfully"),
                eq("lowongan"),
                any(LowonganDetailResponse.class)
        )).thenReturn(ResponseEntity.ok(successResponse));

        mockMvc.perform(get("/api/lowongandaftar/{id}", testLowonganId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.message").value("Lowongan detail retrieved successfully"));

        verify(lowonganService).findById(testLowonganId);
        verify(responseBuilderService).buildSuccessResponse(
                eq("Lowongan detail retrieved successfully"),
                eq("lowongan"),
                any(LowonganDetailResponse.class)
        );
    }

    @Test
    void testGetLowonganDetail_NotFound() throws Exception {
        when(lowonganService.findById(testLowonganId)).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/api/lowongandaftar/{id}", testLowonganId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Lowongan tidak ditemukan dengan ID: " + testLowonganId));

        verify(lowonganService).findById(testLowonganId);
    }

    @Test
    void testDaftar_Success() throws Exception {
        doNothing().when(daftarFormBusinessValidator).validateBusinessRules(any(DaftarForm.class));

        lenient().when(authenticationValidatorService.validateAndGetCurrentUser(any()))
                .thenReturn(testMahasiswa);
        lenient().when(applicationProcessingService.processApplication(any(UUID.class), any(DaftarForm.class), any(Mahasiswa.class)))
                .thenReturn(testPendaftaran);

        Map<String, Object> registrationResponse = createMockResponse(201, "Berhasil mendaftar asisten dosen");
        lenient().when(responseBuilderService.buildRegistrationResponse(testPendaftaran))
                .thenReturn(ResponseEntity.status(201).body(registrationResponse));

        mockMvc.perform(post("/api/lowongandaftar/{id}/daftar", testLowonganId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDaftarForm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status_code").value(201))
                .andExpect(jsonPath("$.message").value("Berhasil mendaftar asisten dosen"));
    }

    @Test
    void testDaftar_ValidationError() throws Exception {
        DaftarForm invalidForm = new DaftarForm();
        invalidForm.setIpk(5.0);
        invalidForm.setSks(-10);

        mockMvc.perform(post("/api/lowongandaftar/{id}/daftar", testLowonganId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[*].field").exists())
                .andExpect(jsonPath("$.details[*].message").exists());
    }

    @Test
    void testDaftar_AuthenticationError() throws Exception {
        doNothing().when(daftarFormBusinessValidator).validateBusinessRules(any(DaftarForm.class));

        lenient().when(authenticationValidatorService.validateAndGetCurrentUser(any()))
                .thenThrow(new IllegalArgumentException("Anda harus login terlebih dahulu"));

        mockMvc.perform(post("/api/lowongandaftar/{id}/daftar", testLowonganId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDaftarForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Anda harus login terlebih dahulu"));
    }

    @Test
    void testDaftar_ProcessingError() throws Exception {
        doNothing().when(daftarFormBusinessValidator).validateBusinessRules(any(DaftarForm.class));

        lenient().when(authenticationValidatorService.validateAndGetCurrentUser(any()))
                .thenReturn(testMahasiswa);
        lenient().when(applicationProcessingService.processApplication(any(UUID.class), any(DaftarForm.class), any(Mahasiswa.class)))
                .thenThrow(new IllegalArgumentException("Anda sudah mendaftar untuk lowongan ini"));

        mockMvc.perform(post("/api/lowongandaftar/{id}/daftar", testLowonganId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDaftarForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Anda sudah mendaftar untuk lowongan ini"));
    }

    @Test
    void testDaftar_UnexpectedError() throws Exception {
        lenient().when(authenticationValidatorService.validateAndGetCurrentUser(any()))
                .thenReturn(testMahasiswa);
        lenient().when(applicationProcessingService.processApplication(any(UUID.class), any(DaftarForm.class), any(Mahasiswa.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/api/lowongandaftar/{id}/daftar", testLowonganId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDaftarForm)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }

    @Test
    void testGetLowonganApplyStatus_Success() throws Exception {
        Map<String, Object> statusData = new HashMap<>();
        statusData.put("hasApplied", true);
        statusData.put("status", "BELUM_DIPROSES");
        statusData.put("pendaftaranId", testPendaftaran.getPendaftaranId().toString());

        lenient().when(authenticationValidatorService.validateAndGetCurrentUser(any()))
                .thenReturn(testMahasiswa);
        lenient().when(applicationStatusService.getApplicationStatus(testLowonganId, testMahasiswa))
                .thenReturn(statusData);

        Map<String, Object> successResponse = createMockResponse(200, "Status pendaftaran retrieved successfully");
        successResponse.put("application_status", statusData);

        lenient().when(responseBuilderService.buildSuccessResponse(
                eq("Status pendaftaran retrieved successfully"),
                eq("application_status"),
                eq(statusData)
        )).thenReturn(ResponseEntity.ok(successResponse));

        mockMvc.perform(get("/api/lowongandaftar/{id}/status", testLowonganId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.message").value("Status pendaftaran retrieved successfully"))
                .andExpect(jsonPath("$.application_status.hasApplied").value(true))
                .andExpect(jsonPath("$.application_status.status").value("BELUM_DIPROSES"));
    }

    @Test
    void testGetLowonganApplyStatus_AuthenticationError() throws Exception {
        lenient().when(authenticationValidatorService.validateAndGetCurrentUser(any()))
                .thenThrow(new IllegalArgumentException("Anda harus login terlebih dahulu"));

        mockMvc.perform(get("/api/lowongandaftar/{id}/status", testLowonganId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Anda harus login terlebih dahulu"));
    }

    @Test
    void testGetLowonganApplyStatus_ServiceError() throws Exception {
        lenient().when(authenticationValidatorService.validateAndGetCurrentUser(any()))
                .thenReturn(testMahasiswa);
        lenient().when(applicationStatusService.getApplicationStatus(testLowonganId, testMahasiswa))
                .thenThrow(new RuntimeException("Service unavailable"));

        mockMvc.perform(get("/api/lowongandaftar/{id}/status", testLowonganId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }

    @Test
    void testGetAllLowonganForMahasiswa_Success() throws Exception {
        List<Lowongan> lowonganList = Arrays.asList(testLowongan);

        LowonganDTO lowonganDTO = new LowonganDTO();
        lowonganDTO.setLowonganId(testLowonganId);
        lowonganDTO.setIdMataKuliah("CS101");
        lowonganDTO.setStatusLowongan("DIBUKA");

        List<LowonganDTO> lowonganDTOList = Arrays.asList(lowonganDTO);

        when(lowonganService.findAll()).thenReturn(lowonganList);
        when(lowonganMapper.toDtoList(lowonganList)).thenReturn(lowonganDTOList);

        Map<String, Object> listResponse = createMockResponse(200, "Lowongan list retrieved successfully");
        listResponse.put("lowongan_list", lowonganDTOList);
        listResponse.put("total_lowongan", 1);

        when(responseBuilderService.buildListResponse(
                eq("Lowongan list retrieved successfully"),
                eq(lowonganDTOList)
        )).thenReturn(ResponseEntity.ok(listResponse));

        mockMvc.perform(get("/api/lowongandaftar/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.message").value("Lowongan list retrieved successfully"))
                .andExpect(jsonPath("$.lowongan_list").isArray())
                .andExpect(jsonPath("$.lowongan_list", hasSize(1)))
                .andExpect(jsonPath("$.total_lowongan").value(1));

        verify(lowonganService).findAll();
        verify(lowonganMapper).toDtoList(lowonganList);
    }

    @Test
    void testGetAllLowonganForMahasiswa_EmptyList() throws Exception {
        List<Lowongan> emptyLowonganList = new ArrayList<>();
        List<LowonganDTO> emptyLowonganDTOList = new ArrayList<>();

        when(lowonganService.findAll()).thenReturn(emptyLowonganList);
        when(lowonganMapper.toDtoList(emptyLowonganList)).thenReturn(emptyLowonganDTOList);

        Map<String, Object> listResponse = createMockResponse(200, "Lowongan list retrieved successfully");
        listResponse.put("lowongan_list", emptyLowonganDTOList);
        listResponse.put("total_lowongan", 0);

        when(responseBuilderService.buildListResponse(
                eq("Lowongan list retrieved successfully"),
                eq(emptyLowonganDTOList)
        )).thenReturn(ResponseEntity.ok(listResponse));

        mockMvc.perform(get("/api/lowongandaftar/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowongan_list").isArray())
                .andExpect(jsonPath("$.lowongan_list", hasSize(0)))
                .andExpect(jsonPath("$.total_lowongan").value(0));
    }

    @Test
    void testGetAllLowonganForMahasiswa_ServiceError() throws Exception {
        when(lowonganService.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/lowongandaftar/list"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));

        verify(lowonganService).findAll();
    }

    @Test
    void testDaftar_LowonganNotFound() throws Exception {
        doNothing().when(daftarFormBusinessValidator).validateBusinessRules(any(DaftarForm.class));

        lenient().when(authenticationValidatorService.validateAndGetCurrentUser(any()))
                .thenReturn(testMahasiswa);
        lenient().when(applicationProcessingService.processApplication(any(UUID.class), any(DaftarForm.class), any(Mahasiswa.class)))
                .thenThrow(new NoSuchElementException("Lowongan not found"));

        mockMvc.perform(post("/api/lowongandaftar/{id}/daftar", testLowonganId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDaftarForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Lowongan tidak ditemukan dengan ID: " + testLowonganId));
    }

    @Test
    void testDaftarWithNullPrincipal() {
        doNothing().when(daftarFormBusinessValidator).validateBusinessRules(any(DaftarForm.class));

        when(authenticationValidatorService.validateAndGetCurrentUser(null))
                .thenThrow(new IllegalArgumentException("Anda harus login terlebih dahulu"));

        assertThrows(IllegalArgumentException.class, () -> {
            pendaftaranRestController.daftar(testLowonganId, testDaftarForm, null);
        });
    }

    @Test
    void testGetLowonganApplyStatusWithNullPrincipal() {
        when(authenticationValidatorService.validateAndGetCurrentUser(null))
                .thenThrow(new IllegalArgumentException("Anda harus login terlebih dahulu"));

        assertThrows(IllegalArgumentException.class, () -> {
            pendaftaranRestController.getLowonganApplyStatus(testLowonganId, null);
        });
    }

    private Map<String, Object> createMockResponse(int statusCode, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status_code", statusCode);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @Test
    void testDaftar_BusinessValidationError() throws Exception {
        DaftarForm invalidBusinessForm = new DaftarForm();
        invalidBusinessForm.setIpk(2.0);
        invalidBusinessForm.setSks(10);

        doThrow(new IllegalArgumentException("IPK minimal 2.5 untuk mendaftar lowongan"))
                .when(daftarFormBusinessValidator).validateBusinessRules(any(DaftarForm.class));

        mockMvc.perform(post("/api/lowongandaftar/{id}/daftar", testLowonganId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBusinessForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("IPK minimal 2.5 untuk mendaftar lowongan"));
    }

    @Test
    void testDaftar_IllegalStateError() throws Exception {
        doNothing().when(daftarFormBusinessValidator).validateBusinessRules(any(DaftarForm.class));

        lenient().when(authenticationValidatorService.validateAndGetCurrentUser(any()))
                .thenReturn(testMahasiswa);
        lenient().when(applicationProcessingService.processApplication(any(UUID.class), any(DaftarForm.class), any(Mahasiswa.class)))
                .thenThrow(new IllegalStateException("Kuota lowongan sudah penuh"));

        mockMvc.perform(post("/api/lowongandaftar/{id}/daftar", testLowonganId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDaftarForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid State"))
                .andExpect(jsonPath("$.message").value("Kuota lowongan sudah penuh"));
    }
}