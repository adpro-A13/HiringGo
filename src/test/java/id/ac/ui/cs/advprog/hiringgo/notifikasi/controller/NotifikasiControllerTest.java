package id.ac.ui.cs.advprog.hiringgo.notifikasi.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.dto.NotifikasiDTO;
import id.ac.ui.cs.advprog.hiringgo.notifikasi.service.NotifikasiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class NotifikasiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotifikasiService notifikasiService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotifikasiController notifikasiController;

    private Mahasiswa mahasiswa;
    private UUID notifikasiId;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(notifikasiController).build();

        mahasiswa = new Mahasiswa();
        mahasiswa.setUsername("mahasiswa@example.com");

        notifikasiId = UUID.randomUUID();
    }

    private void setupAuthentication() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("mahasiswa@example.com");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);
    }

    @Test
    void testGetAllNotifikasi() throws Exception {
        setupAuthentication();

        NotifikasiDTO dto = NotifikasiDTO.builder()
                .id(notifikasiId)
                .mataKuliahNama("Pemrograman Lanjut")
                .tahunAjaran("2024/2025")
                .semester("GENAP")
                .status("DITERIMA")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail("mahasiswa@example.com")).thenReturn(Optional.of(mahasiswa));
        when(notifikasiService.getNotifikasiByMahasiswa(mahasiswa)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/notifikasi")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(notifikasiId.toString())))
                .andExpect(jsonPath("$[0].mataKuliahNama", is("Pemrograman Lanjut")));
    }

    @Test
    void testGetUnreadCount() throws Exception {
        setupAuthentication();

        when(userRepository.findByEmail("mahasiswa@example.com")).thenReturn(Optional.of(mahasiswa));
        when(notifikasiService.countUnreadByMahasiswa(mahasiswa)).thenReturn(3L);

        mockMvc.perform(get("/api/notifikasi/unread-count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void testMarkAsRead() throws Exception {
        doNothing().when(notifikasiService).markAsRead(notifikasiId);

        mockMvc.perform(post("/api/notifikasi/" + notifikasiId + "/read")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(notifikasiService, times(1)).markAsRead(notifikasiId);
    }
}