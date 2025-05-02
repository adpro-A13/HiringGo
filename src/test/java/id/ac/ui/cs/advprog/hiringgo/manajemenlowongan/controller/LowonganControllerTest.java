package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.config.JwtAuthenticationFilter;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service.LowonganService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LowonganController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(LowonganControllerTest.TestConfig.class)
class LowonganControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LowonganService lowonganService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LowonganService lowonganService() {
            return org.mockito.Mockito.mock(LowonganService.class);
        }

        @Bean
        public JwtService jwtService() {
            return org.mockito.Mockito.mock(JwtService.class);
        }

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
            return org.mockito.Mockito.mock(JwtAuthenticationFilter.class);
        }
    }

    @Test
    @WithMockUser
    void shouldShowCreateForm() throws Exception {
        mockMvc.perform(get("/lowongan/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("manajemenlowongan/createLowongan"))
                .andExpect(model().attributeExists("lowongan"))
                .andExpect(model().attribute("semesterList", Semester.values()))
                .andExpect(model().attribute("statusList", StatusLowongan.values()));
    }

    @Test
    @WithMockUser
    void shouldHandleFormSubmissionAndRedirect() throws Exception {
        mockMvc.perform(post("/lowongan/create").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lowongan/list"));

        verify(lowonganService).createLowongan(any(Lowongan.class));
    }

    @Test
    @WithMockUser
    void shouldShowListPage() throws Exception {
        List<Lowongan> sample = Arrays.asList(new Lowongan(), new Lowongan());
        when(lowonganService.findAll()).thenReturn(sample);

        mockMvc.perform(get("/lowongan/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("manajemenlowongan/listLowongan"))
                .andExpect(model().attribute("lowonganList", sample));

        verify(lowonganService).findAll();
    }

    @Test
    @WithMockUser
    void shouldHandleDeleteAndRedirect() throws Exception {
        UUID idLowongan = UUID.randomUUID(); // ID yang akan di-delete

        mockMvc.perform(post("/lowongan/delete").param("id", idLowongan.toString()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lowongan/list"));

        verify(lowonganService).deleteLowonganById(eq(idLowongan)); // Verifikasi bahwa service dipanggil dengan ID yang benar
    }

}