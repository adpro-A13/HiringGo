package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminDtoTest {

    @Test
    void testGettersAndSetters() {
        AdminDto adminDto = new AdminDto();
        
        adminDto.setEmail("admin@example.com");
        adminDto.setPassword("password123");
        
        assertEquals("admin@example.com", adminDto.getEmail());
        assertEquals("password123", adminDto.getPassword());
    }
}