package id.ac.ui.cs.advprog.hiringgo.authentication.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SecurityConfigurationTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private HttpSecurity httpSecurity;

    private SecurityConfiguration securityConfiguration;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        securityConfiguration = new SecurityConfiguration(jwtAuthenticationFilter, authenticationProvider);
        
        // Set up the mock HttpSecurity
        when(httpSecurity.securityMatcher(anyString())).thenReturn(httpSecurity);
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
    }

    @Test
    void securityFilterChain_ShouldConfigureSecurityCorrectly() throws Exception {
        // Set up the mock to return a security filter chain
        DefaultSecurityFilterChain mockFilterChain = mock(DefaultSecurityFilterChain.class);
        when(httpSecurity.build()).thenReturn(mockFilterChain);

        // Execute the method under test
        SecurityFilterChain result = securityConfiguration.securityFilterChain(httpSecurity);

        // Verify the result is as expected
        assertNotNull(result);
        assertEquals(mockFilterChain, result);

        // Verify all the required security configurations were applied
        verify(httpSecurity).securityMatcher("/**");
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authenticationProvider(authenticationProvider);
        verify(httpSecurity).addFilterBefore(eq(jwtAuthenticationFilter), any());
        verify(httpSecurity).build();
    }

    @Test
    void corsConfigurationSource_ShouldConfigureCorsCorrectly() {
        // Execute the method under test
        CorsConfigurationSource corsConfigSource = securityConfiguration.corsConfigurationSource();

        // Verify the result is not null
        assertNotNull(corsConfigSource);

        // Test with a mock HTTP request
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        request.setRequestURI("/auth/login");
        
        // Get the configuration for this request
        CorsConfiguration corsConfig = corsConfigSource.getCorsConfiguration(request);
        
        // Verify the CORS configuration
        assertNotNull(corsConfig);
        
        // Check allowed origins
        assertEquals(1, corsConfig.getAllowedOrigins().size());
        assertEquals("http://localhost:8005", corsConfig.getAllowedOrigins().get(0));
        
        // Check allowed methods
        List<String> expectedMethods = List.of("GET", "POST");
        assertEquals(expectedMethods, corsConfig.getAllowedMethods());
        
        // Check allowed headers
        List<String> expectedHeaders = List.of("Authorization", "Content-Type");
        assertEquals(expectedHeaders, corsConfig.getAllowedHeaders());
    }
}