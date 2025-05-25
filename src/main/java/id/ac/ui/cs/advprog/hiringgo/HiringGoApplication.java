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
        // Fallback ke dotenv hanya kalau System.getenv belum ada
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        setPropertyIfNotNull("DB_HOST", getEnvOrDotenv("DB_HOST", dotenv));
        setPropertyIfNotNull("DB_PORT", getEnvOrDotenv("DB_PORT", dotenv));
        setPropertyIfNotNull("DB_USER", getEnvOrDotenv("DB_USER", dotenv));
        setPropertyIfNotNull("DB_PASSWORD", getEnvOrDotenv("DB_PASSWORD", dotenv));
        setPropertyIfNotNull("DB_NAME", getEnvOrDotenv("DB_NAME", dotenv));
    }

    private static String getEnvOrDotenv(String key, Dotenv dotenv) {
        String value = System.getenv(key);
        return (value != null) ? value : dotenv.get(key);
    }

    private static void setPropertyIfNotNull(String key, String value) {
        if (value != null) {
            System.setProperty(key, value);
        }
    }
}