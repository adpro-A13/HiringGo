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

        if (!mataKuliahStorage.isEmpty()) {
            int i = 0;
            for (MataKuliah m : mataKuliahStorage) {
                if (m.getKode().equals(mataKuliah.getKode())) {
                    mataKuliahStorage.remove(i);
                    mataKuliahStorage.add(i, mataKuliah);
                    return mataKuliah;
                }
                i += 1;
            }
        }
        mataKuliahStorage.add(mataKuliah);
        return mataKuliah;
    }

    public MataKuliah findByKode(String kode) {
        for (MataKuliah m : mataKuliahStorage) {
            if (m.getKode().equals(kode)) {
                return m;
            }
        }
        return null;
    }

    public Iterator<MataKuliah> findAll() {
        return mataKuliahStorage.iterator();
    }

    public void delete(String kode) {
        mataKuliahStorage.removeIf(m -> m.getKode().equals(kode));
    }
}