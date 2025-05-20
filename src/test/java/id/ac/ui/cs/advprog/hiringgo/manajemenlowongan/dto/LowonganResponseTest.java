package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.Semester;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LowonganResponseTest {
    private LowonganMapper lowonganMapper;

    @Mock
    private MataKuliahRepository mataKuliahRepository;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        lowonganMapper = new LowonganMapper(mataKuliahRepository);
        MataKuliah mataKuliah = new MataKuliah("CSGE602022", "AdvProg", "Design Pattern");
        when(mataKuliahRepository.findById("CSGE602022"))
                .thenReturn(java.util.Optional.of(mataKuliah));
    }
    @Test
    @DisplayName("Test LowonganResponse constructor with Lowongan")
    void testConstructorWithLowongan() {
        UUID lowonganId = UUID.randomUUID();
        String idMataKuliah = "CSGE602022";
        String tahunAjaran = "2023/2024";
        Semester semester = Semester.GENAP;
        StatusLowongan statusLowongan = StatusLowongan.DIBUKA;
        int jumlahAsdosDibutuhkan = 5;
        int jumlahAsdosDiterima = 2;
        int jumlahAsdosPendaftar = 10;

        MataKuliah mataKuliah = new MataKuliah("CSGE602022", "AdvProg", "Design Pattern");
        Lowongan lowongan = new Lowongan();
        lowongan.setLowonganId(lowonganId);
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setTahunAjaran(tahunAjaran);
        lowongan.setSemester(semester.toString());  // Convert enum to String
        lowongan.setStatusLowongan(statusLowongan.toString());  // Convert enum to String
        lowongan.setJumlahAsdosDibutuhkan(jumlahAsdosDibutuhkan);
        lowongan.setJumlahAsdosDiterima(jumlahAsdosDiterima);
        lowongan.setJumlahAsdosPendaftar(jumlahAsdosPendaftar);

        LowonganDTO response = lowonganMapper.toDto(lowongan);

        assertEquals(lowonganId, response.getLowonganId());
        assertEquals(idMataKuliah, response.getIdMataKuliah());
        assertEquals(tahunAjaran, response.getTahunAjaran());
        assertEquals(semester, response.getSemester());  // Compare with enum directly
        assertEquals(statusLowongan, response.getStatusLowongan());  // Compare with enum directly
        assertEquals(jumlahAsdosDibutuhkan, response.getJumlahAsdosDibutuhkan());
        assertEquals(jumlahAsdosDiterima, response.getJumlahAsdosDiterima());
        assertEquals(jumlahAsdosPendaftar, response.getJumlahAsdosPendaftar());
    }

    @Test
    @DisplayName("Test LowonganResponse setters")
    void testLowonganResponseSetters() {
        MataKuliah mataKuliah = new MataKuliah("CSGE602022", "advprog", "Design Pattern");

        Lowongan lowongan = new Lowongan();
        lowongan.setMataKuliah(mataKuliah);
        lowongan.setStatusLowongan(String.valueOf(StatusLowongan.DIBUKA));

        LowonganDTO response = lowonganMapper.toDto(lowongan);

        UUID lowonganId = UUID.randomUUID();
        response.setLowonganId(lowonganId);
        response.setIdMataKuliah("CSGE602022");
        response.setTahunAjaran("2023/2024");
        response.setSemester(Semester.GENAP);  // Use enum directly
        response.setStatusLowongan(StatusLowongan.DIBUKA);  // Use enum directly
        response.setJumlahAsdosDibutuhkan(3);
        response.setJumlahAsdosDiterima(1);
        response.setJumlahAsdosPendaftar(5);

        assertEquals(lowonganId, response.getLowonganId());
        assertEquals("CSGE602022", response.getIdMataKuliah());
        assertEquals("2023/2024", response.getTahunAjaran());
        assertEquals(Semester.GENAP, response.getSemester());  // Compare with enum directly
        assertEquals(StatusLowongan.DIBUKA, response.getStatusLowongan());  // Compare with enum directly
        assertEquals(3, response.getJumlahAsdosDibutuhkan());
        assertEquals(1, response.getJumlahAsdosDiterima());
        assertEquals(5, response.getJumlahAsdosPendaftar());
    }
}