package id.ac.ui.cs.advprog.hiringgo.matakuliah.service;

import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MataKuliahServiceImpl implements MataKuliahService {

    private final MataKuliahRepository mataKuliahRepository;

    public MataKuliahServiceImpl(MataKuliahRepository mataKuliahRepository) {
        this.mataKuliahRepository = mataKuliahRepository;
    }

    @Override
    public MataKuliah create(MataKuliah mataKuliah) {
        if (mataKuliahRepository.existsById(mataKuliah.getKode())) {
            throw new RuntimeException("Kode sudah digunakan.");
        }
        return mataKuliahRepository.save(mataKuliah);
    }

    @Override
    public MataKuliah update(MataKuliah mataKuliah) {
        if (!mataKuliahRepository.existsById(mataKuliah.getKode())) {
            throw new RuntimeException("Mata Kuliah tidak ditemukan.");
        }
        return mataKuliahRepository.save(mataKuliah);
    }

    @Override
    public MataKuliah findByKode(String kode) {
        return mataKuliahRepository.findById(kode).orElse(null);
    }

    @Override
    public List<MataKuliah> findAll() {
        return mataKuliahRepository.findAll();
    }

    @Override
    public void deleteByKode(String kode) {
        mataKuliahRepository.deleteById(kode);
    }
}