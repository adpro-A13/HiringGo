package id.ac.ui.cs.advprog.hiringgo.dashboard.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

class DashboardExceptionHandlerTest {

    private DashboardExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new DashboardExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/mahasiswa");
        webRequest = new ServletWebRequest(request);
    }

    @Test
    void handleUserNotFound_shouldReturnNotFoundResponse() {
        NoSuchElementException ex = new NoSuchElementException("User not found");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleUserNotFound(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("dashboard", body.get("module"));
        assertEquals("NOT_FOUND", body.get("status"));
        assertTrue(body.get("message").toString().contains("User not found"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleAsyncError_shouldReturnInternalServerErrorResponse() {
        RuntimeException cause = new RuntimeException("Async operation failed");
        CompletionException ex = new CompletionException(cause);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAsyncError(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("dashboard", body.get("module"));
        assertEquals("ASYNC_ERROR", body.get("status"));
        assertEquals("Async operation failed", body.get("details"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleInvalidDashboardRequest_shouldReturnBadRequestResponse() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid parameter");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleInvalidDashboardRequest(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("dashboard", body.get("module"));
        assertEquals("INVALID_REQUEST", body.get("status"));
        assertTrue(body.get("message").toString().contains("Invalid parameter"));
    }

    @Test
    void handleDashboardAccessDenied_shouldReturnForbiddenResponse() {
        SecurityException ex = new SecurityException("Access denied");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDashboardAccessDenied(ex, webRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("dashboard", body.get("module"));
        assertEquals("ACCESS_DENIED", body.get("status"));
        assertTrue(body.get("message").toString().contains("Access denied"));
    }

    @Test
    void handleGeneralDashboardError_shouldReturnInternalServerErrorResponse() {
        Exception ex = new RuntimeException("General error");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGeneralDashboardError(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("dashboard", body.get("module"));
        assertEquals("INTERNAL_ERROR", body.get("status"));
        assertEquals("RuntimeException", body.get("error_type"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleAsyncError_withNullCause_shouldHandleGracefully() {
        CompletionException ex = new CompletionException(null);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAsyncError(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Unknown async error", body.get("details"));
    }

    // New tests for AuthorizationDeniedException handling
    @Test
    void handleAuthorizationDenied_withMahasiswaPath_shouldReturnForbiddenWithMahasiswaRole() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/mahasiswa");
        WebRequest mahasiswaRequest = new ServletWebRequest(request);
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex, mahasiswaRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("dashboard", body.get("module"));
        assertEquals("FORBIDDEN", body.get("status"));
        assertEquals("Anda tidak memiliki akses ke dashboard ini", body.get("message"));
        assertEquals("MAHASISWA", body.get("required_role"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleAuthorizationDenied_withDosenPath_shouldReturnForbiddenWithDosenRole() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/dosen");
        WebRequest dosenRequest = new ServletWebRequest(request);
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex, dosenRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("dashboard", body.get("module"));
        assertEquals("FORBIDDEN", body.get("status"));
        assertEquals("Anda tidak memiliki akses ke dashboard ini", body.get("message"));
        assertEquals("DOSEN", body.get("required_role"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleAuthorizationDenied_withAdminPath_shouldReturnForbiddenWithAdminRole() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/admin");
        WebRequest adminRequest = new ServletWebRequest(request);
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex, adminRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("dashboard", body.get("module"));
        assertEquals("FORBIDDEN", body.get("status"));
        assertEquals("Anda tidak memiliki akses ke dashboard ini", body.get("message"));
        assertEquals("ADMIN", body.get("required_role"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleAuthorizationDenied_withUnknownPath_shouldReturnForbiddenWithUnknownRole() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/unknown");
        WebRequest unknownRequest = new ServletWebRequest(request);
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex, unknownRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("dashboard", body.get("module"));
        assertEquals("FORBIDDEN", body.get("status"));
        assertEquals("Anda tidak memiliki akses ke dashboard ini", body.get("message"));
        assertEquals("UNKNOWN", body.get("required_role"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleAuthorizationDenied_withEmptyPath_shouldReturnForbiddenWithUnknownRole() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/");
        WebRequest emptyRequest = new ServletWebRequest(request);
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex, emptyRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("UNKNOWN", body.get("required_role"));
    }

    @Test
    void handleAuthorizationDenied_withComplexPath_shouldExtractCorrectRole() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/mahasiswa/profile");
        WebRequest complexRequest = new ServletWebRequest(request);
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex, complexRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("MAHASISWA", body.get("required_role"));
    }

    @Test
    void handleAuthorizationDenied_withMultipleRolesInPath_shouldReturnFirstMatch() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/mahasiswa/dosen");
        WebRequest multiRoleRequest = new ServletWebRequest(request);
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex, multiRoleRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        // Should return MAHASISWA as it appears first in the path
        assertEquals("MAHASISWA", body.get("required_role"));
    }

    @Test
    void handleAuthorizationDenied_withCaseVariations_shouldHandleCorrectly() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/dosen"); // Use lowercase (standard)
        WebRequest caseRequest = new ServletWebRequest(request);
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex, caseRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("DOSEN", body.get("required_role"));
    }

    @Test
    void handleAuthorizationDenied_withNullMessage_shouldHandleGracefully() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/admin");
        WebRequest adminRequest = new ServletWebRequest(request);
        AuthorizationDeniedException ex = new AuthorizationDeniedException(null);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex, adminRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("dashboard", body.get("module"));
        assertEquals("FORBIDDEN", body.get("status"));
        assertEquals("Anda tidak memiliki akses ke dashboard ini", body.get("message"));
        assertEquals("ADMIN", body.get("required_role"));
    }

    // Test to ensure all required keys are present in response
    @Test
    void handleAuthorizationDenied_shouldContainAllRequiredKeys() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/mahasiswa");
        WebRequest testRequest = new ServletWebRequest(request);
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Test");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex, testRequest);

        Map<String, Object> body = response.getBody();
        assertNotNull(body);

        // Verify all required keys are present
        assertTrue(body.containsKey("message"));
        assertTrue(body.containsKey("module"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("required_role"));

        // Verify values are not null
        assertNotNull(body.get("message"));
        assertNotNull(body.get("module"));
        assertNotNull(body.get("status"));
        assertNotNull(body.get("timestamp"));
        assertNotNull(body.get("required_role"));
    }

    // Test timestamp consistency
    @Test
    void handleAuthorizationDenied_timestampShouldBeReasonable() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/dashboard/dosen");
        WebRequest testRequest = new ServletWebRequest(request);
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Test");

        long beforeCall = System.currentTimeMillis();
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthorizationDenied(ex, testRequest);
        long afterCall = System.currentTimeMillis();

        Map<String, Object> body = response.getBody();
        assertNotNull(body);

        Long timestamp = (Long) body.get("timestamp");
        assertNotNull(timestamp);
        assertTrue(timestamp >= beforeCall, "Timestamp should be after or equal to before call time");
        assertTrue(timestamp <= afterCall, "Timestamp should be before or equal to after call time");
    }
}