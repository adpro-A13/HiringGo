package id.ac.ui.cs.advprog.hiringgo.controller;

import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogController.class)
public class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogService logService;

    @Test
    void testCreateLog_shouldReturnCreatedLog() throws Exception {
        String requestBody = """
                {
                    "judul": "Asistensi UTS",
                    "keterangan": "Membantu asistensi UTS"
                }
                """;

        Log log = new Log();
        log.setId(1L);
        log.setJudul("Asistensi UTS");
        log.setKeterangan("Membantu asistensi UTS");

        Mockito.when(logService.createLog(Mockito.any(Log.class))).thenReturn(log);

        mockMvc.perform(post("/api/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.judul").value("Asistensi UTS"));
    }
}