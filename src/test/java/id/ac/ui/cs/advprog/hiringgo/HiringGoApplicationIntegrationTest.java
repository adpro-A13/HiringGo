package id.ac.ui.cs.advprog.hiringgo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
})
class HiringGoApplicationIntegrationTest {

    @Test
    void contextLoads() {
    }
    
    @Test
    void mainMethodStartsApplication() {
        HiringGoApplication.main(new String[]{});
    }
}