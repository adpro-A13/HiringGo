package id.ac.ui.cs.advprog.hiringgo.matakuliah.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MataKuliahService {
    CompletableFuture<MataKuliah> create(MataKuliah mataKuliah);
    CompletableFuture<MataKuliah> update(MataKuliah mataKuliah);
    CompletableFuture<Void> deleteByKode(String kode);
    CompletableFuture<List<MataKuliah>> findAll();
    MataKuliah findByKode(String kode);
    List<MataKuliah> findByDosenPengampu(Dosen dosen);
}