package id.ac.ui.cs.advprog.hiringgo.matakuliah.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import java.util.List;

public interface MataKuliahService {
    MataKuliah create(MataKuliah mataKuliah);
    MataKuliah update(MataKuliah mataKuliah);
    MataKuliah findByKode(String kode);
    List<MataKuliah> findByDosenPengampu(Dosen dosen);
    List<MataKuliah> findAll();
    void deleteByKode(String kode);
}