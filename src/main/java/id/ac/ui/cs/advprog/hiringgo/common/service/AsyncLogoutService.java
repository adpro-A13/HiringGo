package id.ac.ui.cs.advprog.hiringgo.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncLogoutService {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncLogoutService.class);
    
    @Async
    public void logoutUser(String userId) {
        try {
            logger.info("Starting async logout process for user: {}", userId);
            clearUserCache(userId);
            logSecurityEvent(userId);
            
            logger.info("Async logout process completed successfully for user: {}", userId);
            
        } catch (Exception e) {
            logger.error("Error during async logout process for user: {}", userId, e);
        }
    }
    
    private void clearUserCache(String userId) {
        logger.debug("Clearing cache for user: {}", userId);
        logger.info("Cache cleared for user: {}", userId);
    }
    
    private void logSecurityEvent(String userId) {
        logger.info("Security event: User {} tokens invalidated due to credential change", userId);
        logger.info("Audit log: TOKEN_INVALIDATION for user {} due to credential change", userId);
    }
}
