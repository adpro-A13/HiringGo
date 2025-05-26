package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.sort;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class SortByJumlahAsdosDiterima implements LowonganSortStrategy {
    @Override
    public List<Lowongan> sort(List<Lowongan> lowonganList) {
        return lowonganList.stream()
                .sorted(Comparator.comparingInt(Lowongan::getJumlahAsdosDiterima))
                .toList();
    }
}