package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(LowonganController.class)
class LowonganControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LowonganService lowonganService;

    @Test
    void testCreateLowonganPage() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/lowongan/create"))
                .andReturn()
                .getResponse();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    void testCreateLowonganPost() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/lowongan/create")
                        .param("idMataKuliah", "CS101")
                        .param("tahunAjaran", "2025/2026")
                        .param("semester", Semester.GANJIL.name())
                        .param("statusLowongan", StatusLowongan.DIBUKA.name())
                        .param("jumlahAsdosDibutuhkan", "5")
                        .param("jumlahAsdosDiterima", "0")
                        .param("jumlahAsdosPendaftar", "0"))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("/lowongan/list", response.getRedirectedUrl());
    }

    @Test
    void testListLowonganPage() throws Exception {
        Mockito.when(lowonganService.findAll()).thenReturn(Collections.emptyList());

        MockHttpServletResponse response = mockMvc.perform(get("/lowongan/list"))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }
}
