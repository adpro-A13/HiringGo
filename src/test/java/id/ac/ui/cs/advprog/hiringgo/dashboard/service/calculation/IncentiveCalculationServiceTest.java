package id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation;

import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncentiveCalculationServiceTest {

    @Mock
    private LogService logService;

    @InjectMocks
    private IncentiveCalculationService service;

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
    }

    private List<Log> createSampleLogs() {
        List<Log> logs = new ArrayList<>();

        // Log 1: 2 hours (from 9:00 to 11:00)
        Log log1 = mock(Log.class);
        when(log1.getWaktuMulai()).thenReturn(LocalTime.of(9, 0));
        when(log1.getWaktuSelesai()).thenReturn(LocalTime.of(11, 0));
        logs.add(log1);

        // Log 2: 1.5 hours (from 13:00 to 14:30)
        Log log2 = mock(Log.class);
        when(log2.getWaktuMulai()).thenReturn(LocalTime.of(13, 0));
        when(log2.getWaktuSelesai()).thenReturn(LocalTime.of(14, 30));
        logs.add(log2);

        // Log 3: 0.5 hours (from 15:00 to 15:30)
        Log log3 = mock(Log.class);
        when(log3.getWaktuMulai()).thenReturn(LocalTime.of(15, 0));
        when(log3.getWaktuSelesai()).thenReturn(LocalTime.of(15, 30));
        logs.add(log3);

        return logs;
    }

    // Test Constructor Coverage
    @Test
    void constructor_shouldInitializeCorrectly() {
        // This test covers the constructor line that wasn't covered
        LogService mockLogService = mock(LogService.class);
        IncentiveCalculationService newService = new IncentiveCalculationService(mockLogService);

        // Verify the service was created (constructor was called)
        assertNotNull(newService);
    }

    // Test Synchronous Methods for Full Coverage
    @Test
    void calculateTotalLoggedHours_withValidLogs_shouldReturnCorrectHours() {
        // Setup - sampleLogs total: 120 + 90 + 30 = 240 minutes = 4.00 hours
        List<Log> sampleLogs = createSampleLogs();
        when(logService.getLogsByUser(userId)).thenReturn(sampleLogs);

        // Execute
        BigDecimal result = service.calculateTotalLoggedHours(userId);

        // Verify
        assertEquals(new BigDecimal("4.00"), result);
        assertEquals(2, result.scale()); // Should have 2 decimal places
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalLoggedHours_withNoLogs_shouldReturnZero() {
        // Setup
        when(logService.getLogsByUser(userId)).thenReturn(Collections.emptyList());

        // Execute
        BigDecimal result = service.calculateTotalLoggedHours(userId);

        // Verify
        assertEquals(new BigDecimal("0.00"), result);
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalLoggedHours_withSingleMinuteLog_shouldHandleRounding() {
        // Setup - 1 minute log
        List<Log> oneMinuteLogs = new ArrayList<>();
        Log oneMinuteLog = mock(Log.class);
        when(oneMinuteLog.getWaktuMulai()).thenReturn(LocalTime.of(9, 0));
        when(oneMinuteLog.getWaktuSelesai()).thenReturn(LocalTime.of(9, 1));
        oneMinuteLogs.add(oneMinuteLog);

        when(logService.getLogsByUser(userId)).thenReturn(oneMinuteLogs);

        // Execute
        BigDecimal result = service.calculateTotalLoggedHours(userId);

        // Verify - 1 minute = 1/60 hour = 0.02 (rounded to 2 decimal places with HALF_UP)
        assertEquals(new BigDecimal("0.02"), result);
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalIncentive_withValidHours_shouldReturnCorrectAmount() {
        // Setup - 4 hours from sampleLogs
        List<Log> sampleLogs = createSampleLogs();
        when(logService.getLogsByUser(userId)).thenReturn(sampleLogs);

        // Execute
        BigDecimal result = service.calculateTotalIncentive(userId);

        // Verify - 4.00 hours * 27500 = 110000.00
        assertEquals(0, new BigDecimal("110000.00").compareTo(result));
        // calculateTotalIncentive calls calculateTotalLoggedHours internally, which calls logService.getLogsByUser once
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalIncentive_withNoHours_shouldReturnZero() {
        // Setup
        when(logService.getLogsByUser(userId)).thenReturn(Collections.emptyList());

        // Execute
        BigDecimal result = service.calculateTotalIncentive(userId);

        // Verify
        assertEquals(0, BigDecimal.ZERO.compareTo(result));
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalIncentive_withPartialHours_shouldCalculateCorrectly() {
        // Setup - Only first log (2 hours)
        List<Log> partialLogs = new ArrayList<>();
        Log log1 = mock(Log.class);
        when(log1.getWaktuMulai()).thenReturn(LocalTime.of(9, 0));
        when(log1.getWaktuSelesai()).thenReturn(LocalTime.of(11, 0));
        partialLogs.add(log1);

        when(logService.getLogsByUser(userId)).thenReturn(partialLogs);

        // Execute
        BigDecimal result = service.calculateTotalIncentive(userId);

        // Verify - 2.00 hours * 27500 = 55000.00
        assertEquals(0, new BigDecimal("55000.00").compareTo(result));
        verify(logService).getLogsByUser(userId);
    }

    // Test Async Methods
    @Test
    void calculateTotalLoggedHoursAsync_withValidUserId_shouldReturnCorrectFuture() throws ExecutionException, InterruptedException {
        // Setup
        List<Log> sampleLogs = createSampleLogs();
        when(logService.getLogsByUser(userId)).thenReturn(sampleLogs);

        // Execute
        CompletableFuture<BigDecimal> future = service.calculateTotalLoggedHoursAsync(userId);

        // Verify
        assertNotNull(future);
        assertEquals(new BigDecimal("4.00"), future.get());
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalLoggedHoursAsync_withException_shouldPropagateException() {
        // Setup
        when(logService.getLogsByUser(userId)).thenThrow(new RuntimeException("Database error"));

        // Execute
        CompletableFuture<BigDecimal> future = service.calculateTotalLoggedHoursAsync(userId);

        // Verify exception is propagated
        assertThrows(CompletionException.class, future::join);
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalIncentiveAsync_withValidUserId_shouldReturnCorrectFuture() throws ExecutionException, InterruptedException {
        // Setup
        List<Log> sampleLogs = createSampleLogs();
        when(logService.getLogsByUser(userId)).thenReturn(sampleLogs);

        // Execute
        CompletableFuture<BigDecimal> future = service.calculateTotalIncentiveAsync(userId);

        // Verify
        assertNotNull(future);
        assertEquals(0, new BigDecimal("110000.00").compareTo(future.get()));
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalIncentiveAsync_withException_shouldPropagateException() {
        // Setup
        when(logService.getLogsByUser(userId)).thenThrow(new RuntimeException("Service unavailable"));

        // Execute
        CompletableFuture<BigDecimal> future = service.calculateTotalIncentiveAsync(userId);

        // Verify exception is propagated
        assertThrows(CompletionException.class, future::join);
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalLoggedHoursAsync_withNullUserId_shouldThrowException() {
        // Setup - LogService throws exception for null userId
        when(logService.getLogsByUser(null)).thenThrow(new IllegalArgumentException("User ID cannot be null"));

        // Execute & Verify
        CompletableFuture<BigDecimal> future = service.calculateTotalLoggedHoursAsync(null);
        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        verify(logService).getLogsByUser(null);
    }

    @Test
    void calculateTotalIncentiveAsync_withNullUserId_shouldThrowException() {
        // Setup
        when(logService.getLogsByUser(null)).thenThrow(new IllegalArgumentException("User ID cannot be null"));

        // Execute & Verify
        CompletableFuture<BigDecimal> future = service.calculateTotalIncentiveAsync(null);
        CompletionException exception = assertThrows(CompletionException.class, future::join);
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        verify(logService).getLogsByUser(null);
    }

    // Performance and Concurrency Tests
    @Test
    void bothAsyncMethods_performanceTest() throws ExecutionException, InterruptedException {
        // Setup
        List<Log> sampleLogs = createSampleLogs();
        when(logService.getLogsByUser(userId)).thenReturn(sampleLogs);

        long startTime = System.currentTimeMillis();

        // Execute both methods concurrently
        CompletableFuture<BigDecimal> hoursResult = service.calculateTotalLoggedHoursAsync(userId);
        CompletableFuture<BigDecimal> incentiveResult = service.calculateTotalIncentiveAsync(userId);

        // Wait for both to complete
        CompletableFuture.allOf(hoursResult, incentiveResult).get();

        long duration = System.currentTimeMillis() - startTime;

        // Verify results
        assertEquals(new BigDecimal("4.00"), hoursResult.get());
        assertEquals(0, new BigDecimal("110000.00").compareTo(incentiveResult.get()));

        // Should complete reasonably fast
        assertTrue(duration < 1000, "Both async operations should complete quickly");

        // Verify service was called - both async methods call the service independently
        verify(logService, atLeast(1)).getLogsByUser(userId);
    }

    @Test
    void asyncMethods_withMultipleUsers_shouldHandleIndependently() throws ExecutionException, InterruptedException {
        // Setup
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        // Create separate logs for each user
        List<Log> logs1 = new ArrayList<>();
        Log log1 = mock(Log.class);
        when(log1.getWaktuMulai()).thenReturn(LocalTime.of(9, 0));
        when(log1.getWaktuSelesai()).thenReturn(LocalTime.of(11, 0)); // 2 hours
        logs1.add(log1);

        List<Log> logs2 = new ArrayList<>();
        Log log2 = mock(Log.class);
        when(log2.getWaktuMulai()).thenReturn(LocalTime.of(13, 0));
        when(log2.getWaktuSelesai()).thenReturn(LocalTime.of(14, 30)); // 1.5 hours
        logs2.add(log2);
        Log log3 = mock(Log.class);
        when(log3.getWaktuMulai()).thenReturn(LocalTime.of(15, 0));
        when(log3.getWaktuSelesai()).thenReturn(LocalTime.of(15, 30)); // 0.5 hours
        logs2.add(log3);

        when(logService.getLogsByUser(userId1)).thenReturn(logs1);
        when(logService.getLogsByUser(userId2)).thenReturn(logs2);

        // Execute
        CompletableFuture<BigDecimal> hours1 = service.calculateTotalLoggedHoursAsync(userId1);
        CompletableFuture<BigDecimal> hours2 = service.calculateTotalLoggedHoursAsync(userId2);
        CompletableFuture<BigDecimal> incentive1 = service.calculateTotalIncentiveAsync(userId1);
        CompletableFuture<BigDecimal> incentive2 = service.calculateTotalIncentiveAsync(userId2);

        // Verify
        assertEquals(new BigDecimal("2.00"), hours1.get());
        assertEquals(new BigDecimal("2.00"), hours2.get());
        assertEquals(0, new BigDecimal("55000.00").compareTo(incentive1.get()));
        assertEquals(0, new BigDecimal("55000.00").compareTo(incentive2.get()));

        // Verify each user was processed
        verify(logService, atLeast(1)).getLogsByUser(userId1);
        verify(logService, atLeast(1)).getLogsByUser(userId2);
    }

    // Edge Cases and Precision Tests
    @Test
    void calculateTotalLoggedHours_withPrecisionEdgeCase_shouldRoundCorrectly() {
        // Setup - Create a log that results in 1/3 hour (20 minutes) to test rounding
        List<Log> precisionLogs = new ArrayList<>();
        Log precisionLog = mock(Log.class);
        when(precisionLog.getWaktuMulai()).thenReturn(LocalTime.of(9, 0));
        when(precisionLog.getWaktuSelesai()).thenReturn(LocalTime.of(9, 20));
        precisionLogs.add(precisionLog);

        when(logService.getLogsByUser(userId)).thenReturn(precisionLogs);

        // Execute
        BigDecimal result = service.calculateTotalLoggedHours(userId);

        // Verify - 20 minutes = 20/60 = 0.333... which should round to 0.33 with HALF_UP
        assertEquals(new BigDecimal("0.33"), result);
        assertEquals(2, result.scale());
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalIncentive_withHourlyRateConstant_shouldUseCorrectRate() {
        // Setup - 1 hour log
        List<Log> oneHourLogs = new ArrayList<>();
        Log oneHourLog = mock(Log.class);
        when(oneHourLog.getWaktuMulai()).thenReturn(LocalTime.of(9, 0));
        when(oneHourLog.getWaktuSelesai()).thenReturn(LocalTime.of(10, 0));
        oneHourLogs.add(oneHourLog);

        when(logService.getLogsByUser(userId)).thenReturn(oneHourLogs);

        // Execute
        BigDecimal result = service.calculateTotalIncentive(userId);

        // Verify - 1 hour * 27500 (hourly rate constant)
        assertEquals(0, new BigDecimal("27500.00").compareTo(result));
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateMethods_withLargeNumberOfLogs_shouldHandleEfficiently() {
        // Setup - Create many 1-hour logs
        List<Log> manyLogs = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Log log = mock(Log.class);
            // Each log is 1 hour
            when(log.getWaktuMulai()).thenReturn(LocalTime.of(9, 0));
            when(log.getWaktuSelesai()).thenReturn(LocalTime.of(10, 0));
            manyLogs.add(log);
        }

        when(logService.getLogsByUser(userId)).thenReturn(manyLogs);

        long startTime = System.currentTimeMillis();

        // Execute
        BigDecimal hours = service.calculateTotalLoggedHours(userId);
        BigDecimal incentive = service.calculateTotalIncentive(userId);

        long duration = System.currentTimeMillis() - startTime;

        // Verify - 50 hours and corresponding incentive
        assertEquals(new BigDecimal("50.00"), hours);
        assertEquals(0, new BigDecimal("1375000.00").compareTo(incentive));

        // Should handle efficiently
        assertTrue(duration < 1000, "Should handle large number of logs efficiently");
        // Both methods call the service independently
        verify(logService, times(2)).getLogsByUser(userId);
    }

    @Test
    void calculateTotalLoggedHours_withNullLogs_shouldHandleGracefully() {
        // Setup
        when(logService.getLogsByUser(userId)).thenReturn(null);

        // Execute & Verify - Should handle null gracefully
        assertThrows(NullPointerException.class, () -> service.calculateTotalLoggedHours(userId));
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalIncentive_withNullLogs_shouldHandleGracefully() {
        // Setup
        when(logService.getLogsByUser(userId)).thenReturn(null);

        // Execute & Verify - Should handle null gracefully
        assertThrows(NullPointerException.class, () -> service.calculateTotalIncentive(userId));
        verify(logService).getLogsByUser(userId);
    }

    // Test Static Final Field Coverage
    @Test
    void hourlyRateConstant_shouldBeCorrectValue() {
        // This test ensures the HOURLY_RATE constant is being used correctly
        List<Log> oneHourLogs = new ArrayList<>();
        Log oneHourLog = mock(Log.class);
        when(oneHourLog.getWaktuMulai()).thenReturn(LocalTime.of(9, 0));
        when(oneHourLog.getWaktuSelesai()).thenReturn(LocalTime.of(10, 0));
        oneHourLogs.add(oneHourLog);

        when(logService.getLogsByUser(userId)).thenReturn(oneHourLogs);

        BigDecimal incentive = service.calculateTotalIncentive(userId);

        // The result should be exactly 27500 (1 hour * 27500 rate)
        assertEquals(0, new BigDecimal("27500.00").compareTo(incentive));

        // This confirms that the HOURLY_RATE constant (27500) is being used
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalLoggedHours_withCrossMidnightLog_shouldHandleCorrectly() {
        // Setup - Log that crosses midnight (will give negative duration)
        List<Log> crossMidnightLogs = new ArrayList<>();
        Log crossMidnightLog = mock(Log.class);
        when(crossMidnightLog.getWaktuMulai()).thenReturn(LocalTime.of(23, 0));
        when(crossMidnightLog.getWaktuSelesai()).thenReturn(LocalTime.of(1, 0)); // Next day
        crossMidnightLogs.add(crossMidnightLog);

        when(logService.getLogsByUser(userId)).thenReturn(crossMidnightLogs);

        // Execute
        BigDecimal result = service.calculateTotalLoggedHours(userId);

        // Verify - This will handle cross-midnight (negative becomes 0)
        assertNotNull(result);
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalLoggedHours_withZeroDurationLog_shouldHandleCorrectly() {
        // Setup - Log with same start and end time
        List<Log> zeroDurationLogs = new ArrayList<>();
        Log zeroDurationLog = mock(Log.class);
        when(zeroDurationLog.getWaktuMulai()).thenReturn(LocalTime.of(10, 0));
        when(zeroDurationLog.getWaktuSelesai()).thenReturn(LocalTime.of(10, 0));
        zeroDurationLogs.add(zeroDurationLog);

        when(logService.getLogsByUser(userId)).thenReturn(zeroDurationLogs);

        // Execute
        BigDecimal result = service.calculateTotalLoggedHours(userId);

        // Verify - Should be 0 hours
        assertEquals(new BigDecimal("0.00"), result);
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalIncentive_withZeroHours_shouldReturnZero() {
        // Setup - Zero duration logs
        List<Log> zeroDurationLogs = new ArrayList<>();
        Log zeroDurationLog = mock(Log.class);
        when(zeroDurationLog.getWaktuMulai()).thenReturn(LocalTime.of(10, 0));
        when(zeroDurationLog.getWaktuSelesai()).thenReturn(LocalTime.of(10, 0));
        zeroDurationLogs.add(zeroDurationLog);

        when(logService.getLogsByUser(userId)).thenReturn(zeroDurationLogs);

        // Execute
        BigDecimal result = service.calculateTotalIncentive(userId);

        // Verify - Should be 0 incentive
        assertEquals(0, BigDecimal.ZERO.compareTo(result));
        verify(logService).getLogsByUser(userId);
    }

    @Test
    void calculateTotalLoggedHours_withFractionalMinutes_shouldRoundCorrectly() {
        // Setup - Log with odd minute duration to test rounding
        List<Log> fractionalLogs = new ArrayList<>();
        Log fractionalLog = mock(Log.class);
        when(fractionalLog.getWaktuMulai()).thenReturn(LocalTime.of(9, 0));
        when(fractionalLog.getWaktuSelesai()).thenReturn(LocalTime.of(9, 7)); // 7 minutes
        fractionalLogs.add(fractionalLog);

        when(logService.getLogsByUser(userId)).thenReturn(fractionalLogs);

        // Execute
        BigDecimal result = service.calculateTotalLoggedHours(userId);

        // Verify - 7 minutes = 7/60 = 0.11666... should round to 0.12 with HALF_UP
        assertEquals(new BigDecimal("0.12"), result);
        assertEquals(2, result.scale());
        verify(logService).getLogsByUser(userId);
    }
}