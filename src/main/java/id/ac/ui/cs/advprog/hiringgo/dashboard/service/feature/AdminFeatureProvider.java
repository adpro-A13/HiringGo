package id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AdminFeatureProvider implements FeatureProvider {

    @Override
    public Map<String, String> getAvailableFeatures() {
        Map<String, String> features = new HashMap<>();
        features.put("manajemenAkun", "/api/admin/accounts");
        features.put("manajemenMataKuliah", "/api/admin/matakuliah");
        features.put("manajemenLowongan", "/api/admin/lowongan");
        features.put("profile", "/api/profile");
        return features;
    }
}