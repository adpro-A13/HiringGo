package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;

import java.util.List;
import java.util.stream.Collectors;

public class FilterByStatus implements LowonganFilterStrategy {

    private StatusLowongan status;

    public FilterByStatus(StatusLowongan status) {
        this.status = status;
    }

    @Override
    public List<Lowongan> filter(List<Lowongan> lowonganList) {
        return lowonganList.stream()
                .filter(lowongan -> lowongan.getStatusLowongan() == status)
                .collect(Collectors.toList());
    }
}
