package id.ac.ui.cs.advprog.hiringgo.middleware;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MiddlewareSecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthFilter;

    @InjectMocks
    private MiddlewareSecurityConfig middlewareSecurityConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMiddlewareCorsConfigurationSource() {
        CorsConfigurationSource corsConfigurationSource = middlewareSecurityConfig.middlewareCorsConfigurationSource();
        
        assertNotNull(corsConfigurationSource, "CORS configuration source should not be null");
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        
        CorsConfiguration config = corsConfigurationSource.getCorsConfiguration(request);
        
        assertNotNull(config, "CORS configuration should not be null");
        
        assertEquals(1, config.getAllowedOrigins().size(), "Should have exactly one allowed origin");
        assertEquals("*", config.getAllowedOrigins().get(0), "Should allow all origins");
        
        List<String> expectedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertTrue(config.getAllowedMethods().containsAll(expectedMethods), 
                "Should allow all specified HTTP methods");
        
        List<String> expectedHeaders = Arrays.asList("authorization", "content-type", "x-auth-token");
        assertTrue(config.getAllowedHeaders().containsAll(expectedHeaders), 
                "Should allow all specified headers");
        
        assertEquals(1, config.getExposedHeaders().size(), "Should have exactly one exposed header");
        assertEquals("x-auth-token", config.getExposedHeaders().get(0), "Should expose the x-auth-token header");
    }

    @Test
    void testMiddlewareSecurityFilterChain() throws Exception {
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        
        when(httpSecurity.securityMatcher(anyString())).thenReturn(httpSecurity);
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        
        DefaultSecurityFilterChain mockFilterChain = mock(DefaultSecurityFilterChain.class);
        when(httpSecurity.build()).thenReturn(mockFilterChain);
        
        SecurityFilterChain result = middlewareSecurityConfig.middlewareSecurityFilterChain(httpSecurity);
        
        assertNotNull(result, "Security filter chain should not be null");
        assertEquals(mockFilterChain, result, "Should return the built filter chain");
        
        verify(httpSecurity).securityMatcher("/api/**");
        
        verify(httpSecurity).csrf(any());
        
        verify(httpSecurity).cors(any());
        
        verify(httpSecurity).authorizeHttpRequests(any());
        
        verify(httpSecurity).sessionManagement(any());
        
        verify(httpSecurity).addFilterBefore(eq(jwtAuthFilter), any());
        
        verify(httpSecurity).build();
    }
}