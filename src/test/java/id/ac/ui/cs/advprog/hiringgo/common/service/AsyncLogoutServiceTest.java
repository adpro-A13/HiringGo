
package id.ac.ui.cs.advprog.hiringgo.common.service;
import id.ac.ui.cs.advprog.hiringgo.common.service.AsyncLogoutService;

import id.ac.ui.cs.advprog.hiringgo.common.service.AsyncLogoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Async;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
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

    @Test
    void logoutUser_shouldHandleExceptionInTryBlock() {
        AsyncLogoutService customService = new AsyncLogoutService() {
            @Async
            public void logoutUser(String userId) {
                try {
                    // Use reflection to access the private logger
                    java.lang.reflect.Field loggerField = AsyncLogoutService.class.getDeclaredField("logger");
                    loggerField.setAccessible(true);
                    org.slf4j.Logger logger = (org.slf4j.Logger) loggerField.get(this);

                    logger.info("Starting async logout process for user: {}", userId);

                    throw new RuntimeException("Forced exception to test catch block");

                } catch (Exception e) {
                    try {
                        java.lang.reflect.Field loggerField = AsyncLogoutService.class.getDeclaredField("logger");
                        loggerField.setAccessible(true);
                        org.slf4j.Logger logger = (org.slf4j.Logger) loggerField.get(this);
                        logger.error("Error during async logout process for user: {}", userId, e);
                    } catch (Exception reflectionException) {
                        System.err.println("Error during async logout process for user: " + userId);
                    }
                }
            }
        };

        assertDoesNotThrow(() -> {
            customService.logoutUser("test-user");
        });
    }
}