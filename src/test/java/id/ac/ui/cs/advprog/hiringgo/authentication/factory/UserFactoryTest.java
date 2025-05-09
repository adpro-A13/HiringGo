package id.ac.ui.cs.advprog.hiringgo.authentication.factory;

import id.ac.ui.cs.advprog.hiringgo.authentication.Enums.UserRoleEnums;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    @Test
    void createMahasiswaUser() {
        String email = "student@example.com";
        String password = "password123";
        String fullName = "Student Name";
        String nim = "12345678";
        
        User user = UserFactory.createUser(UserRoleEnums.MAHASISWA, email, password, fullName, nim);
        
        assertNotNull(user);
        assertTrue(user instanceof Mahasiswa);
        assertEquals(email, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(fullName, ((Mahasiswa) user).getFullName());
        assertEquals(nim, ((Mahasiswa) user).getNim());
    }
    
    @Test
    void createDosenUser() {
        String email = "lecturer@example.com";
        String password = "password123";
        String fullName = "Lecturer Name";
        String nip = "987654321";
        
        User user = UserFactory.createUser(UserRoleEnums.DOSEN, email, password, fullName, nip);
        
        assertNotNull(user);
        assertTrue(user instanceof Dosen);
        assertEquals(email, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(fullName, ((Dosen) user).getFullName());
        assertEquals(nip, ((Dosen) user).getNip());
    }
    
    @Test
    void createAdminUser() {
        String email = "admin@example.com";
        String password = "password123";
        String fullName = "Not Used";
        String nimOrNip = "Not Used";
        
        User user = UserFactory.createUser(UserRoleEnums.ADMIN, email, password, fullName, nimOrNip);
        
        assertNotNull(user);
        assertTrue(user instanceof Admin);
        assertEquals(email, user.getUsername());
        assertEquals(password, user.getPassword());
    }
    
    @Test
    void shouldThrowExceptionForInvalidRole() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            UserFactory.createUser(null, "email@example.com", "password", "Full Name", "12345");
        });
        
        assertNotNull(exception);
    }

    @ParameterizedTest
    @EnumSource(UserRoleEnums.class)
    void shouldCreateUserForAllKnownRoles(UserRoleEnums role) {
        String email = "test@example.com";
        String password = "password123";
        String fullName = "Test User";
        String identifier = "12345";
        
        User user = UserFactory.createUser(role, email, password, fullName, identifier);
        
        assertNotNull(user);
        
        switch (role) {
            case MAHASISWA:
                assertTrue(user instanceof Mahasiswa);
                assertEquals(fullName, ((Mahasiswa) user).getFullName());
                assertEquals(identifier, ((Mahasiswa) user).getNim());
                break;
            case DOSEN:
                assertTrue(user instanceof Dosen);
                assertEquals(fullName, ((Dosen) user).getFullName());
                assertEquals(identifier, ((Dosen) user).getNip());
                break;
            case ADMIN:
                assertTrue(user instanceof Admin);
                break;
        }
        
        assertEquals(email, user.getUsername());
        assertEquals(password, user.getPassword());
    }

    @Test
    void testUserFactoryConstructor() {
        UserFactory userFactory = new UserFactory();
        assertNotNull(userFactory);
    }
}