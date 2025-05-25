package id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation;

import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import id.ac.ui.cs.advprog.hiringgo.log.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class IncentiveCalculationService {

    private final LogService logService;
    private static final BigDecimal HOURLY_RATE = BigDecimal.valueOf(27500);

    @Autowired
    public IncentiveCalculationService(LogService logService) {
        this.logService = logService;
    }

    public BigDecimal calculateTotalLoggedHours(UUID userId) {
        List<Log> logs = logService.getLogsByUser(userId);
        long totalLoggedMinutes = logs.stream()
                .map(log -> Duration.between(log.getWaktuMulai(), log.getWaktuSelesai()))
                .mapToLong(Duration::toMinutes)
                .sum();
        return BigDecimal.valueOf(totalLoggedMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalIncentive(UUID userId) {
        BigDecimal totalHours = calculateTotalLoggedHours(userId);
        return totalHours.multiply(HOURLY_RATE);
    }

    public CompletableFuture<BigDecimal> calculateTotalLoggedHoursAsync(UUID userId) {
        return CompletableFuture.supplyAsync(() -> calculateTotalLoggedHours(userId));
    }

    public CompletableFuture<BigDecimal> calculateTotalIncentiveAsync(UUID userId) {
        return calculateTotalLoggedHoursAsync(userId)
                .thenApply(hours -> hours.multiply(HOURLY_RATE));
    }
}