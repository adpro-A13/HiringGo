package id.ac.ui.cs.advprog.hiringgo.dashboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;

@ControllerAdvice(basePackages = "id.ac.ui.cs.advprog.hiringgo.dashboard")
public class DashboardExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(NoSuchElementException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Dashboard data tidak dapat dimuat: " + ex.getMessage());
        response.put("module", "dashboard");
        response.put("status", "NOT_FOUND");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<Map<String, Object>> handleAsyncError(CompletionException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Terjadi kesalahan saat memproses data dashboard secara asynchronous");
        response.put("module", "dashboard");
        response.put("status", "ASYNC_ERROR");
        response.put("timestamp", System.currentTimeMillis());
        response.put("details", ex.getCause() != null ? ex.getCause().getMessage() : "Unknown async error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDashboardRequest(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Parameter dashboard tidak valid: " + ex.getMessage());
        response.put("module", "dashboard");
        response.put("status", "INVALID_REQUEST");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleDashboardAccessDenied(SecurityException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Akses dashboard ditolak: " + ex.getMessage());
        response.put("module", "dashboard");
        response.put("status", "ACCESS_DENIED");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralDashboardError(Exception ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Terjadi kesalahan saat memuat dashboard");
        response.put("module", "dashboard");
        response.put("status", "INTERNAL_ERROR");
        response.put("timestamp", System.currentTimeMillis());
        response.put("error_type", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}