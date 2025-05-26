package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class AuthenticationValidatorService {

    private final UserRepository userRepository;

    @Autowired
    public AuthenticationValidatorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mahasiswa validateAndGetCurrentUser(Principal principal) {
        validateAuthentication(principal);
        return getCurrentMahasiswa(principal);
    }

    private void validateAuthentication(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Anda harus login terlebih dahulu");
        }
    }

    private Mahasiswa getCurrentMahasiswa(Principal principal) {
        String email = principal.getName();
        return (Mahasiswa) userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan: " + email));
    }
}