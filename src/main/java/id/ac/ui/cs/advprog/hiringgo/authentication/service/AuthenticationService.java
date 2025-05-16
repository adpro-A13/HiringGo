package id.ac.ui.cs.advprog.hiringgo.authentication.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.Enums.UserRoleEnums;
import id.ac.ui.cs.advprog.hiringgo.authentication.dto.LoginUserDto;
import id.ac.ui.cs.advprog.hiringgo.authentication.dto.RegisterUserDto;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import id.ac.ui.cs.advprog.hiringgo.authentication.factory.UserFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {
        if (input.getEmail() == null || input.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (input.getPassword() == null || input.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (input.getFullName() == null || input.getFullName().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (input.getNim() == null || input.getNim().isEmpty()) {
            throw new IllegalArgumentException("NIM is required");
        }
        if (!input.getPassword().equals(input.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirm password do not match");
        }
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        User user = UserFactory.createUser(
                        UserRoleEnums.MAHASISWA,
                        input.getEmail(), 
                        passwordEncoder.encode(input.getPassword()),
                        input.getFullName(),
                        input.getNim()
                    );
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("nim")) {
                throw new IllegalArgumentException("NIM already exists");
            } else if (e.getMessage() != null && e.getMessage().contains("nip")) {
                throw new IllegalArgumentException("NIP already exists");
            }
            throw e;
        }
    }

    public User authenticate(LoginUserDto input) {
        if (input.getEmail() == null || input.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (input.getPassword() == null || input.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }
}
