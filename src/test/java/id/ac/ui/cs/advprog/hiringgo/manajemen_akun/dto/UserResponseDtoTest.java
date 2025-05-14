package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserResponseDtoTest {

    @Test
    void testGettersAndSetters() {
        UserResponseDto userResponseDto = new UserResponseDto();
        
        userResponseDto.setId("123e4567-e89b-12d3-a456-426614174000");
        userResponseDto.setEmail("user@example.com");
        userResponseDto.setRole("DOSEN");
        userResponseDto.setFullName("User Name");
        userResponseDto.setNim("87654321");
        userResponseDto.setNip("12345678");
        
        assertEquals("123e4567-e89b-12d3-a456-426614174000", userResponseDto.getId());
        assertEquals("user@example.com", userResponseDto.getEmail());
        assertEquals("DOSEN", userResponseDto.getRole());
        assertEquals("User Name", userResponseDto.getFullName());
        assertEquals("87654321", userResponseDto.getNim());
        assertEquals("12345678", userResponseDto.getNip());
    }
}