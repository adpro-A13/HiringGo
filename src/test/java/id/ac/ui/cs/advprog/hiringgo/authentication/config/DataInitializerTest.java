package id.ac.ui.cs.advprog.hiringgo.authentication.config;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataInitializer = new DataInitializer(userRepository, passwordEncoder);
        
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
    }

    @Test
    void initializeData_ShouldCreateAdminUser_WhenAdminDoesNotExist() throws Exception {
        when(userRepository.findByEmail("admin@hiringgo.com")).thenReturn(Optional.empty());
        
        CommandLineRunner runner = dataInitializer.initializeData();
        assertNotNull(runner);
        
        runner.run();
        
        verify(userRepository).findByEmail("admin@hiringgo.com");
        verify(passwordEncoder).encode("admin123");
        verify(userRepository).save(any(Admin.class));
    }

    @Test
    void initializeData_ShouldNotCreateAdminUser_WhenAdminAlreadyExists() throws Exception {
        Admin existingAdmin = new Admin();
        when(userRepository.findByEmail("admin@hiringgo.com")).thenReturn(Optional.of(existingAdmin));
        
        CommandLineRunner runner = dataInitializer.initializeData();
        assertNotNull(runner);
        
        runner.run();
        
        verify(userRepository).findByEmail("admin@hiringgo.com");
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }
}