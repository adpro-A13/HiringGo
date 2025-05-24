package id.ac.ui.cs.advprog.hiringgo.dashboard.service.validation;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AdminValidationService {

    private final UserRepository userRepository;

    @Autowired
    public AdminValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateAdmin(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User tidak ditemukan dengan ID: " + userId);
        }

        boolean isAdmin = userRepository.findById(userId)
                .filter(Admin.class::isInstance)
                .isPresent();

        if (!isAdmin) {
            throw new IllegalArgumentException("User dengan ID: " + userId + " bukan admin");
        }
    }

    public Admin getAdminById(UUID userId) {
        return userRepository.findById(userId)
                .filter(Admin.class::isInstance)
                .map(Admin.class::cast)
                .orElseThrow(() -> new NoSuchElementException("Admin tidak ditemukan dengan ID: " + userId));
    }
}