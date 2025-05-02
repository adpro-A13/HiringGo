package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.config.JwtAuthenticationFilter;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(LowonganController.class)
public class LowonganControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private LowonganService lowonganService;


    @Test
    @WithMockUser(username = "user", roles = {"USER"}) // Ganti "USER" jika controller butuh role tertentu
    void testCreateLowonganPage() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/lowongan/create"))
                .andReturn()
                .getResponse();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }



    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})  // atau sesuaikan role-nya
    void testListLowonganPage() throws Exception {
        Mockito.when(lowonganService.findAll()).thenReturn(Collections.emptyList());

        MockHttpServletResponse response = mockMvc.perform(get("/lowongan/list"))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }
}
