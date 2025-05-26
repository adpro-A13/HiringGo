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

        setPropertyIfNotNull("DB_URL", getEnvOrDotenv("DB_URL", dotenv));
        setPropertyIfNotNull("DB_USERNAME", getEnvOrDotenv("DB_USERNAME", dotenv));
        setPropertyIfNotNull("DB_PASSWORD", getEnvOrDotenv("DB_PASSWORD", dotenv));
        setPropertyIfNotNull("JWT_SECRET", getEnvOrDotenv("JWT_SECRET", dotenv));
        setPropertyIfNotNull("PASSWORD_ADMIN", getEnvOrDotenv("PASSWORD_ADMIN", dotenv));
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