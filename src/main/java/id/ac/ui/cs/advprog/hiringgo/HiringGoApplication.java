package id.ac.ui.cs.advprog.hiringgo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class HiringGoApplication {

    public static void main(String[] args) {
        loadEnv();
        SpringApplication.run(HiringGoApplication.class, args);
    }

    public static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        try {
            System.setProperty("DB_URL", getEnvOrDotenv("DB_URL", dotenv));
            System.setProperty("DB_USERNAME", getEnvOrDotenv("DB_USERNAME", dotenv));
            System.setProperty("DB_PASSWORD", getEnvOrDotenv("DB_PASSWORD", dotenv));
            System.setProperty("JWT_SECRET", getEnvOrDotenv("JWT_SECRET", dotenv));
            System.setProperty("PASSWORD_ADMIN", getEnvOrDotenv("PASSWORD_ADMIN", dotenv));
        } catch (Exception e) {
            System.err.println("Error setting system properties");
        }
    }

    private static String getEnvOrDotenv(String key, Dotenv dotenv) {
        String value = System.getenv(key);
        return (value != null) ? value : dotenv.get(key);
    }
}