package id.ac.ui.cs.advprog.hiringgo.authentication.config;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Admin;
import id.ac.ui.cs.advprog.hiringgo.authentication.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Bean
    @Profile("!prod")  // Only runs when not in production profile
    public CommandLineRunner initializeData() {
        return args -> {
            if (userRepository.findByEmail("admin@hiringgo.com").isEmpty()) {
                logger.info("Initializing default admin user");
                
                Admin admin = new Admin(
                    "admin@hiringgo.com",
                    passwordEncoder.encode("admin123")
                );
                admin.setId(UUID.randomUUID());
                userRepository.save(admin);
                
                logger.info("Default admin created with email: admin@hiringgo.com and password: admin123");
            } else {
                logger.info("Admin user already exists, skipping initialization");
            }
        };
    }
}