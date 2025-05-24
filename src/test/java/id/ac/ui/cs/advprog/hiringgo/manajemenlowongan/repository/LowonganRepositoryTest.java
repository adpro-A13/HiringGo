package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LowonganRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private LowonganRepository lowonganRepository;
    @Autowired
    private MataKuliahRepository mataKuliahRepository;
    private Lowongan lowongan;
    private MataKuliah mataKuliah;
    @BeforeEach
    void setUp() {
        lowongan = new Lowongan();
        mataKuliah = new MataKuliah("CSUI-MK1", "DAA", "Design analysis");
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setTahunAjaran("2024");
        lowongan.setSemester(String.valueOf(Semester.GANJIL));
        lowongan.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        lowongan.setJumlahAsdosDibutuhkan(2);
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);
    }

    @Test
    void testSaveAndFindById() {
        Lowongan saved = lowonganRepository.save(lowongan);
        Optional<Lowongan> result = lowonganRepository.findById(saved.getLowonganId());

        assertTrue(result.isPresent());
        assertEquals("CSUI-MK1", result.get().getMataKuliah().getKode());
    }

    @Test
    void testFindAll() {
        MataKuliah matakuliah1 = new MataKuliah("CSUI-MK1", "TBD", "Nama MK 1");
        MataKuliah matakuliah2 = new MataKuliah("CSUI-MK2", "TBA", "PDA turing");

        // Simpan dulu objek MataKuliah agar tidak dianggap 'transient'
        mataKuliahRepository.save(matakuliah1);
        mataKuliahRepository.save(matakuliah2);
        entityManager.flush();
        Lowongan lowongan1 = new Lowongan();
        lowongan1.setMataKuliah(matakuliah1);
        lowongan1.setTahunAjaran("2024");
        lowongan1.setSemester(String.valueOf(Semester.GANJIL));
        lowongan1.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        lowongan1.setJumlahAsdosDibutuhkan(5);
        lowongan1.setJumlahAsdosDiterima(0);
        lowongan1.setJumlahAsdosPendaftar(0);

        Lowongan lowongan2 = new Lowongan();
        lowongan2.setMataKuliah(matakuliah2);
        lowongan2.setTahunAjaran("2024");
        lowongan2.setSemester(String.valueOf(Semester.GENAP));
        lowongan2.setStatusLowongan(String.valueOf(StatusLowongan.DITUTUP));
        lowongan2.setJumlahAsdosDibutuhkan(3);
        lowongan2.setJumlahAsdosDiterima(0);
        lowongan2.setJumlahAsdosPendaftar(0);

        lowonganRepository.save(lowongan1);
        lowonganRepository.save(lowongan2);

        List<Lowongan> result = lowonganRepository.findAll();
        assertEquals(2, result.size());
    }


    @Test
    void testUpdateLowongan() {
        Lowongan saved = lowonganRepository.save(lowongan);
        saved.setJumlahAsdosDiterima(1);
        lowonganRepository.save(saved);

        Optional<Lowongan> updated = lowonganRepository.findById(saved.getLowonganId());
        assertTrue(updated.isPresent());
        assertEquals(1, updated.get().getJumlahAsdosDiterima());
    }

    @Test
    void testDeleteById() {
        Lowongan saved = lowonganRepository.save(lowongan);
        UUID id = saved.getLowonganId();

        assertTrue(lowonganRepository.findById(id).isPresent());

        lowonganRepository.deleteById(id);
        assertFalse(lowonganRepository.findById(id).isPresent());
    }

    @Test
    void testFindByIdMataKuliahAndSemesterAndTahunAjaran() {
        MataKuliah matakuliah2 = new MataKuliah("CSUI-MK2", "TBA", "PDA turing");
        mataKuliahRepository.save(matakuliah2);
        entityManager.flush();

        lowongan.setMataKuliah(matakuliah2);
        lowongan.setSemester(String.valueOf(Semester.GANJIL));
        lowongan.setJumlahAsdosDiterima(1);
        lowongan.setJumlahAsdosPendaftar(1);

        lowonganRepository.save(lowongan);

        Optional<Lowongan> result = lowonganRepository.findByMataKuliahAndSemesterAndTahunAjaran(
                matakuliah2, Semester.GANJIL, "2024"
        );

        assertTrue(result.isPresent());
        assertEquals("CSUI-MK2", result.get().getMataKuliah().getKode());
        assertEquals(Semester.GANJIL, result.get().getSemester());
        assertEquals("2024", result.get().getTahunAjaran());
    }
}
