package id.ac.ui.cs.advprog.hiringgo.dashboard.service.feature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeatureProviderIntegrationTest {

    private AdminFeatureProvider adminProvider;
    private DosenFeatureProvider dosenProvider;
    private MahasiswaFeatureProvider mahasiswaProvider;

    @BeforeEach
    void setup() {
        adminProvider = new AdminFeatureProvider();
        dosenProvider = new DosenFeatureProvider();
        mahasiswaProvider = new MahasiswaFeatureProvider();
    }

    @Test
    void allProviders_shouldImplementSameInterface() {
        assertTrue(adminProvider instanceof FeatureProvider);
        assertTrue(dosenProvider instanceof FeatureProvider);
        assertTrue(mahasiswaProvider instanceof FeatureProvider);
    }

    @Test
    void allProviders_shouldReturnNonEmptyFeatures() {
        Map<String, String> adminFeatures = adminProvider.getAvailableFeatures();
        Map<String, String> dosenFeatures = dosenProvider.getAvailableFeatures();
        Map<String, String> mahasiswaFeatures = mahasiswaProvider.getAvailableFeatures();

        assertFalse(adminFeatures.isEmpty());
        assertFalse(dosenFeatures.isEmpty());
        assertFalse(mahasiswaFeatures.isEmpty());
    }

    @Test
    void allProviders_shouldHaveProfileFeature() {
        Map<String, String> adminFeatures = adminProvider.getAvailableFeatures();
        Map<String, String> dosenFeatures = dosenProvider.getAvailableFeatures();
        Map<String, String> mahasiswaFeatures = mahasiswaProvider.getAvailableFeatures();

        assertTrue(adminFeatures.containsKey("profile"));
        assertTrue(dosenFeatures.containsKey("profile"));
        assertTrue(mahasiswaFeatures.containsKey("profile"));

        assertEquals("/api/profile", adminFeatures.get("profile"));
        assertEquals("/api/profile", dosenFeatures.get("profile"));
        assertEquals("/api/profile", mahasiswaFeatures.get("profile"));
    }

    @Test
    void providers_shouldHaveUniqueRoleSpecificFeatures() {
        Map<String, String> adminFeatures = adminProvider.getAvailableFeatures();
        Map<String, String> dosenFeatures = dosenProvider.getAvailableFeatures();
        Map<String, String> mahasiswaFeatures = mahasiswaProvider.getAvailableFeatures();

        // Admin-specific features
        assertTrue(adminFeatures.containsKey("manajemenAkun"));
        assertTrue(adminFeatures.containsKey("manajemenMataKuliah"));
        assertFalse(dosenFeatures.containsKey("manajemenAkun"));
        assertFalse(mahasiswaFeatures.containsKey("manajemenAkun"));

        // Dosen-specific features
        assertTrue(dosenFeatures.containsKey("manajemenAsdos"));
        assertFalse(adminFeatures.containsKey("manajemenAsdos"));
        assertFalse(mahasiswaFeatures.containsKey("manajemenAsdos"));

        // Mahasiswa-specific features
        assertTrue(mahasiswaFeatures.containsKey("pendaftaran"));
        assertFalse(adminFeatures.containsKey("pendaftaran"));
        assertFalse(dosenFeatures.containsKey("pendaftaran"));
    }

    @Test
    void allUrls_shouldBeValidApiPaths() {
        Set<String> allUrls = new HashSet<>();

        allUrls.addAll(adminProvider.getAvailableFeatures().values());
        allUrls.addAll(dosenProvider.getAvailableFeatures().values());
        allUrls.addAll(mahasiswaProvider.getAvailableFeatures().values());

        for (String url : allUrls) {
            assertTrue(url.startsWith("/api/"), "URL should start with /api/: " + url);
            assertFalse(url.contains(" "), "URL should not contain spaces: " + url);
            assertFalse(url.endsWith("/"), "URL should not end with slash: " + url);
        }
    }

    @Test
    void providers_shouldReturnConsistentResults() {
        // Test multiple calls return same results
        for (int i = 0; i < 3; i++) {
            Map<String, String> adminFeatures1 = adminProvider.getAvailableFeatures();
            Map<String, String> adminFeatures2 = adminProvider.getAvailableFeatures();
            assertEquals(adminFeatures1, adminFeatures2);

            Map<String, String> dosenFeatures1 = dosenProvider.getAvailableFeatures();
            Map<String, String> dosenFeatures2 = dosenProvider.getAvailableFeatures();
            assertEquals(dosenFeatures1, dosenFeatures2);

            Map<String, String> mahasiswaFeatures1 = mahasiswaProvider.getAvailableFeatures();
            Map<String, String> mahasiswaFeatures2 = mahasiswaProvider.getAvailableFeatures();
            assertEquals(mahasiswaFeatures1, mahasiswaFeatures2);
        }
    }

    @Test
    void providers_performanceTest() {
        long startTime = System.currentTimeMillis();

        // Call each provider multiple times
        for (int i = 0; i < 1000; i++) {
            adminProvider.getAvailableFeatures();
            dosenProvider.getAvailableFeatures();
            mahasiswaProvider.getAvailableFeatures();
        }

        long duration = System.currentTimeMillis() - startTime;
        assertTrue(duration < 1000, "Performance test should complete quickly: " + duration + "ms");
    }
}