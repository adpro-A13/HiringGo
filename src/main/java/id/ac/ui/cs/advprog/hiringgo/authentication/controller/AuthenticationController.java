package id.ac.ui.cs.advprog.hiringgo.authentication.controller;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Dosen;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.authentication.model.User;
import id.ac.ui.cs.advprog.hiringgo.authentication.dto.LoginUserDto;
import id.ac.ui.cs.advprog.hiringgo.authentication.dto.RegisterUserDto;
import id.ac.ui.cs.advprog.hiringgo.authentication.response.LoginResponse;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.AuthenticationService;
import id.ac.ui.cs.advprog.hiringgo.authentication.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        if (registerUserDto.getEmail() == null || registerUserDto.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (registerUserDto.getPassword() == null || registerUserDto.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }
        if (registerUserDto.getConfirmPassword() == null || registerUserDto.getConfirmPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Confirm password is required");
        }
        if (!registerUserDto.getPassword().equals(registerUserDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }
        if (registerUserDto.getFullName() == null || registerUserDto.getFullName().isEmpty()) {
            return ResponseEntity.badRequest().body("Full name is required");
        }
        if (registerUserDto.getNim() == null || registerUserDto.getNim().isEmpty()) {
            return ResponseEntity.badRequest().body("NIM is required");
        }
        try {
            User registeredUser = authenticationService.signup(registerUserDto);
            return ResponseEntity.ok(sanitizeUser(registeredUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Register failed, please try again.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto) {
        if (loginUserDto.getEmail() == null || loginUserDto.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (loginUserDto.getPassword() == null || loginUserDto.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            String jwtToken = jwtService.generateToken(authenticatedUser);
            LoginResponse loginResponse = new LoginResponse()
                    .setToken(jwtToken)
                    .setExpiresIn(jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(403).body("Authorization failed");
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(
            @org.springframework.web.bind.annotation.RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("Missing or invalid Authorization header");
        }
        String token = authorizationHeader.substring(7);
        User user = authenticationService.verifyToken(token);
        if (user != null) {
            return ResponseEntity.ok(sanitizeUser(user));
        } else {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }

    private Map<String, Object> sanitizeUser(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getUsername());

        userMap.put("role", user.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("UNKNOWN"));

        if (user instanceof Mahasiswa) {
            Mahasiswa mahasiswa = (Mahasiswa) user;
            userMap.put("fullName", mahasiswa.getFullName());
            userMap.put("nim", mahasiswa.getNim());
        } else if (user instanceof Dosen) {
            Dosen dosen = (Dosen) user;
            userMap.put("fullName", dosen.getFullName());
            userMap.put("nip", dosen.getNip());
        }

        return userMap;
    }
}