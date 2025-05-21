package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.sort;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;

import java.util.List;

public interface LowonganSortStrategy {
    List<Lowongan> sort(List<Lowongan> lowonganList);
}
