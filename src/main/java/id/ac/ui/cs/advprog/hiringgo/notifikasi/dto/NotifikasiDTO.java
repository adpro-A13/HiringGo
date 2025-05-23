package id.ac.ui.cs.advprog.hiringgo.notifikasi.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotifikasiDTO {
    private UUID id;
    private String mataKuliahNama;
    private String tahunAjaran;
    private String semester;
    private String status;
    private boolean read;
    private LocalDateTime createdAt;
}
