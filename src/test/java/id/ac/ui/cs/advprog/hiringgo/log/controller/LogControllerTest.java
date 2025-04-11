package id.ac.ui.cs.advprog.hiringgo.log.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.hiringgo.log.dto.request.CreateLogRequest;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebMvcTest(LogController.class)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogService logService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateLog() throws Exception {
        CreateLogRequest request = new CreateLogRequest();
        request.setJudul("Asistensi");
        request.setWaktuMulai(LocalTime.of(8, 0));
        request.setWaktuSelesai(LocalTime.of(9, 0));
        request.setTanggalLog(LocalDate.now());

        Log log = new Log.Builder()
                .judul("Asistensi")
                .waktuMulai(LocalTime.of(8, 0))
                .waktuSelesai(LocalTime.of(9, 0))
                .tanggalLog(LocalDate.now())
                .build();

        when(logService.createLog(any(Log.class))).thenReturn(log);

        mockMvc.perform(post("/api/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.judul").value("Asistensi"));
    }

    @Test
    void testGetLogsByStatus() throws Exception {
        Log log = new Log.Builder()
                .judul("Asistensi")
                .build();

        when(logService.getLogsByStatus(LogStatus.MENUNGGU)).thenReturn(List.of(log));

        mockMvc.perform(get("/api/log/status/MENUNGGU"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].judul").value("Asistensi"));
    }

    @Test
    void testGetLogsByTanggal() throws Exception {
        Log log = new Log.Builder()
                .judul("Asistensi")
                .build();

        when(logService.getLogsByTanggal(any(), any())).thenReturn(List.of(log));

        mockMvc.perform(get("/api/log/tanggal")
                        .param("from", "2024-04-01")
                        .param("to", "2024-04-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].judul").value("Asistensi"));
    }

    @Test
    void testUpdateStatus() throws Exception {
        Log log = new Log.Builder()
                .status(LogStatus.DITERIMA)
                .build();

        when(logService.updateStatus(1L, LogStatus.DITERIMA)).thenReturn(log);

        mockMvc.perform(put("/api/log/1/status/DITERIMA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DITERIMA"));
    }
}