package id.ac.ui.cs.advprog.hiringgo.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class AsyncLogoutServiceTest {

    @InjectMocks
    private AsyncLogoutService asyncLogoutService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void logoutUser_shouldCompleteWithoutException() {
        String userId = "test-user-123";
        
        assertDoesNotThrow(() -> {
            asyncLogoutService.logoutUser(userId);
        });
    }

    @Test
    void logoutUser_withNullUserId_shouldCompleteWithoutException() {
        assertDoesNotThrow(() -> {
            asyncLogoutService.logoutUser(null);
        });
    }

    @Test
    void logoutUser_withEmptyUserId_shouldCompleteWithoutException() {
        assertDoesNotThrow(() -> {
            asyncLogoutService.logoutUser("");
        });
    }
}
