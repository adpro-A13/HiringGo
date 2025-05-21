package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@Component
public class FilterByStatus implements LowonganFilterStrategy {

    @Override
    public List<Lowongan> filter(List<Lowongan> lowonganList, String filterValue) {
        StatusLowongan status;
        try {
            status = StatusLowongan.valueOf(filterValue.toUpperCase()); // enum parsing
        } catch (IllegalArgumentException e) {
            return lowonganList; // fallback jika input tidak valid
        }

        return lowonganList.stream()
                .filter(lowongan -> lowongan.getStatusLowongan() == status)
                .collect(Collectors.toList());
    }

    @Override
    public String getStrategyName() {
        return "FilterByStatus"; // bisa pakai nama lain jika ingin
    }
}
