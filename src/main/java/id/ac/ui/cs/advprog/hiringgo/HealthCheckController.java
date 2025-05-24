package id.ac.ui.cs.advprog.hiringgo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public String index() {
        return "OK";
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}
