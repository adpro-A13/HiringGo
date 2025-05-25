package id.ac.ui.cs.advprog.hiringgo.log.repository;
import id.ac.ui.cs.advprog.hiringgo.log.enums.LogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import id.ac.ui.cs.advprog.hiringgo.log.model.Log;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LogRepository extends JpaRepository<Log, UUID> {
    List<Log> findByStatus(LogStatus status);
    List<Log> findByTanggalLogBetweenAndUser_Id(LocalDate start, LocalDate end, UUID userId);
    @Query("SELECT l FROM Log l " +
            "JOIN l.pendaftaran p " +
            "JOIN p.lowongan lo " +
            "JOIN lo.mataKuliah mk " +
            "JOIN mk.dosenPengampu d " +
            "WHERE d.id = :dosenId")
    List<Log> findLogsByDosenMataKuliah(@Param("dosenId") UUID dosenId);
    List<Log> findByUserId(UUID userId);

}
