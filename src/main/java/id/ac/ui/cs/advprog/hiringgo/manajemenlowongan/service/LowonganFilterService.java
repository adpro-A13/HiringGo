package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.LowonganFilterStrategy;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;

import java.util.List;
// saya pisahkan dengan LowonganService agar memenuhi Single Responsibility Principle
public class LowonganFilterService {

    private LowonganFilterStrategy strategy;

    public void setStrategy(LowonganFilterStrategy strategy) {
    }

    public List<Lowongan> filter(List<Lowongan> lowonganList) {
        return null;
    }
}
