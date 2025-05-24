package id.ac.ui.cs.advprog.hiringgo.dashboard.service.validation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class MahasiswaValidationService {

    private final UserRepository userRepository;

    @Autowired
    public MahasiswaValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateMahasiswa(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User tidak ditemukan dengan ID: " + userId);
        }

        boolean isMahasiswa = userRepository.findById(userId)
                .filter(Mahasiswa.class::isInstance)
                .isPresent();

        if (!isMahasiswa) {
            throw new IllegalArgumentException("User dengan ID: " + userId + " bukan mahasiswa");
        }
    }

    public Mahasiswa getMahasiswaById(UUID userId) {
        return userRepository.findById(userId)
                .filter(Mahasiswa.class::isInstance)
                .map(Mahasiswa.class::cast)
                .orElseThrow(() -> new NoSuchElementException("Mahasiswa tidak ditemukan dengan ID: " + userId));
    }
}