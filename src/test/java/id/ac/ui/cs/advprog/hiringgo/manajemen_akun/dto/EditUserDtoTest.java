package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EditUserDtoTest {

    @Test
    void testGettersAndSetters() {
        EditUserDto editUserDto = new EditUserDto();
        
        editUserDto.setNewRole("ADMIN");
        
        assertEquals("ADMIN", editUserDto.getNewRole());
    }
}