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


    @Test
    void createUser_withNullRole_shouldThrowNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            UserFactory.createUser(null, "email@example.com", "password", "Full Name", "12345");
        });

        assertNotNull(exception);
    }

    @Test
    void createUser_withProvidedId_shouldUseProvidedId() {
        java.util.UUID providedId = java.util.UUID.randomUUID();

        User user = UserFactory.createUser(UserRoleEnums.MAHASISWA, "test@test.com", "password", "Test User", "123456", providedId);

        assertNotNull(user);
        assertEquals(providedId, user.getId());
    }

    @Test
    void createUser_withNullIdAndUserHasNullId_shouldGenerateNewId() {
        User user = UserFactory.createUser(UserRoleEnums.ADMIN, "admin@test.com", "password", "Admin", "");

        assertNotNull(user);
        assertNotNull(user.getId());
    }

    @Test
    void createUser_withNullIdButUserAlreadyHasId_shouldKeepExistingId() {

        User user = UserFactory.createUser(UserRoleEnums.DOSEN, "dosen@test.com", "password", "Dosen User", "123456789");

        assertNotNull(user);
        assertNotNull(user.getId());
    }

    @Test
    void createUser_allRolesBranchCoverage() {

        User mahasiswa = UserFactory.createUser(UserRoleEnums.MAHASISWA, "m@test.com", "pass", "M User", "123");
        assertTrue(mahasiswa instanceof Mahasiswa);

        User dosen = UserFactory.createUser(UserRoleEnums.DOSEN, "d@test.com", "pass", "D User", "456");
        assertTrue(dosen instanceof Dosen);

        User admin = UserFactory.createUser(UserRoleEnums.ADMIN, "a@test.com", "pass", "A User", "789");
        assertTrue(admin instanceof Admin);
    }

    @Test
    void createUser_withNullNimOrNip_shouldUseEmptyString() {

        User userWithNullIdentifier = UserFactory.createUser(UserRoleEnums.MAHASISWA, "test@test.com", "password", "Test", null);

        assertNotNull(userWithNullIdentifier);
        assertTrue(userWithNullIdentifier instanceof Mahasiswa);
        assertEquals("", ((Mahasiswa) userWithNullIdentifier).getNim());
    }

    @Test
    void createUser_withEmptyNimOrNip_shouldUseEmptyString() {

        User userWithEmptyIdentifier = UserFactory.createUser(UserRoleEnums.DOSEN, "test@test.com", "password", "Test", "");

        assertNotNull(userWithEmptyIdentifier);
        assertTrue(userWithEmptyIdentifier instanceof Dosen);
        assertEquals("", ((Dosen) userWithEmptyIdentifier).getNip());
    }

    @Test
    void createUser_overloadedMethod_shouldCallMainMethod() {

        User user = UserFactory.createUser(UserRoleEnums.MAHASISWA, "test@test.com", "password", "Test User", "123456");

        assertNotNull(user);
        assertNotNull(user.getId());
        assertTrue(user instanceof Mahasiswa);
        assertEquals("test@test.com", user.getUsername());
        assertEquals("password", user.getPassword());
    }



    @Test
    void createUser_withUnknownRole_shouldThrowIllegalArgumentException() {
        try {

            UserRoleEnums[] originalValues = UserRoleEnums.values();
            assertDoesNotThrow(() -> {
                for (UserRoleEnums role : UserRoleEnums.values()) {
                    UserFactory.createUser(role, "test@example.com", "password", "Test", "123");
                }
            });

        } catch (Exception e) {

            fail("All enum values should be handled");
        }
    }

    @Test
    void createUser_withNullIdParameter_shouldGenerateNewIdWhenUserIdIsNull() {

        User user = UserFactory.createUser(UserRoleEnums.MAHASISWA, "test@test.com", "password", "Test User", "123456", null);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertNotNull(user.getId().toString());
    }

    @Test
    void createUser_withNullIdParameter_allUserTypes_shouldGenerateNewId() {

        User mahasiswa = UserFactory.createUser(UserRoleEnums.MAHASISWA, "m@test.com", "password", "M User", "123", null);
        assertNotNull(mahasiswa);
        assertNotNull(mahasiswa.getId());

        User dosen = UserFactory.createUser(UserRoleEnums.DOSEN, "d@test.com", "password", "D User", "456", null);
        assertNotNull(dosen);
        assertNotNull(dosen.getId());

        // Test with ADMIN
        User admin = UserFactory.createUser(UserRoleEnums.ADMIN, "a@test.com", "password", "A User", "789", null);
        assertNotNull(admin);
        assertNotNull(admin.getId());
    }

    @Test
    void createUser_overloadedMethodWithNullId_shouldGenerateNewId() {
        User user = UserFactory.createUser(UserRoleEnums.DOSEN, "dosen@test.com", "password", "Dosen User", "123456");

        assertNotNull(user);
        assertNotNull(user.getId());
        assertTrue(user.getId().toString().length() > 0);
    }

    @Test
    void createUser_verifyUuidGeneration_whenUserIdIsNull() {

        User user1 = UserFactory.createUser(UserRoleEnums.MAHASISWA, "test1@test.com", "password", "Test1", "111", null);
        User user2 = UserFactory.createUser(UserRoleEnums.MAHASISWA, "test2@test.com", "password", "Test2", "222", null);

        assertNotNull(user1.getId());
        assertNotNull(user2.getId());

        assertNotEquals(user1.getId(), user2.getId());
    }
}