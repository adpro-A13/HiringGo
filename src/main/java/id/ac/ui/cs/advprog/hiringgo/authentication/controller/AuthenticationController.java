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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/auth")
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
        }        try {
            User registeredUser = authenticationService.signup(registerUserDto);
            Map<String, Object> userInfo = sanitizeUser(registeredUser);
            String jwtToken = jwtService.generateToken(userInfo, registeredUser);
            LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime())
                .setUser(userInfo);
            return ResponseEntity.ok(loginResponse);
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
        }        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            Map<String, Object> userInfo = sanitizeUser(authenticatedUser);
            String jwtToken = jwtService.generateToken(userInfo, authenticatedUser);
            LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime())
                .setUser(userInfo);
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Authorization failed");
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

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
        Cookie authTokenCookie = new Cookie("authToken", null);
        authTokenCookie.setPath("/");
        authTokenCookie.setMaxAge(0);
        response.addCookie(authTokenCookie);

        Cookie tokenCookie = new Cookie("token", null);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0);
        response.addCookie(tokenCookie);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status_code", 200);
        responseData.put("message", "Anda berhasil logout!!");

        Map<String, Object> result = new HashMap<>();
        result.put("data", responseData);

        return ResponseEntity.ok(result);
    }

}