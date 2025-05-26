package id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DosenFeatureProvider implements FeatureProvider {

    @Override
    public Map<String, String> getAvailableFeatures() {
        Map<String, String> features = new HashMap<>();
        features.put("manajemenlowongan", "/api/manajemenlowongan");
        features.put("manajemenAsdos", "/api/asdos");
        features.put("profile", "/api/profile");
        features.put("periksaLog", "/api/log");
        return features;
    }
}