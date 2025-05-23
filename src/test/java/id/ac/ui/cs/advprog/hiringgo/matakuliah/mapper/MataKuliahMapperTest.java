package id.ac.ui.cs.advprog.hiringgo.matakuliah.mapper;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MataKuliahMapperTest {

    private MataKuliahMapper mapper;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        mapper = new MataKuliahMapper(userRepository);
    }

    @Test
    void testToEntityWithValidDosen() {
        MataKuliahDTO dto = new MataKuliahDTO();
        dto.setKode("CS001");
        dto.setNama("Pemrograman");
        dto.setDeskripsi("Dasar Java");
        dto.setDosenPengampuEmails(List.of("dosen1@example.com"));

        Dosen dosen = mock(Dosen.class);
        when(dosen.getNip()).thenReturn("123456");
        when(userRepository.findByEmail("dosen1@example.com")).thenReturn(Optional.of(dosen));

        MataKuliah result = mapper.toEntity(dto);

        assertEquals("CS001", result.getKode());
        assertEquals("Pemrograman", result.getNama());
        assertEquals("Dasar Java", result.getDeskripsi());
        assertEquals(1, result.getDosenPengampu().size());
    }

    @Test
    void testToEntityThrowsIfUserNotFound() {
        MataKuliahDTO dto = new MataKuliahDTO();
        dto.setKode("CS002");
        dto.setNama("AI");
        dto.setDeskripsi("Dasar AI");
        dto.setDosenPengampuEmails(List.of("notfound@example.com"));

        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> mapper.toEntity(dto));
        assertEquals("Pengguna dengan email notfound@example.com tidak ditemukan", ex.getMessage());
    }

    @Test
    void testToEntityThrowsIfUserNotDosen() {
        MataKuliahDTO dto = new MataKuliahDTO();
        dto.setKode("CS003");
        dto.setNama("ML");
        dto.setDeskripsi("Machine Learning");
        dto.setDosenPengampuEmails(List.of("user@example.com"));

        User user = mock(User.class); // bukan Dosen
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> mapper.toEntity(dto));
        assertEquals("Pengguna dengan email user@example.com bukan seorang dosen", ex.getMessage());
    }

    @Test
    void testToDto() {
        Dosen dosen = mock(Dosen.class);
        when(dosen.getUsername()).thenReturn("dosen1@example.com");
        when(dosen.getNip()).thenReturn("123456");

        MataKuliah matkul = new MataKuliah("CS004", "Big Data", "Dasar BD");
        matkul.addDosenPengampu(dosen);

        MataKuliahDTO dto = mapper.toDto(matkul);

        assertEquals("CS004", dto.getKode());
        assertEquals("Big Data", dto.getNama());
        assertEquals("Dasar BD", dto.getDeskripsi());
        assertEquals(List.of("dosen1@example.com"), dto.getDosenPengampuEmails());
    }

    @Test
    void testToDtoList() {
        MataKuliah m1 = new MataKuliah("CS005", "Algo", "Deskripsi");
        MataKuliah m2 = new MataKuliah("CS006", "Statistik", "Deskripsi");

        List<MataKuliahDTO> dtos = mapper.toDtoList(List.of(m1, m2));

        assertEquals(2, dtos.size());
        assertEquals("CS005", dtos.get(0).getKode());
        assertEquals("CS006", dtos.get(1).getKode());
    }
}
