package id.ac.ui.cs.advprog.hiringgo.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebMvcTest(LogController.class)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogService logService;

    @Test
    void testCreateLog() throws Exception {
        Log log = new Log();
        log.setJudul("Asistensi");
        log.setWaktuMulai(LocalTime.of(8, 0));
        log.setWaktuSelesai(LocalTime.of(9, 0));
        log.setTanggalLog(LocalDate.now());

        when(logService.createLog(any(Log.class))).thenReturn(log);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/log")
                        .contentType("application/json")
                        .content("{\"judul\":\"Asistensi\",\"waktuMulai\":\"08:00\",\"waktuSelesai\":\"09:00\",\"tanggalLog\":\"2024-04-11\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetLogsByStatus() throws Exception {
        when(logService.getLogsByStatus(LogStatus.MENUNGGU)).thenReturn(List.of(new Log()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/log/status/MENUNGGU"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetLogsByTanggal() throws Exception {
        when(logService.getLogsByTanggal(any(), any())).thenReturn(List.of(new Log()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/log/tanggal")
                        .param("from", "2024-04-01")
                        .param("to", "2024-04-11"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStatus() throws Exception {
        Log log = new Log();
        log.setId(1L);
        log.setStatus(LogStatus.DITERIMA);

        when(logService.updateStatus(1L, LogStatus.DITERIMA)).thenReturn(log);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/log/1/status/DITERIMA"))
                .andExpect(status().isOk());
    }
}
