package id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MahasiswaFeatureProvider {

    public Map<String, String> getAvailableFeatures() {
        Map<String, String> features = new HashMap<>();
        features.put("pendaftaran", "/api/pendaftaran");
        features.put("lowongan", "/api/lowongan");
        features.put("profile", "/api/profile");
        features.put("logActivities", "/api/log");
        return features;
    }
}