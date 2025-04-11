package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.LowonganFilterStrategy;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LowonganServiceImpl implements LowonganService {

    @Autowired
    private LowonganRepository lowonganRepository;

    private final LowonganFilterService filterService = new LowonganFilterService();

    @Override
    public Lowongan findById(UUID id) {
        return null;
    }

    @Override
    public List<Lowongan> findAll() {
        return null;
    }

    @Override
    public List<Lowongan> filterLowongan(LowonganFilterStrategy strategy) {
        return null;
    }
}
