package id.ac.ui.cs.advprog.hiringgo.dashboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;

@ControllerAdvice(basePackages = "id.ac.ui.cs.advprog.hiringgo.dashboard")
public class DashboardExceptionHandler {

    // Define constants for repeated literals
    private static final String STATUS_KEY = "status";
    private static final String MESSAGE_KEY = "message";
    private static final String MODULE_KEY = "module";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String MODULE_VALUE = "dashboard";

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(NoSuchElementException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE_KEY, "Dashboard data tidak dapat dimuat: " + ex.getMessage());
        response.put(MODULE_KEY, MODULE_VALUE);
        response.put(STATUS_KEY, "NOT_FOUND");
        response.put(TIMESTAMP_KEY, System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<Map<String, Object>> handleAsyncError(CompletionException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE_KEY, "Terjadi kesalahan saat memproses data dashboard secara asynchronous");
        response.put(MODULE_KEY, MODULE_VALUE);
        response.put(STATUS_KEY, "ASYNC_ERROR");
        response.put(TIMESTAMP_KEY, System.currentTimeMillis());
        response.put("details", ex.getCause() != null ? ex.getCause().getMessage() : "Unknown async error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDashboardRequest(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE_KEY, "Parameter dashboard tidak valid: " + ex.getMessage());
        response.put(MODULE_KEY, MODULE_VALUE);
        response.put(STATUS_KEY, "INVALID_REQUEST");
        response.put(TIMESTAMP_KEY, System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleDashboardAccessDenied(SecurityException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE_KEY, "Akses dashboard ditolak: " + ex.getMessage());
        response.put(MODULE_KEY, MODULE_VALUE);
        response.put(STATUS_KEY, "ACCESS_DENIED");
        response.put(TIMESTAMP_KEY, System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralDashboardError(Exception ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE_KEY, "Terjadi kesalahan saat memuat dashboard");
        response.put(MODULE_KEY, MODULE_VALUE);
        response.put(STATUS_KEY, "INTERNAL_ERROR");
        response.put(TIMESTAMP_KEY, System.currentTimeMillis());
        response.put("error_type", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDenied(AuthorizationDeniedException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE_KEY, "Anda tidak memiliki akses ke dashboard ini");
        response.put(MODULE_KEY, MODULE_VALUE);
        response.put(STATUS_KEY, "FORBIDDEN");
        response.put(TIMESTAMP_KEY, System.currentTimeMillis());
        response.put("required_role", extractRequiredRole(request));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    private String extractRequiredRole(WebRequest request) {
        String requestURI = request.getDescription(false);
        if (requestURI.contains("/mahasiswa")) {
            return "MAHASISWA";
        } else if (requestURI.contains("/dosen")) {
            return "DOSEN";
        } else if (requestURI.contains("/admin")) {
            return "ADMIN";
        }
        return "UNKNOWN";
    }
}