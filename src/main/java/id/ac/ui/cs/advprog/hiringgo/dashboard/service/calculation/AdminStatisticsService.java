package id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

@Service
public class AdminStatisticsService {

    private final UserRepository userRepository;
    private final MataKuliahRepository mataKuliahRepository;
    private final LowonganRepository lowonganRepository;

    @Autowired
    public AdminStatisticsService(
            UserRepository userRepository,
            MataKuliahRepository mataKuliahRepository,
            LowonganRepository lowonganRepository) {
        this.userRepository = userRepository;
        this.mataKuliahRepository = mataKuliahRepository;
        this.lowonganRepository = lowonganRepository;
    }

    public int countDosen() {
        return (int) StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(Dosen.class::isInstance)
                .count();
    }

    public int countMahasiswa() {
        return (int) StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(Mahasiswa.class::isInstance)
                .count();
    }

    public int countCourses() {
        return (int) mataKuliahRepository.count();
    }

    public int countLowongan() {
        return (int) lowonganRepository.count();
    }

    public CompletableFuture<Integer> countDosenAsync() {
        return CompletableFuture.supplyAsync(this::countDosen);
    }

    public CompletableFuture<Integer> countMahasiswaAsync() {
        return CompletableFuture.supplyAsync(this::countMahasiswa);
    }

    public CompletableFuture<Integer> countCoursesAsync() {
        return CompletableFuture.supplyAsync(this::countCourses);
    }

    public CompletableFuture<Integer> countLowonganAsync() {
        return CompletableFuture.supplyAsync(this::countLowongan);
    }
}