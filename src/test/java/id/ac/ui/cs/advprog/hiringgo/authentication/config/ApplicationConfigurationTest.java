package id.ac.ui.cs.advprog.hiringgo.authentication.config;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApplicationConfigurationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    private ApplicationConfiguration applicationConfiguration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        applicationConfiguration = new ApplicationConfiguration(userRepository);
    }

    @Test
    void userDetailsService_ShouldReturnUserDetailsService() {
        User mockUser = mock(User.class);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        UserDetailsService userDetailsService = applicationConfiguration.userDetailsService();
        assertNotNull(userDetailsService);

        assertSame(mockUser, userDetailsService.loadUserByUsername("test@example.com"));
    }

    @Test
    void userDetailsService_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = applicationConfiguration.userDetailsService();
        assertNotNull(userDetailsService);

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        BCryptPasswordEncoder passwordEncoder = applicationConfiguration.passwordEncoder();
        
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
        
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void authenticationManager_ShouldReturnAuthenticationManagerFromConfig() throws Exception {
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);

        AuthenticationManager authManager = applicationConfiguration.authenticationManager(authenticationConfiguration);
        
        assertNotNull(authManager);
        assertSame(mockAuthManager, authManager);
    }

    @Test
    void authenticationProvider_ShouldReturnConfiguredDaoAuthenticationProvider() {
        AuthenticationProvider authProvider = applicationConfiguration.authenticationProvider();
        
        assertNotNull(authProvider);
        assertTrue(authProvider instanceof DaoAuthenticationProvider);
        
    }
}