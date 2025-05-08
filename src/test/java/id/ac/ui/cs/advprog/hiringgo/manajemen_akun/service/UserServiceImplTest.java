package id.ac.ui.cs.advprog.hiringgo.manajemen_akun.service;

import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.factory.UserFactory;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.User;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.model.UserRole;
import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserFactory userFactory;
    
    private UserServiceImpl userService;
    
    private Admin testAdmin;
    private Dosen testDosen;
    private Mahasiswa testMahasiswa;
    
    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userFactory);
        
        testAdmin = new Admin("admin@test.com", "adminpass");
        testDosen = new Dosen("D12345", "Dr. Test", "dosen@test.com", "dosenpass");
        testMahasiswa = new Mahasiswa("Student Test", "M12345", "student@test.com", "studentpass");
    }
    
    @Test
    void testCreateAdmin() {
        when(userFactory.createAdmin("admin@test.com", "adminpass")).thenReturn(testAdmin);
        when(userRepository.save(testAdmin)).thenReturn(testAdmin);
        
        User createdAdmin = userService.createAdmin("admin@test.com", "adminpass");
        
        assertEquals(testAdmin, createdAdmin);
        verify(userFactory).createAdmin("admin@test.com", "adminpass");
        verify(userRepository).save(testAdmin);
    }
    
    @Test
    void testCreateDosen() {
        when(userFactory.createDosen("D12345", "Dr. Test", "dosen@test.com", "dosenpass")).thenReturn(testDosen);
        when(userRepository.save(testDosen)).thenReturn(testDosen);
        
        User createdDosen = userService.createDosen("D12345", "Dr. Test", "dosen@test.com", "dosenpass");
        
        assertEquals(testDosen, createdDosen);
        verify(userFactory).createDosen("D12345", "Dr. Test", "dosen@test.com", "dosenpass");
        verify(userRepository).save(testDosen);
    }
    
    @Test
    void testGetAllUsers() {
        List<User> expectedUsers = Arrays.asList(testAdmin, testDosen, testMahasiswa);
        when(userRepository.findAll()).thenReturn(expectedUsers);
        
        List<User> actualUsers = userService.getAllUsers();
        
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository).findAll();
    }
    
    @Test
    void testGetUserByEmail() {
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(testAdmin));
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());
        
        Optional<User> foundUser = userService.getUserByEmail("admin@test.com");
        Optional<User> nonExistentUser = userService.getUserByEmail("nonexistent@test.com");
        
        assertTrue(foundUser.isPresent());
        assertEquals(testAdmin, foundUser.get());
        assertTrue(nonExistentUser.isEmpty());
        
        verify(userRepository).findByEmail("admin@test.com");
        verify(userRepository).findByEmail("nonexistent@test.com");
    }
    
    @Test
    void testGetUsersByRole() {
        List<User> expectedAdmins = Arrays.asList(testAdmin);
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(expectedAdmins);
        
        List<User> actualAdmins = userService.getUsersByRole(UserRole.ADMIN);
        
        assertEquals(expectedAdmins, actualAdmins);
        verify(userRepository).findByRole(UserRole.ADMIN);
    }
    
    @Test
    void testChangeUserToAdmin() {
        when(userRepository.updateUserToAdmin("dosen@test.com")).thenReturn(true);
        when(userRepository.updateUserToAdmin("nonexistent@test.com")).thenReturn(false);
        
        boolean updated = userService.changeUserToAdmin("dosen@test.com");
        boolean nonExistentUpdated = userService.changeUserToAdmin("nonexistent@test.com");
        
        assertTrue(updated);
        assertFalse(nonExistentUpdated);
        
        verify(userRepository).updateUserToAdmin("dosen@test.com");
        verify(userRepository).updateUserToAdmin("nonexistent@test.com");
    }
    
    @Test
    void testChangeUserToDosen() {
        String nip = "D54321";
        String name = "Dr. Former Student";
        when(userRepository.updateUserToDosen("student@test.com", nip, name)).thenReturn(true);
        when(userRepository.updateUserToDosen("nonexistent@test.com", nip, name)).thenReturn(false);
        
        boolean updated = userService.changeUserToDosen("student@test.com", nip, name);
        boolean nonExistentUpdated = userService.changeUserToDosen("nonexistent@test.com", nip, name);
        
        assertTrue(updated);
        assertFalse(nonExistentUpdated);
        
        verify(userRepository).updateUserToDosen("student@test.com", nip, name);
        verify(userRepository).updateUserToDosen("nonexistent@test.com", nip, name);
    }
    
    @Test
    void testChangeUserToMahasiswa() {
        String nim = "M54321";
        String name = "Former Dosen";
        when(userRepository.updateUserToMahasiswa("dosen@test.com", nim, name)).thenReturn(true);
        when(userRepository.updateUserToMahasiswa("nonexistent@test.com", nim, name)).thenReturn(false);
        
        boolean updated = userService.changeUserToMahasiswa("dosen@test.com", nim, name);
        boolean nonExistentUpdated = userService.changeUserToMahasiswa("nonexistent@test.com", nim, name);
        
        assertTrue(updated);
        assertFalse(nonExistentUpdated);
        
        verify(userRepository).updateUserToMahasiswa("dosen@test.com", nim, name);
        verify(userRepository).updateUserToMahasiswa("nonexistent@test.com", nim, name);
    }
    
    @Test
    void testUpdateUserPassword() {
        when(userRepository.updateUserPassword("admin@test.com", "newpassword")).thenReturn(true);
        when(userRepository.updateUserPassword("nonexistent@test.com", "newpassword")).thenReturn(false);
        
        boolean updated = userService.updateUserPassword("admin@test.com", "newpassword");
        boolean nonExistentUpdated = userService.updateUserPassword("nonexistent@test.com", "newpassword");
        
        assertTrue(updated);
        assertFalse(nonExistentUpdated);
        
        verify(userRepository).updateUserPassword("admin@test.com", "newpassword");
        verify(userRepository).updateUserPassword("nonexistent@test.com", "newpassword");
    }
    
    @Test
    void testDeleteUser() {
        when(userRepository.deleteByEmail("admin@test.com")).thenReturn(true);
        when(userRepository.deleteByEmail("nonexistent@test.com")).thenReturn(false);
        
        boolean deleted = userService.deleteUser("admin@test.com");
        boolean nonExistentDeleted = userService.deleteUser("nonexistent@test.com");
        
        assertTrue(deleted);
        assertFalse(nonExistentDeleted);
        
        verify(userRepository).deleteByEmail("admin@test.com");
        verify(userRepository).deleteByEmail("nonexistent@test.com");
    }
}