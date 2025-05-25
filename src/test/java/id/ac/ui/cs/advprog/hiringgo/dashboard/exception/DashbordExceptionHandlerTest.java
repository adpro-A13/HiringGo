package id.ac.ui.cs.advprog.hiringgo.dashboard.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
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
}