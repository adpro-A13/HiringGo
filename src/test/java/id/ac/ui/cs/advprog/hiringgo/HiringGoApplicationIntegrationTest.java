package id.ac.ui.cs.advprog.hiringgo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "app.default-admin-password=password123"
})
class HiringGoApplicationIntegrationTest {

    @Test
    void contextLoads() {
    }
    
    @Test
    void mainMethodExists() {
        try {
            HiringGoApplication.class.getMethod("main", String[].class);
            assert true;
        } catch (NoSuchMethodException e) {
            assert false : "main method does not exist";
        }
    }
}