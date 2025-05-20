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
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User tidak ditemukan dengan ID: " + userId);
        }

        boolean isDosen = userRepository.findById(userId)
                .filter(Dosen.class::isInstance)
                .isPresent();

        if (!isDosen) {
            throw new IllegalArgumentException("User dengan ID: " + userId + " bukan dosen");
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
    protected void populateRoleSpecificData(UUID userId, DashboardResponse baseResponse) {
        DosenDashboardResponse response = (DosenDashboardResponse) baseResponse;

        Dosen dosen = userRepository.findById(userId)
                .filter(Dosen.class::isInstance)
                .map(Dosen.class::cast)
                .orElseThrow(() -> new NoSuchElementException("Dosen tidak ditemukan"));

        List<MataKuliah> coursesTaught = mataKuliahRepository.findByDosenPengampu(dosen);
        response.setCourseCount(coursesTaught.size());

        List<MataKuliahDTO> courseDTOs = mataKuliahMapper.toDtoList(coursesTaught);
        response.setCourses(courseDTOs);

        List<String> mataKuliahKodes = coursesTaught.stream()
                .map(MataKuliah::getKode)
                .collect(Collectors.toList());

        int totalAcceptedAssistants = 0;
        int totalOpenPositions = 0;

        List<Lowongan> allOpenLowongan = lowonganRepository.findByStatusLowongan(StatusLowongan.DIBUKA);

        List<Lowongan> dosenLowongan = allOpenLowongan.stream()
                .filter(l -> l.getMataKuliah() != null &&
                        mataKuliahKodes.contains(l.getMataKuliah().getKode()))
                .collect(Collectors.toList());

        for (Lowongan lowongan : dosenLowongan) {
            totalAcceptedAssistants += lowongan.getJumlahAsdosDiterima();

            int openPositionsInCourse = lowongan.getJumlahAsdosDibutuhkan() - lowongan.getJumlahAsdosDiterima();
            int allowedPosition = 0;
            if (openPositionsInCourse > allowedPosition) {
                totalOpenPositions += openPositionsInCourse;
            }
        }

        response.setAcceptedAssistantCount(totalAcceptedAssistants);
        response.setOpenPositionCount(totalOpenPositions);

        List<LowonganDTO> openLowonganDTOs = dosenLowongan.stream()
                .filter(l -> l.getJumlahAsdosDiterima() < l.getJumlahAsdosDibutuhkan())
                .map(this::convertToLowonganDTO)
                .collect(Collectors.toList());

        response.setOpenPositions(openLowonganDTOs);
    }

    private LowonganDTO convertToLowonganDTO(Lowongan lowongan) {
        return lowonganMapper.toDto(lowongan);
    }
}