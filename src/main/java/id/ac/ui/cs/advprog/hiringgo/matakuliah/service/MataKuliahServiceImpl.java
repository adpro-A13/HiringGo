package id.ac.ui.cs.advprog.hiringgo.matakuliah.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.exception.MataKuliahAlreadyExistException;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.exception.MataKuliahNotFoundException;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MataKuliahServiceImpl implements MataKuliahService {

    private final MataKuliahRepository mataKuliahRepository;

    public MataKuliahServiceImpl(MataKuliahRepository mataKuliahRepository) {
        this.mataKuliahRepository = mataKuliahRepository;
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<MataKuliah> create(MataKuliah mataKuliah) {
        if (mataKuliahRepository.existsById(mataKuliah.getKode())) {
            throw new MataKuliahAlreadyExistException("Kode sudah digunakan.");
        }
        return CompletableFuture.completedFuture(mataKuliahRepository.save(mataKuliah));
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<MataKuliah> update(MataKuliah mataKuliah) {
        if (!mataKuliahRepository.existsById(mataKuliah.getKode())) {
            throw new MataKuliahNotFoundException("Mata Kuliah tidak ditemukan.");
        }
        return CompletableFuture.completedFuture(mataKuliahRepository.save(mataKuliah));
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<Void> deleteByKode(String kode) {
        mataKuliahRepository.deleteById(kode);
        return CompletableFuture.completedFuture(null);
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<MataKuliah>> findAll() {
        return CompletableFuture.completedFuture(mataKuliahRepository.findAll());
    }

    @Override
    public MataKuliah findByKode(String kode) {
        return mataKuliahRepository.findByKode(kode)
                .orElseThrow(() -> new MataKuliahNotFoundException("Mata kuliah tidak ditemukan"));
    }

    @Override
    public List<MataKuliah> findByDosenPengampu(Dosen dosen) {
        if (dosen == null) {
            throw new MataKuliahNotFoundException("Dosen tidak ditemukan");
        }
        return mataKuliahRepository.findByDosenPengampu(dosen);
    }
}