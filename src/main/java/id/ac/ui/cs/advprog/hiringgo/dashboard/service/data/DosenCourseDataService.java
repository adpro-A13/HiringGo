package id.ac.ui.cs.advprog.hiringgo.dashboard.service.data;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DosenCourseDataService {

    private final MataKuliahRepository mataKuliahRepository;
    private final LowonganRepository lowonganRepository;
    private final MataKuliahMapper mataKuliahMapper;
    private final LowonganMapper lowonganMapper;

    @Autowired
    public DosenCourseDataService(
            MataKuliahRepository mataKuliahRepository,
            LowonganRepository lowonganRepository,
            MataKuliahMapper mataKuliahMapper,
            LowonganMapper lowonganMapper) {
        this.mataKuliahRepository = mataKuliahRepository;
        this.lowonganRepository = lowonganRepository;
        this.mataKuliahMapper = mataKuliahMapper;
        this.lowonganMapper = lowonganMapper;
    }

    public List<MataKuliah> getCoursesTaughtByDosen(Dosen dosen) {
        try {
            return mataKuliahRepository.findByDosenPengampu(dosen);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<MataKuliahDTO> convertCoursesToDTO(List<MataKuliah> courses) {
        return courses.stream()
                .map(mataKuliahMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LowonganDTO> getLowonganForCourse(MataKuliah course) {
        try {
            List<Lowongan> openings = lowonganRepository.findByMataKuliah(course);
            if (openings != null && !openings.isEmpty()) {
                return openings.stream()
                        .map(lowonganMapper::toDto)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public CompletableFuture<Map<String, List<LowonganDTO>>> mapLowonganToCoursesAsync(List<MataKuliah> courses) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, List<LowonganDTO>> result = new HashMap<>();

            List<CompletableFuture<Void>> futures = courses.stream()
                    .map(course -> CompletableFuture.runAsync(() -> {
                        List<LowonganDTO> lowonganList = getLowonganForCourse(course);
                        if (!lowonganList.isEmpty()) {
                            synchronized(result) {
                                result.put(course.getKode(), lowonganList);
                            }
                        }
                    }))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return result;
        });
    }
}