package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DosenDtoTest {

    @Test
    void testGettersAndSetters() {
        DosenDto dosenDto = new DosenDto();
        
        dosenDto.setEmail("dosen@example.com");
        dosenDto.setPassword("password123");
        dosenDto.setFullName("Professor Name");
        dosenDto.setNip("123456789");
        
        assertEquals("dosen@example.com", dosenDto.getEmail());
        assertEquals("password123", dosenDto.getPassword());
        assertEquals("Professor Name", dosenDto.getFullName());
        assertEquals("123456789", dosenDto.getNip());
    }
}