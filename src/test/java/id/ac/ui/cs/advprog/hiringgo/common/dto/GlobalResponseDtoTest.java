package id.ac.ui.cs.advprog.hiringgo.common.dto;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GlobalResponseDtoTest {

    @Test
    void testSuccessWithData() {
        String testData = "Test Data";
        String message = "Success message";
        
        GlobalResponseDto<String> response = GlobalResponseDto.success(testData, message);
        
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(message, response.getMessage());
        assertEquals(testData, response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testSuccessWithDataAndCustomStatus() {
        String testData = "Created Data";
        String message = "Resource created successfully";
        HttpStatus status = HttpStatus.CREATED;
        
        GlobalResponseDto<String> response = GlobalResponseDto.success(testData, message, status);
        
        assertTrue(response.isSuccess());
        assertEquals(status.value(), response.getStatusCode());
        assertEquals(message, response.getMessage());
        assertEquals(testData, response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testSuccessWithoutData() {
        String message = "Operation successful";
        
        GlobalResponseDto<Object> response = GlobalResponseDto.success(message);
        
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testError() {
        String errorMessage = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        
        GlobalResponseDto<Object> response = GlobalResponseDto.error(errorMessage, status);
        
        assertFalse(response.isSuccess());
        assertEquals(status.value(), response.getStatusCode());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testBadRequest() {
        String errorMessage = "Invalid input";
        
        GlobalResponseDto<Object> response = GlobalResponseDto.badRequest(errorMessage);
        
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testNotFound() {
        String errorMessage = "User not found";
        
        GlobalResponseDto<Object> response = GlobalResponseDto.notFound(errorMessage);
        
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testServerError() {
        String errorMessage = "Database connection failed";
        
        GlobalResponseDto<Object> response = GlobalResponseDto.serverError(errorMessage);
        
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testBuilderAndGettersSetters() {
        LocalDateTime now = LocalDateTime.now();
        String testData = "Test";
        
        GlobalResponseDto<String> response = GlobalResponseDto.<String>builder()
                .success(true)
                .statusCode(200)
                .message("Message")
                .data(testData)
                .timestamp(now)
                .build();
        
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatusCode());
        assertEquals("Message", response.getMessage());
        assertEquals(testData, response.getData());
        assertEquals(now, response.getTimestamp());
        
        LocalDateTime newTime = now.plusHours(1);
        response.setSuccess(false);
        response.setStatusCode(400);
        response.setMessage("New message");
        response.setData("New data");
        response.setTimestamp(newTime);
        
        assertFalse(response.isSuccess());
        assertEquals(400, response.getStatusCode());
        assertEquals("New message", response.getMessage());
        assertEquals("New data", response.getData());
        assertEquals(newTime, response.getTimestamp());
    }
}
