package id.ac.ui.cs.advprog.hiringgo.log.repository;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import java.time.LocalDate;
import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByStatus(LogStatus status);
    List<Log> findByTanggalLogBetween(LocalDate start, LocalDate end);
}
