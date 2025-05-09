package id.ac.ui.cs.advprog.hiringgo.matakuliah.dto;

import lombok.*;
import java.util.*;

@Getter
@Setter
public class MataKuliahDTO {
    private String kode;
    private String nama;
    private String deskripsi;
    private List<String> dosenPengampu;
}