package id.ac.ui.cs.advprog.hiringgo.log.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogKategori;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Trim;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

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

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(logController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Sample Log for testing using Builder pattern
        sampleLog = new Log.Builder()
                .id(1L)
                .judul("Test Log")
                .keterangan("This is a test log")
                .kategori(LogKategori.LAIN_LAIN)  // Assuming you have categories defined
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .tanggalLog(LocalDate.now())
                .pesanUntukDosen("Test message for the teacher")
                .status(LogStatus.MENUNGGU)
                .build();
    }

    @Test
    void createLog_shouldReturnCreatedLog() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(logService.createLog(any(Log.class))).thenReturn(sampleLog);

        mockMvc.perform(post("/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleLog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.judul").value("Test Log"));
    }

    @Test
    void getAllLogs_shouldReturnLogsList() throws Exception {
        when(logService.getAllLogs()).thenReturn(Collections.singletonList(sampleLog));

        mockMvc.perform(get("/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Test Log"));
    }

    @Test
    void getLogById_shouldReturnLog() throws Exception {
        when(logService.getLogById(1L)).thenReturn(Optional.of(sampleLog));

        mockMvc.perform(get("/logs/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.judul").value("Test Log"));
    }

    @Test
    void getLogById_shouldReturnNotFound() throws Exception {
        when(logService.getLogById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/logs/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Log not found"));
    }

    @Test
    void getLogsByStatus_shouldReturnLogs() throws Exception {
        when(logService.getLogsByStatus(LogStatus.MENUNGGU)).thenReturn(Arrays.asList(sampleLog));

        mockMvc.perform(get("/logs/status/{status}", LogStatus.MENUNGGU))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Test Log"));
    }

    @Test
    void getLogsByDateRange_shouldReturnLogs() throws Exception {
        when(logService.getLogsByMonth(any(Integer.class), any(Integer.class)))
                .thenReturn(Collections.singletonList(sampleLog));

        mockMvc.perform(get("/logs/month")
                        .param("bulan", String.valueOf(LocalDate.now().getMonthValue()))
                        .param("tahun", String.valueOf(LocalDate.now().getYear())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Test Log"));
    }

    @Test
    void updateLogStatus_shouldReturnUpdatedLog() throws Exception {
        Log updatedLog = new Log.Builder()
                .id(1L)
                .judul("Test Log")
                .status(LogStatus.DITERIMA)
                .build();

        when(logService.updateStatus(1L, LogStatus.DITERIMA)).thenReturn(updatedLog);

        mockMvc.perform(patch("/logs/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"DITERIMA\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DITERIMA"));
    }

    @Test
    void updateLog_shouldReturnUpdatedLog() throws Exception {
        // Updated log that you want to test
        Log updatedLog = new Log.Builder()
                .id(1L)  // Ensure id here is 1L to match the request
                .judul("Updated Test Log")
                .keterangan("Updated description")
                .kategori(LogKategori.LAIN_LAIN)
                .waktuMulai(LocalTime.of(11, 0))
                .waktuSelesai(LocalTime.of(13, 0))
                .tanggalLog(LocalDate.now())
                .pesanUntukDosen("Updated message for the teacher")
                .status(LogStatus.MENUNGGU)
                .build();

        // Simulate the behavior of the log service and ensure the log with id 1L exists
        when(logService.updateLog(eq(1L), any(Log.class))).thenReturn(updatedLog);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Perform the update operation
        mockMvc.perform(put("/logs/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L)) // id should match 1L
                .andExpect(jsonPath("$.judul").value("Updated Test Log"));
    }

    @Test
    void deleteLog_shouldReturnNoContent() throws Exception {
        doNothing().when(logService).deleteLog(1L);

        mockMvc.perform(delete("/logs/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void createLog_shouldHandleValidationError() throws Exception {
        Log invalidLog = new Log.Builder()
                .judul("Invalid Log")
                .build();

        when(logService.createLog(any(Log.class)))
                .thenThrow(new IllegalArgumentException("Waktu mulai dan selesai tidak boleh kosong"));

        mockMvc.perform(post("/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLog)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Waktu mulai dan selesai tidak boleh kosong"));
    }

    @Test
    void createLog_shouldHandleServerError() throws Exception {
        Log log = new Log.Builder()
                .judul("Test Log")
                .build();

        when(logService.createLog(any(Log.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(post("/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(log)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error creating log"));
    }

    @Test
    void updateLogStatus_shouldHandleNotFound() throws Exception {
        when(logService.updateStatus(eq(999L), any(LogStatus.class)))
                .thenThrow(new RuntimeException("Log tidak ditemukan"));

        mockMvc.perform(patch("/logs/{id}/status", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"DITERIMA\""))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Log tidak ditemukan"));
    }

    @Test
    void updateLog_shouldHandleNotFound() throws Exception {
        Log updatedLog = new Log.Builder()
                .id(999L)
                .judul("Updated Test Log")
                .waktuMulai(LocalTime.of(10, 0))
                .waktuSelesai(LocalTime.of(12, 0))
                .build();

        when(logService.updateLog(eq(999L), any(Log.class)))
                .thenThrow(new RuntimeException("Log tidak ditemukan"));

        mockMvc.perform(put("/logs/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLog)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Log tidak ditemukan"));
    }

    @Test
    void deleteLog_shouldHandleNotFound() throws Exception {
        doThrow(new RuntimeException("Log tidak ditemukan"))
                .when(logService).deleteLog(999L);

        mockMvc.perform(delete("/logs/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Log tidak ditemukan"));
    }

}
