package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.PendaftaranRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PendaftaranServiceTest {
    @Mock LowonganRepository lowRepo;
    @Mock PendaftaranRepository pndRepo;
    @InjectMocks PendaftaranServiceImpl svc;

    @Test
    void shouldFailWhenLowonganNotFound() {
        UUID id = UUID.randomUUID();
        when(lowRepo.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                svc.daftar(id, "user1", BigDecimal.ONE, 10)
        );
        assertEquals("Lowongan tidak ditemukan", ex.getMessage());
        verify(pndRepo, never()).save(any());
    }
}
