package id.ac.ui.cs.advprog.hiringgo.middleware;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.AuthenticationService;
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
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private User mockUser;
    private final String validToken = "valid.jwt.token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        mockUser = new Mahasiswa();
        mockUser.setUsername("test@example.com");
        mockUser.setPassword("encodedPassword");
        ((Mahasiswa) mockUser).setFullName("Test User");
        ((Mahasiswa) mockUser).setNim("12345678");
    }

    @Test
    void shouldSkipFilterForAuthEndpoints() throws ServletException, IOException {
        request.setServletPath("/auth/login");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(authenticationService, never()).verifyToken(anyString());
    }

    @Test
    void shouldRejectRequestWithoutAuthorizationHeader() throws ServletException, IOException {
        request.setServletPath("/api/someEndpoint");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains("Authorization header with Bearer token is required"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldRejectRequestWithInvalidToken() throws ServletException, IOException {
        request.setServletPath("/api/someEndpoint");
        request.addHeader("Authorization", "Bearer " + validToken);
        when(authenticationService.verifyToken(validToken)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid or expired token"));
        verify(authenticationService, times(1)).verifyToken(validToken);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldAuthenticateRequestWithValidToken() throws ServletException, IOException {
        request.setServletPath("/api/someEndpoint");
        request.addHeader("Authorization", "Bearer " + validToken);
        when(authenticationService.verifyToken(validToken)).thenReturn(mockUser);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(authenticationService, times(1)).verifyToken(validToken);
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(mockUser, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void shouldRejectRequestWithInvalidAuthorizationHeaderFormat() throws ServletException, IOException {
        request.setServletPath("/api/someEndpoint");
        request.addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains("Authorization header with Bearer token is required"));
        assertTrue(response.getContentAsString().contains("missing_token"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldIncludeTimestampInErrorResponse() throws ServletException, IOException {
        request.setServletPath("/api/someEndpoint");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains("timestamp"));
        assertTrue(response.getContentAsString().contains("error_code"));
        assertTrue(response.getContentAsString().contains("status"));
    }
}