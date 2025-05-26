package id.ac.ui.cs.advprog.hiringgo.log.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import id.ac.ui.cs.advprog.hiringgo.log.dto.request.CreateLogRequest;
import id.ac.ui.cs.advprog.hiringgo.log.dto.response.LowonganWithPendaftaranDTO;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LogService logService;

    @InjectMocks
    private LogController logController;

    private Log sampleLog;
    private CreateLogRequest createLogRequest;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(logController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleLog = new Log.Builder()
                .id(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))
                .judul("Test Log")
                .keterangan("This is a test log")
                .kategori(LogKategori.LAIN_LAIN)
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .tanggalLog(LocalDate.now())
                .pesanUntukDosen("Test message for the teacher")
                .status(LogStatus.MENUNGGU)
                .build();

        // Create a sample CreateLogRequest
        createLogRequest = new CreateLogRequest();
        // Set properties of createLogRequest
        // (Assuming CreateLogRequest has setters for these properties)
        // Set properties based on your actual CreateLogRequest class
    }

    @Test
    void createLog_shouldReturnCreatedLog() throws Exception {
        when(logService.createLog(any(CreateLogRequest.class))).thenReturn(sampleLog);

        mockMvc.perform(post("/api/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLogRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))
                .andExpect(jsonPath("$.judul").value("Test Log"));
    }

    @Test
    void getAllLogs_shouldReturnLogsList() throws Exception {
        when(logService.getAllLogs()).thenReturn(Collections.singletonList(sampleLog));

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Test Log"));
    }

    @Test
    void testGetLogsByDosenMataKuliah() throws Exception {
        UUID dosenId = UUID.randomUUID();

        Log log = new Log();
        List<Log> logs = List.of(log);

        when(logService.getLogsByDosenMataKuliah(dosenId)).thenReturn(logs);

        mockMvc.perform(get("/api/logs/dosen/" + dosenId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getLogsByDosenMataKuliah_shouldReturn403_whenRuntimeExceptionThrown() throws Exception {
        UUID dosenId = UUID.randomUUID();
        when(logService.getLogsByDosenMataKuliah(dosenId)).thenThrow(new RuntimeException("Unauthorized"));

        mockMvc.perform(get("/api/logs/dosen/" + dosenId))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Unauthorized"));
    }

    @Test
    void getLogsByUser_shouldReturnLogsList() throws Exception {
        UUID userId = UUID.randomUUID();
        when(logService.getLogsByUser(userId)).thenReturn(Collections.singletonList(sampleLog));

        mockMvc.perform(get("/api/logs/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Test Log"));
    }

    @Test
    void getLogById_shouldReturnLog() throws Exception {
        when(logService.getLogById(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))).thenReturn(Optional.of(sampleLog));

        mockMvc.perform(get("/api/logs/{id}", UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.judul").value("Test Log"));
    }

    @Test
    void getLogById_shouldReturnNotFound() throws Exception {
        when(logService.getLogById(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/logs/{id}", UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401")))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Log not found"));
    }

    @Test
    void getLogsByStatus_shouldReturnLogs() throws Exception {
        when(logService.getLogsByStatus(LogStatus.MENUNGGU)).thenReturn(Arrays.asList(sampleLog));

        mockMvc.perform(get("/api/logs/status/{status}", LogStatus.MENUNGGU))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Test Log"));
    }

    @Test
    void getLogsByDateRange_shouldReturnLogs() throws Exception {
        UUID userId = UUID.randomUUID();

        when(logService.getLogsByMonth(any(Integer.class), any(Integer.class), any(UUID.class)))
                .thenReturn(CompletableFuture.completedFuture(Collections.singletonList(sampleLog)));

        mockMvc.perform(get("/api/logs/month")
                        .param("id", String.valueOf(userId))
                        .param("bulan", String.valueOf(LocalDate.now().getMonthValue()))
                        .param("tahun", String.valueOf(LocalDate.now().getYear())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Test Log"));
    }

    @Test
    void getLogsByMonth_shouldReturn500_whenInterruptedExceptionThrown() throws Exception {
        UUID id = UUID.randomUUID();
        CompletableFuture<List<Log>> future = CompletableFuture.failedFuture(new InterruptedException("Interrupted"));

        when(logService.getLogsByMonth(anyInt(), anyInt(), eq(id)))
                .thenReturn(future);

        mockMvc.perform(get("/api/logs/month")
                        .param("id", id.toString())
                        .param("bulan", "5")
                        .param("tahun", "2025"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getLogsByMonth_shouldReturn500_whenGeneralExceptionThrown() throws Exception {
        UUID id = UUID.randomUUID();
        when(logService.getLogsByMonth(anyInt(), anyInt(), eq(id)))
                .thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(get("/api/logs/month")
                        .param("id", id.toString())
                        .param("bulan", "5")
                        .param("tahun", "2025"))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void updateLogStatus_shouldReturnUpdatedLog() throws Exception {
        Log updatedLog = new Log.Builder()
                .id(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))
                .judul("Test Log")
                .status(LogStatus.DITERIMA)
                .build();

        // Mock the command pattern behavior
        when(logService.updateStatus(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"),
                LogStatus.DITERIMA)).thenReturn(updatedLog);

        mockMvc.perform(patch("/api/logs/{id}/status", UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"DITERIMA\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DITERIMA"));
    }

    @Test
    void updateLog_shouldReturnUpdatedLog() throws Exception {
        Log updatedLog = new Log.Builder()
                .id(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))
                .judul("Updated Test Log")
                .keterangan("Updated description")
                .kategori(LogKategori.LAIN_LAIN)
                .waktuMulai(LocalTime.of(11, 0))
                .waktuSelesai(LocalTime.of(13, 0))
                .tanggalLog(LocalDate.now())
                .pesanUntukDosen("Updated message for the teacher")
                .status(LogStatus.MENUNGGU)
                .build();

        when(logService.updateLog(eq(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401")), any(Log.class))).thenReturn(updatedLog);

        mockMvc.perform(put("/api/logs/{id}", UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))
                .andExpect(jsonPath("$.judul").value("Updated Test Log"));
    }

    @Test
    void deleteLog_shouldReturnNoContent() throws Exception {
        doNothing().when(logService).deleteLog(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"));

        mockMvc.perform(delete("/api/logs/{id}", UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401")))
                .andExpect(status().isNoContent());
    }

    @Test
    void createLog_shouldHandleValidationError() throws Exception {
        when(logService.createLog(any(CreateLogRequest.class)))
                .thenThrow(new IllegalArgumentException("Waktu mulai dan selesai tidak boleh kosong"));

        mockMvc.perform(post("/api/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLogRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Waktu mulai dan selesai tidak boleh kosong"));
    }

    @Test
    void createLog_shouldHandleServerError() throws Exception {
        when(logService.createLog(any(CreateLogRequest.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(post("/api/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLogRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error creating log"));
    }

    @Test
    void updateLogStatus_shouldHandleNotFound() throws Exception {
        when(logService.updateStatus(eq(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25")), any(LogStatus.class)))
                .thenThrow(new RuntimeException("Log tidak ditemukan"));

        mockMvc.perform(patch("/api/logs/{id}/status", UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"DITERIMA\""))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Log tidak ditemukan"));
    }

    @Test
    void updateLog_shouldHandleNotFound() throws Exception {
        Log updatedLog = new Log.Builder()
                .id(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"))
                .judul("Updated Test Log")
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .build();

        when(logService.updateLog(eq(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25")), any(Log.class)))
                .thenThrow(new RuntimeException("Log tidak ditemukan"));

        mockMvc.perform(put("/api/logs/{id}", UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLog)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Log tidak ditemukan"));
    }

    @Test
    void deleteLog_shouldHandleNotFound() throws Exception {
        doThrow(new RuntimeException("Log tidak ditemukan"))
                .when(logService).deleteLog(UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25"));

        mockMvc.perform(delete("/api/logs/{id}", UUID.fromString("90c05a1f-4183-401c-a0fe-09ebd943da25")))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Log tidak ditemukan"));
    }

    @Test
    void getLowonganYangDiterima_shouldReturnListLowonganWithPendaftaran() throws Exception {
        UUID lowonganId = UUID.randomUUID();

        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);

        List<Pendaftaran> daftarPendaftaran = Collections.emptyList(); // atau mock sesuai kebutuhan

        LowonganWithPendaftaranDTO dto = new LowonganWithPendaftaranDTO(lowongan, daftarPendaftaran);

        when(logService.getLowonganYangDiterima())
                .thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/logs/listLowongan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lowongan.lowonganId").value(lowonganId.toString()))
                .andExpect(jsonPath("$[0].pendaftaranUser").isArray());
    }

    @Test
    void getLogsByDosenMataKuliah_shouldReturnError_whenGeneralExceptionThrown() throws Exception {
        UUID dosenId = UUID.randomUUID();
        when(logService.getLogsByDosenMataKuliah(dosenId)).thenThrow(new NullPointerException("Database error"));

        mockMvc.perform(get("/api/logs/dosen/" + dosenId))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Database error"));
    }

    @Test
    void updateLogStatus_shouldReturnError_whenGeneralExceptionThrown() throws Exception {
        UUID logId = UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401");

        // Mock the command pattern to throw a general exception
        when(logService.updateStatus(eq(logId), any(LogStatus.class)))
                .thenThrow(new NullPointerException("Database connection failed"));

        mockMvc.perform(patch("/api/logs/{id}/status", logId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"DITERIMA\""))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Database connection failed"));
    }

    @Test
    void updateLog_shouldHandleValidationError() throws Exception {
        Log updatedLog = new Log.Builder()
                .id(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))
                .judul("Updated Test Log")
                .build();

        when(logService.updateLog(eq(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401")), any(Log.class)))
                .thenThrow(new IllegalArgumentException("Invalid log data"));

        mockMvc.perform(put("/api/logs/{id}", UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLog)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid log data"));
    }

    @Test
    void updateLog_shouldReturnError_whenGeneralExceptionThrown() throws Exception {
        Log updatedLog = new Log.Builder()
                .id(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))
                .judul("Updated Test Log")
                .build();

        when(logService.updateLog(eq(UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401")), any(Log.class)))
                .thenThrow(new NullPointerException("Database error"));

        mockMvc.perform(put("/api/logs/{id}", UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLog)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Database error"));
    }

    @Test
    void deleteLog_shouldReturnError_whenGeneralExceptionThrown() throws Exception {
        UUID logId = UUID.fromString("f0a26f9d-bf79-4d90-9be6-90d8963f3401");

        doThrow(new NullPointerException("Database connection failed"))
                .when(logService).deleteLog(logId);

        mockMvc.perform(delete("/api/logs/{id}", logId))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Database connection failed"));
    }

}