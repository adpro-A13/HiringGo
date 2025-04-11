package id.ac.ui.cs.advprog.hiringgo.repository;

import id.ac.ui.cs.advprog.hiringgo.model.MataKuliah;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Repository
public class MataKuliahRepository {

    private final List<MataKuliah> mataKuliahStorage = new ArrayList<>();

    public MataKuliah save(MataKuliah mataKuliah) {

    }

    public MataKuliah findByKode(String kode) {

    }

    public Iterator<MataKuliah> findAll() {

    }

    public void delete(String kode) {

    }
}