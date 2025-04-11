package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;

import java.util.List;
import java.util.stream.Collectors;

public class FilterBySemester implements LowonganFilterStrategy {

    private Semester semester;

    public FilterBySemester(Semester semester) {
        this.semester = semester;
    }

    @Override
    public List<Lowongan> filter(List<Lowongan> lowonganList) {
        return lowonganList.stream()
                .filter(lowongan -> lowongan.getSemester() == semester)
                .collect(Collectors.toList());
    }
}
