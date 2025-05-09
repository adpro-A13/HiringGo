package id.ac.ui.cs.advprog.hiringgo.matakuliah.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemen_akun.dto.DosenDto;
import lombok.*;
import java.util.*;

@Getter
@Setter
public class MataKuliahDTO {
    private String kode;
    private String nama;
    private String deskripsi;
    private List<String> dosenPengampuEmails;
}