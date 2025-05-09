package id.ac.ui.cs.advprog.hiringgo.authentication.Enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleEnumsTest {

    @ParameterizedTest
    @EnumSource(UserRoleEnums.class)
    void getValue_ShouldReturnEnumName(UserRoleEnums role) {
        assertEquals(role.name(), role.getValue());
    }
    
    @Test
    void fromValue_WithValidValues() {
        assertEquals(UserRoleEnums.ADMIN, UserRoleEnums.fromValue("ADMIN"));
        assertEquals(UserRoleEnums.DOSEN, UserRoleEnums.fromValue("DOSEN"));
        assertEquals(UserRoleEnums.MAHASISWA, UserRoleEnums.fromValue("MAHASISWA"));
    }
    
    @Test
    void fromValue_WithNull() {
        assertNull(UserRoleEnums.fromValue(null));
    }
    
    @Test
    void fromValue_WithInvalidValue() {
        assertNull(UserRoleEnums.fromValue("INVALID_ROLE"));
    }
}