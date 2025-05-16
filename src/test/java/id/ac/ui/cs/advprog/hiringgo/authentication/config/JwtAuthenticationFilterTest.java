package id.ac.ui.cs.advprog.hiringgo.authentication.config;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private User mockUser;
    private final String validToken = "valid.jwt.token";
    private final String userEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        SecurityContextHolder.setContext(securityContext);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        mockUser = new Mahasiswa();
        mockUser.setUsername(userEmail);
        mockUser.setPassword("encodedPassword");
        ((Mahasiswa) mockUser).setFullName("Test User");
        ((Mahasiswa) mockUser).setNim("12345678");
    }

    @Test
    void shouldContinueFilterChainWhenNoAuthHeader() throws ServletException, IOException {

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldContinueFilterChainWhenAuthHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldSetAuthenticationWhenTokenIsValid() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtService.extractUsername(validToken)).thenReturn(userEmail);
        
        when(securityContext.getAuthentication()).thenReturn(null);
        
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(mockUser);
        
        when(jwtService.isTokenValid(validToken, mockUser)).thenReturn(true);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSendErrorResponseWhenTokenIsInvalid() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtService.extractUsername(validToken)).thenReturn(userEmail);
        
        when(securityContext.getAuthentication()).thenReturn(null);
        
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(mockUser);
        
        when(jwtService.isTokenValid(validToken, mockUser)).thenReturn(false);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains("Token is no longer valid"));
        assertTrue(response.getContentAsString().contains("invalid_token"));
        
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldSkipTokenValidationWhenAuthenticationAlreadyExists() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtService.extractUsername(validToken)).thenReturn(userEmail);
        
        Authentication existingAuth = new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        when(securityContext.getAuthentication()).thenReturn(existingAuth);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).isTokenValid(anyString(), any(UserDetails.class));
    }

    @Test
    void shouldHandleExceptionDuringTokenProcessing() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtService.extractUsername(validToken)).thenThrow(new RuntimeException("Token parsing error"));
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid or expired token"));
        assertTrue(response.getContentAsString().contains("Token parsing error"));
        
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldSkipAuthenticationWhenExtractedUsernameIsNull() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtService.extractUsername(validToken)).thenReturn(null);
        
        when(securityContext.getAuthentication()).thenReturn(null);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        
        verify(filterChain).doFilter(request, response);
    }
}