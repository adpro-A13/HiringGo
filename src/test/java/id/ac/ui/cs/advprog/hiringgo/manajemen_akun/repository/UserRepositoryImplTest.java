package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.repository;

import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.factory.UserFactory;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.User;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {
    
    @Mock
    private UserFactory userFactory;
    
    private UserRepositoryImpl userRepository;
    
    private Admin testAdmin;
    private Dosen testDosen;
    private Mahasiswa testMahasiswa;
    
    @BeforeEach
    void setUp() {
        testAdmin = new Admin("admin@hiringgo.com", "admin123");
        when(userFactory.createAdmin("admin@hiringgo.com", "admin123")).thenReturn(testAdmin);

        userRepository = new UserRepositoryImpl(userFactory);
        
        testDosen = new Dosen("D12345", "Dr. Test", "dosen@test.com", "pass123");
        testMahasiswa = new Mahasiswa("Student Test", "M12345", "student@test.com", "pass456");
    }
    
    @Test
    void testSaveUser() {
        User savedDosen = userRepository.save(testDosen);
        User savedMahasiswa = userRepository.save(testMahasiswa);
        
        assertEquals(testDosen, savedDosen);
        assertEquals(testMahasiswa, savedMahasiswa);
    }
    
    @Test
    void testSaveUserWithExistingEmail() {
        userRepository.save(testDosen);
        
        assertThrows(IllegalArgumentException.class, () -> {
            Dosen duplicateEmailUser = new Dosen("D99999", "Another Dosen", "dosen@test.com", "anotherpass");
            userRepository.save(duplicateEmailUser);
        });
    }
    
    @Test
    void testFindAll() {
        userRepository.save(testDosen);
        userRepository.save(testMahasiswa);
        
        List<User> allUsers = userRepository.findAll();
        
        assertEquals(3, allUsers.size());
        assertTrue(allUsers.contains(testAdmin));
        assertTrue(allUsers.contains(testDosen));
        assertTrue(allUsers.contains(testMahasiswa));
    }
    
    @Test
    void testFindByEmail() {
        userRepository.save(testDosen);
        
        Optional<User> foundDosen = userRepository.findByEmail("dosen@test.com");
        Optional<User> nonExistentUser = userRepository.findByEmail("nonexistent@test.com");
        
        assertTrue(foundDosen.isPresent());
        assertEquals(testDosen, foundDosen.get());
        
        assertTrue(nonExistentUser.isEmpty());
    }
    
    @Test
    void testFindByRole() {
        userRepository.save(testDosen);
        userRepository.save(testMahasiswa);
        
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        List<User> dosens = userRepository.findByRole(UserRole.DOSEN);
        List<User> mahasiswas = userRepository.findByRole(UserRole.MAHASISWA);
        
        assertEquals(1, admins.size());
        assertTrue(admins.contains(testAdmin));
        
        assertEquals(1, dosens.size());
        assertTrue(dosens.contains(testDosen));
        
        assertEquals(1, mahasiswas.size());
        assertTrue(mahasiswas.contains(testMahasiswa));
    }
    
    @Test
    void testDeleteByEmail() {
        userRepository.save(testDosen);
        
        boolean deleted = userRepository.deleteByEmail("dosen@test.com");
        boolean nonExistentDeleted = userRepository.deleteByEmail("nonexistent@test.com");
        
        assertTrue(deleted);
        assertFalse(nonExistentDeleted);
        
        Optional<User> deletedUser = userRepository.findByEmail("dosen@test.com");
        assertTrue(deletedUser.isEmpty());
    }
    
    @Test
    void testUpdateUserToAdmin() {
        userRepository.save(testDosen);
        
        Admin convertedAdmin = new Admin("dosen@test.com", "pass123");
        when(userFactory.convertToAdmin(testDosen)).thenReturn(convertedAdmin);
        
        boolean updated = userRepository.updateUserToAdmin("dosen@test.com");
        boolean nonExistentUpdated = userRepository.updateUserToAdmin("nonexistent@test.com");
        
        assertTrue(updated);
        assertFalse(nonExistentUpdated);
        
        Optional<User> updatedUser = userRepository.findByEmail("dosen@test.com");
        assertTrue(updatedUser.isPresent());
        assertEquals(UserRole.ADMIN, updatedUser.get().getRole());
        assertTrue(updatedUser.get() instanceof Admin);
    }
    
    @Test
    void testUpdateUserToDosen() {
        userRepository.save(testMahasiswa);
        
        Dosen convertedDosen = new Dosen("D54321", "Dr. Former Student", "student@test.com", "pass456");
        when(userFactory.convertToDosen(testMahasiswa, "D54321", "Dr. Former Student")).thenReturn(convertedDosen);
        
        boolean updated = userRepository.updateUserToDosen("student@test.com", "D54321", "Dr. Former Student");
        boolean nonExistentUpdated = userRepository.updateUserToDosen("nonexistent@test.com", "D99999", "Non Existent");
        
        assertTrue(updated);
        assertFalse(nonExistentUpdated);
        
        Optional<User> updatedUser = userRepository.findByEmail("student@test.com");
        assertTrue(updatedUser.isPresent());
        assertEquals(UserRole.DOSEN, updatedUser.get().getRole());
        assertTrue(updatedUser.get() instanceof Dosen);
        Dosen castedDosen = (Dosen) updatedUser.get();
        assertEquals("D54321", castedDosen.getNip());
        assertEquals("Dr. Former Student", castedDosen.getName());
    }
    
    @Test
    void testUpdateUserToMahasiswa() {
        userRepository.save(testDosen);
        
        Mahasiswa convertedMahasiswa = new Mahasiswa("Former Dosen", "M54321", "dosen@test.com", "pass123");
        when(userFactory.convertToMahasiswa(testDosen, "M54321", "Former Dosen")).thenReturn(convertedMahasiswa);
        
        boolean updated = userRepository.updateUserToMahasiswa("dosen@test.com", "M54321", "Former Dosen");
        boolean nonExistentUpdated = userRepository.updateUserToMahasiswa("nonexistent@test.com", "M99999", "Non Existent");
        
        assertTrue(updated);
        assertFalse(nonExistentUpdated);
        
        Optional<User> updatedUser = userRepository.findByEmail("dosen@test.com");
        assertTrue(updatedUser.isPresent());
        assertEquals(UserRole.MAHASISWA, updatedUser.get().getRole());
        assertTrue(updatedUser.get() instanceof Mahasiswa);
        Mahasiswa castedMahasiswa = (Mahasiswa) updatedUser.get();
        assertEquals("M54321", castedMahasiswa.getNim());
        assertEquals("Former Dosen", castedMahasiswa.getName());
    }
    
    @Test
    void testUpdateUserPassword() {
        userRepository.save(testDosen);
        
        boolean updated = userRepository.updateUserPassword("dosen@test.com", "newpassword");
        boolean nonExistentUpdated = userRepository.updateUserPassword("nonexistent@test.com", "newpassword");
        
        assertTrue(updated);
        assertFalse(nonExistentUpdated);
        
        Optional<User> updatedUser = userRepository.findByEmail("dosen@test.com");
        assertTrue(updatedUser.isPresent());
        assertEquals("newpassword", updatedUser.get().getPassword());
    }
}