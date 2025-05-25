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
        
        when(httpSecurity.securityMatcher(anyString())).thenReturn(httpSecurity);
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
    }

    @Test
    void securityFilterChain_ShouldConfigureSecurityCorrectly() throws Exception {
        DefaultSecurityFilterChain mockFilterChain = mock(DefaultSecurityFilterChain.class);

        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.securityMatcher(any(String[].class))).thenReturn(httpSecurity);
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(mockFilterChain);

        SecurityFilterChain result = securityConfiguration.securityFilterChain(httpSecurity);

        assertNotNull(result);
        assertEquals(mockFilterChain, result);

        verify(httpSecurity).cors(any());
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
        CorsConfigurationSource corsConfigSource = securityConfiguration.corsConfigurationSource();

        assertNotNull(corsConfigSource);

        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        request.setRequestURI("/auth/login");
        
        CorsConfiguration corsConfig = corsConfigSource.getCorsConfiguration(request);
        
        assertNotNull(corsConfig);

        assertEquals(2, corsConfig.getAllowedOrigins().size());
        assertEquals("http://localhost:8005", corsConfig.getAllowedOrigins().get(0));
        
        List<String> expectedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertEquals(expectedMethods, corsConfig.getAllowedMethods());
        
        List<String> expectedHeaders = List.of("Authorization", "Content-Type");
        assertEquals(expectedHeaders, corsConfig.getAllowedHeaders());
    }
}