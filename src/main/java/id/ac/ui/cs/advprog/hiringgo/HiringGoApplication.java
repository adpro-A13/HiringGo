package id.ac.ui.cs.advprog.hiringgo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HiringGoApplication {

    public static void main(String[] args) {
        loadEnv();
        SpringApplication.run(HiringGoApplication.class, args);
    }


    public static void loadEnv() {
        // Fallback ke dotenv hanya kalau System.getenv belum ada
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        System.setProperty("DB_HOST", getEnvOrDotenv("DB_HOST", dotenv));
        System.setProperty("DB_PORT", getEnvOrDotenv("DB_PORT", dotenv));
        System.setProperty("DB_USER", getEnvOrDotenv("DB_USER", dotenv));
        System.setProperty("DB_PASSWORD", getEnvOrDotenv("DB_PASSWORD", dotenv));
        System.setProperty("DB_NAME", getEnvOrDotenv("DB_NAME", dotenv));
    }

    private static String getEnvOrDotenv(String key, Dotenv dotenv) {
        String value = System.getenv(key);
        return (value != null) ? value : dotenv.get(key);
    }
}

