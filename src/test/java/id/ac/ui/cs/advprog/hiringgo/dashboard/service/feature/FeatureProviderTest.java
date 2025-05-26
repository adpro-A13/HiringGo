package id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeatureProviderTest {

    @Test
    void featureProvider_isAnInterface() {
        assertTrue(FeatureProvider.class.isInterface());
    }

    @Test
    void featureProvider_hasCorrectMethod() throws NoSuchMethodException {
        // Verify the interface has the expected method
        assertNotNull(FeatureProvider.class.getMethod("getAvailableFeatures"));

        // Verify method return type
        assertEquals(Map.class, FeatureProvider.class.getMethod("getAvailableFeatures").getReturnType());
    }

    @Test
    void featureProvider_canBeImplemented() {
        // Create anonymous implementation to test interface
        FeatureProvider testProvider = new FeatureProvider() {
            @Override
            public Map<String, String> getAvailableFeatures() {
                Map<String, String> features = new HashMap<>();
                features.put("test", "/api/test");
                return features;
            }
        };

        assertNotNull(testProvider);
        Map<String, String> features = testProvider.getAvailableFeatures();
        assertNotNull(features);
        assertEquals(1, features.size());
        assertEquals("/api/test", features.get("test"));
    }

    @Test
    void allImplementations_shouldImplementInterface() {
        assertTrue(FeatureProvider.class.isAssignableFrom(AdminFeatureProvider.class));
        assertTrue(FeatureProvider.class.isAssignableFrom(DosenFeatureProvider.class));
        assertTrue(FeatureProvider.class.isAssignableFrom(MahasiswaFeatureProvider.class));
    }
}