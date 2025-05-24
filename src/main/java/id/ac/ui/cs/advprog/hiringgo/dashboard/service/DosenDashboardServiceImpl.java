package id.ac.ui.cs.advprog.hiringgo.dashboard.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.mapper.LowonganMapper;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.dashboard.dto.DosenDashboardResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusLowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.dto.MataKuliahDTO;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.repository.MataKuliahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DosenDashboardServiceImpl extends AbstractDashboardService {

    private final UserRepository userRepository;
    private final MataKuliahRepository mataKuliahRepository;
    private final LowonganRepository lowonganRepository;
    private final MataKuliahMapper mataKuliahMapper;
    private final LowonganMapper lowonganMapper;

    @Autowired
    public DosenDashboardServiceImpl(
            UserRepository userRepository,
            MataKuliahRepository mataKuliahRepository,
            LowonganRepository lowonganRepository,
            MataKuliahMapper mataKuliahMapper,
            LowonganMapper lowonganMapper) {
        this.userRepository = userRepository;
        this.mataKuliahRepository = mataKuliahRepository;
        this.lowonganRepository = lowonganRepository;
        this.mataKuliahMapper = mataKuliahMapper;
        this.lowonganMapper = lowonganMapper;
    }

    @Override
    protected void validateUser(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User tidak ditemukan dengan ID: " + userId));

        if (!(user instanceof Dosen)) {
            throw new IllegalArgumentException("User dengan ID: " + userId + " bukan seorang Dosen");
        }
    }

    @Override
    protected DashboardResponse createDashboardResponse() {
        return new DosenDashboardResponse();
    }

    @Override
    protected void populateCommonData(UUID userId, DashboardResponse response) {

        Dosen dosen = userRepository.findById(userId)
                .filter(Dosen.class::isInstance)
                .map(Dosen.class::cast)
                .orElseThrow(() -> new NoSuchElementException("Dosen tidak ditemukan dengan ID: " + userId));

        response.setUserRole("DOSEN");
        response.setUsername(dosen.getUsername());
        response.setFullName(dosen.getFullName());

        Map<String, String> features = new HashMap<>();
        features.put("manajemenlowongan", "/api/manajemenlowongan");
        features.put("manajemenAsdos", "/api/asdos");
        features.put("profile", "/api/profile");
        features.put("periksaLog", "/api/log");
        response.setAvailableFeatures(features);
    }

    @Override
    protected void populateRoleSpecificData(UUID userId, DashboardResponse dashboardResponse) {
        DosenDashboardResponse response = (DosenDashboardResponse) dashboardResponse;

        Dosen dosen = getDosenById(userId);

        List<MataKuliah> coursesTaught = getCoursesTaughtByDosen(dosen);

        if (coursesTaught.isEmpty()) {
            setEmptyResponseData(response);
            return;
        }

        List<MataKuliahDTO> coursesDTO = convertCoursesToDTO(coursesTaught);
        response.setCoursesTaught(coursesDTO);

        Map<String, List<LowonganDTO>> lowonganPerCourse = mapLowonganToCourses(coursesTaught);
        response.setLowonganPerCourse(lowonganPerCourse);

        Map<String, Integer> acceptedAssistantsPerCourse = countAcceptedAssistantsPerCourse(coursesTaught);
        response.setAcceptedAssistantsPerCourse(acceptedAssistantsPerCourse);
    }

    private Dosen getDosenById(UUID userId) {
        return userRepository.findById(userId)
                .filter(user -> user instanceof Dosen)
                .map(user -> (Dosen) user)
                .orElseThrow(() -> new NoSuchElementException("Dosen tidak ditemukan"));
    }

    private List<MataKuliah> getCoursesTaughtByDosen(Dosen dosen) {
        try {
            return mataKuliahRepository.findByDosenPengampu(dosen);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<MataKuliahDTO> convertCoursesToDTO(List<MataKuliah> courses) {
        return courses.stream()
                .map(mataKuliahMapper::toDto)
                .collect(Collectors.toList());
    }

    private Map<String, List<LowonganDTO>> mapLowonganToCourses(List<MataKuliah> courses) {
        Map<String, List<LowonganDTO>> result = new HashMap<>();

        for (MataKuliah course : courses) {
            try {
                List<Lowongan> openings = lowonganRepository.findByMataKuliah(course);
                if (openings != null && !openings.isEmpty()) {
                    List<LowonganDTO> openingsDTO = openings.stream()
                            .map(lowonganMapper::toDto)
                            .collect(Collectors.toList());
                    result.put(course.getKode(), openingsDTO);
                }
            } catch (Exception e) {
            }
        }

        return result;
    }

    private Map<String, Integer> countAcceptedAssistantsPerCourse(List<MataKuliah> courses) {
        Map<String, Integer> acceptedPerCourse = new HashMap<>();

        for (MataKuliah course : courses) {
            try {
                String courseCode = course.getKode();
                List<Lowongan> openings = lowonganRepository.findByMataKuliah(course);

                int acceptedForCourse = 0;
                if (openings != null && !openings.isEmpty()) {
                    acceptedForCourse = openings.stream()
                            .mapToInt(Lowongan::getJumlahAsdosDiterima)
                            .sum();
                }

                acceptedPerCourse.put(courseCode, acceptedForCourse);
            } catch (Exception e) {
                acceptedPerCourse.put(course.getKode(), 0);
            }
        }

        return acceptedPerCourse;
    }

    private void setEmptyResponseData(DosenDashboardResponse response) {
        response.setCoursesTaught(Collections.emptyList());
        response.setLowonganPerCourse(Collections.emptyMap());
        response.setAcceptedAssistantsPerCourse(Collections.emptyMap());
    }

    private LowonganDTO convertToLowonganDTO(Lowongan lowongan) {
        return lowonganMapper.toDto(lowongan);
    }
}