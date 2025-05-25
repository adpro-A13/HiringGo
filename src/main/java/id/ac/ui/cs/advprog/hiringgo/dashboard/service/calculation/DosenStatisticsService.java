package id.ac.ui.cs.advprog.hiringgo.dashboard.service.calculation;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository.LowonganRepository;
import id.ac.ui.cs.advprog.hiringgo.matakuliah.model.MataKuliah;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DosenStatisticsService {

    private final LowonganRepository lowonganRepository;

    @Autowired
    public DosenStatisticsService(LowonganRepository lowonganRepository) {
        this.lowonganRepository = lowonganRepository;
    }

    public Map<String, Integer> countAcceptedAssistantsPerCourse(List<MataKuliah> courses) {
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

    public int getTotalAcceptedAssistants(List<MataKuliah> courses) {
        return countAcceptedAssistantsPerCourse(courses).values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getTotalOpenPositions(List<MataKuliah> courses) {
        int totalOpenPositions = 0;

        for (MataKuliah course : courses) {
            try {
                List<Lowongan> openings = lowonganRepository.findByMataKuliah(course);
                if (openings != null) {
                    totalOpenPositions += openings.stream()
                            .mapToInt(Lowongan::getJumlahAsdosDibutuhkan)
                            .sum();
                }
            } catch (Exception e) {
                //logger handling
            }
        }

        return totalOpenPositions;
    }
}