package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
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
    private LowonganRepository lowonganRepository;

    private Lowongan lowongan;

    @BeforeEach
    void setUp() {
        lowongan = new Lowongan();
        lowongan.setIdMataKuliah("CSUI-MK1");
        lowongan.setTahunAjaran("2024/2025");
        lowongan.setSemester(String.valueOf(Semester.GANJIL));
        lowongan.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));
        lowongan.setJumlahAsdosDibutuhkan(2);
        lowongan.setJumlahAsdosDiterima(1);
        lowongan.setJumlahAsdosPendaftar(4);
        lowongan.setIdAsdosDiterima(new ArrayList<>(List.of("asdos-1")));
    }

    @Test
    void testSaveAndFindById() {
        Lowongan saved = lowonganRepository.save(lowongan);
        Optional<Lowongan> result = lowonganRepository.findById(saved.getLowonganId());

        assertTrue(result.isPresent());
        assertEquals("CSUI-MK1", result.get().getIdMataKuliah());
    }

    @Test
    void testFindAll() {
        Lowongan lowongan2 = new Lowongan();
        lowongan2.setIdMataKuliah("CSUI-MK2");
        lowongan2.setTahunAjaran("2024/2025");
        lowongan2.setSemester(String.valueOf(Semester.GENAP));
        lowongan2.setStatusLowongan(String.valueOf(StatusLowongan.DITUTUP));
        lowongan2.setJumlahAsdosDibutuhkan(3);
        lowongan2.setJumlahAsdosDiterima(2);
        lowongan2.setJumlahAsdosPendaftar(5);
        lowongan2.setIdAsdosDiterima(new ArrayList<>());

        lowonganRepository.save(lowongan);
        lowonganRepository.save(lowongan2);

        List<Lowongan> result = lowonganRepository.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testUpdateLowongan() {
        Lowongan saved = lowonganRepository.save(lowongan);
        saved.setJumlahAsdosDiterima(3);
        lowonganRepository.save(saved);

        Optional<Lowongan> updated = lowonganRepository.findById(saved.getLowonganId());
        assertTrue(updated.isPresent());
        assertEquals(3, updated.get().getJumlahAsdosDiterima());
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
        lowongan.setIdMataKuliah("CSUI-MK2");
        lowongan.setSemester(String.valueOf(Semester.GANJIL));
        lowongan.setJumlahAsdosDiterima(0);
        lowongan.setJumlahAsdosPendaftar(0);
        lowongan.setIdAsdosDiterima(new ArrayList<>());

        lowonganRepository.save(lowongan);

        Optional<Lowongan> result = lowonganRepository.findByIdMataKuliahAndSemesterAndTahunAjaran(
                "CSUI-MK2", Semester.GANJIL, "2024/2025"
        );

        assertTrue(result.isPresent());
        assertEquals("CSUI-MK2", result.get().getIdMataKuliah());
        assertEquals(Semester.GANJIL, result.get().getSemester());
        assertEquals("2024/2025", result.get().getTahunAjaran());
    }
}
