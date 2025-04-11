package id.ac.ui.cs.advprog.hiringgo.repository;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LowonganRepositoryTest {

    @Autowired
    private LowonganRepository lowonganRepository;

    @Test
    void testSaveAndFindById() {
        Lowongan lowongan = new Lowongan();
        lowongan.setIdMataKuliah("CSUI-MK1");
        lowongan.setTahunAjaran("2024/2025");
        lowongan.setSemester(String.valueOf(Semester.GANJIL));
        lowongan.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        lowongan.setJumlahAsdosDibutuhkan(2);
        lowongan.setJumlahAsdosDiterima(1);
        lowongan.setJumlahAsdosPendaftar(4);
        lowongan.setIdAsdosDiterima(List.of("asdos-1"));

        Lowongan saved = lowonganRepository.save(lowongan);

        Optional<Lowongan> result = lowonganRepository.findById(saved.getLowonganId());
        assertTrue(result.isPresent());
        assertEquals("CSUI-MK1", result.get().getIdMataKuliah());
    }

    @Test
    void testFindAll() {
        Lowongan lowongan1 = new Lowongan();
        lowongan1.setIdMataKuliah("CSUI-MK1");
        lowongan1.setTahunAjaran("2024/2025");
        lowongan1.setSemester(String.valueOf(Semester.GANJIL));
        lowongan1.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        lowongan1.setJumlahAsdosDibutuhkan(2);
        lowongan1.setJumlahAsdosDiterima(1);
        lowongan1.setJumlahAsdosPendaftar(4);
        lowongan1.setIdAsdosDiterima(new ArrayList<>());

        Lowongan lowongan2 = new Lowongan();
        lowongan2.setIdMataKuliah("CSUI-MK2");
        lowongan2.setTahunAjaran("2024/2025");
        lowongan2.setSemester(String.valueOf(Semester.GENAP));
        lowongan2.setStatusLowongan(String.valueOf(StatusLowongan.DITUTUP));
        lowongan2.setJumlahAsdosDibutuhkan(3);
        lowongan2.setJumlahAsdosDiterima(2);
        lowongan2.setJumlahAsdosPendaftar(5);
        lowongan2.setIdAsdosDiterima(new ArrayList<>());

        lowonganRepository.save(lowongan1);
        lowonganRepository.save(lowongan2);

        List<Lowongan> result = lowonganRepository.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testUpdateLowongan() {
        Lowongan lowongan = new Lowongan();
        lowongan.setIdMataKuliah("CSUI-MK1");
        lowongan.setTahunAjaran("2024/2025");
        lowongan.setSemester(String.valueOf(Semester.GANJIL));
        lowongan.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        lowongan.setJumlahAsdosDibutuhkan(2);
        lowongan.setJumlahAsdosDiterima(1);
        lowongan.setJumlahAsdosPendaftar(4);
        lowongan.setIdAsdosDiterima(new ArrayList<>());

        Lowongan saved = lowonganRepository.save(lowongan);

        // Update
        saved.setJumlahAsdosDiterima(3);
        lowonganRepository.save(saved);

        Optional<Lowongan> updated = lowonganRepository.findById(saved.getLowonganId());
        assertTrue(updated.isPresent());
        assertEquals(3, updated.get().getJumlahAsdosDiterima());
    }
}
