package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeRoleDtoTest {

    @Test
    void testGettersAndSetters() {
        ChangeRoleDto changeRoleDto = new ChangeRoleDto();
        
        changeRoleDto.setNewRole("ADMIN");
        
        assertEquals("ADMIN", changeRoleDto.getNewRole());
    }
}