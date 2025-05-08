package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;

import java.util.List;

public interface LowonganFilterStrategy {
    List<Lowongan> filter(List<Lowongan> lowonganList);
}
