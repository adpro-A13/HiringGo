package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilterBySemester implements LowonganFilterStrategy {

    @Override
    public List<Lowongan> filter(List<Lowongan> lowonganList, String filterValue) {
        Semester semester;
        try {
            semester = Semester.valueOf(filterValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            return lowonganList;
        }

        return lowonganList.stream()
                .filter(lowongan -> lowongan.getSemester() == semester)
                .toList();
    }

    @Override
    public String getStrategyName() {
        return "FilterBySemester";
    }
}
