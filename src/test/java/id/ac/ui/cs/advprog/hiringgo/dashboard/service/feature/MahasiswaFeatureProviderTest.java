package id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MahasiswaFeatureProviderTest {

    private MahasiswaFeatureProvider provider;

    @BeforeEach
    void setup() {
        provider = new MahasiswaFeatureProvider();
    }

    @Test
    void constructor_shouldInitializeCorrectly() {
        MahasiswaFeatureProvider newProvider = new MahasiswaFeatureProvider();
        assertNotNull(newProvider);
    }

    @Test
    void getAvailableFeatures_shouldReturnCorrectFeatures() {
        Map<String, String> features = provider.getAvailableFeatures();

        assertNotNull(features);
        assertEquals(4, features.size());

        assertEquals("/api/pendaftaran", features.get("pendaftaran"));
        assertEquals("/api/lowongan", features.get("lowongan"));
        assertEquals("/api/profile", features.get("profile"));
        assertEquals("/api/log", features.get("logActivities"));
    }

    @Test
    void getAvailableFeatures_shouldContainAllExpectedKeys() {
        Map<String, String> features = provider.getAvailableFeatures();

        assertTrue(features.containsKey("pendaftaran"));
        assertTrue(features.containsKey("lowongan"));
        assertTrue(features.containsKey("profile"));
        assertTrue(features.containsKey("logActivities"));
    }

    @Test
    void getAvailableFeatures_shouldReturnNewMapEachTime() {
        Map<String, String> features1 = provider.getAvailableFeatures();
        Map<String, String> features2 = provider.getAvailableFeatures();

        assertNotSame(features1, features2);
        assertEquals(features1, features2);
    }

    @Test
    void getAvailableFeatures_mapShouldBeMutable() {
        Map<String, String> features = provider.getAvailableFeatures();

        int originalSize = features.size();
        features.put("testKey", "testValue");

        assertEquals(originalSize + 1, features.size());
        assertTrue(features.containsKey("testKey"));
        assertEquals("testValue", features.get("testKey"));
    }

    @Test
    void getAvailableFeatures_shouldNotReturnNull() {
        Map<String, String> features = provider.getAvailableFeatures();
        assertNotNull(features);

        for (String key : features.keySet()) {
            assertNotNull(key);
            assertNotNull(features.get(key));
        }
    }

    @Test
    void getAvailableFeatures_multipleCallsShouldReturnSameContent() {
        Map<String, String> features1 = provider.getAvailableFeatures();
        Map<String, String> features2 = provider.getAvailableFeatures();
        Map<String, String> features3 = provider.getAvailableFeatures();

        assertEquals(features1.size(), features2.size());
        assertEquals(features2.size(), features3.size());

        for (String key : features1.keySet()) {
            assertEquals(features1.get(key), features2.get(key));
            assertEquals(features2.get(key), features3.get(key));
        }
    }

    @Test
    void implementsFeatureProvider_shouldBeTrue() {
        assertTrue(provider instanceof FeatureProvider);
    }

    @Test
    void getAvailableFeatures_shouldHaveCorrectUrlPaths() {
        Map<String, String> features = provider.getAvailableFeatures();

        // Verify all URLs start with /api/
        for (String url : features.values()) {
            assertTrue(url.startsWith("/api/"), "URL should start with /api/: " + url);
        }
    }

    @Test
    void getAvailableFeatures_shouldContainStudentSpecificFeatures() {
        Map<String, String> features = provider.getAvailableFeatures();

        // Verify student-specific features
        assertTrue(features.containsKey("pendaftaran"), "Should contain pendaftaran feature");
        assertTrue(features.containsKey("lowongan"), "Should contain lowongan feature");

        // Verify URLs are correct for student features
        assertEquals("/api/pendaftaran", features.get("pendaftaran"));
        assertEquals("/api/lowongan", features.get("lowongan"));
    }
}