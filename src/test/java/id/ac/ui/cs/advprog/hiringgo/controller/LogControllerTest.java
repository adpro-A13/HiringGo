package id.ac.ui.cs.advprog.hiringgo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogController.class)
@ExtendWith(SpringExtension.class)
class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    private LogService logService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        logService = Mockito.mock(LogService.class);
        ((ConfigurableApplicationContext) context).getBeanFactory()
                .registerSingleton(LogService.class.getName(), logService);
    }

    @Test
    void testCreateLogSuccess() throws Exception {
        Log requestLog = new Log();
        requestLog.setJudul("Asistensi UTS");
        requestLog.setKeterangan("Membantu asistensi UTS");

        Log responseLog = new Log();
        responseLog.setId(1L);
        responseLog.setJudul("Asistensi UTS");
        responseLog.setKeterangan("Membantu asistensi UTS");
        responseLog.setWaktuMulai(LocalTime.of(9, 0));
        responseLog.setWaktuSelesai(LocalTime.of(11, 0));
        responseLog.setTanggalLog(LocalDate.now());

        Mockito.when(logService.createLog(Mockito.any(Log.class))).thenReturn(responseLog);

        mockMvc.perform(post("/api/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.judul").value("Asistensi UTS"))
                .andExpect(jsonPath("$.keterangan").value("Membantu asistensi UTS"));
    }
}