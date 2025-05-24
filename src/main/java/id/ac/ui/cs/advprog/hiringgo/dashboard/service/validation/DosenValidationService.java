package id.ac.ui.cs.advprog.hiringgo.dashboard.service.validation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class DosenValidationService {

    private final UserRepository userRepository;

    @Autowired
    public DosenValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateDosen(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User tidak ditemukan dengan ID: " + userId));

        if (!(user instanceof Dosen)) {
            throw new IllegalArgumentException("User dengan ID: " + userId + " bukan seorang Dosen");
        }
    }

    public Dosen getDosenById(UUID userId) {
        return userRepository.findById(userId)
                .filter(user -> user instanceof Dosen)
                .map(user -> (Dosen) user)
                .orElseThrow(() -> new NoSuchElementException("Dosen tidak ditemukan"));
    }
}