package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.repository;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PendaftaranRepositoryTest {

    @Autowired
    private PendaftaranRepository pendaftaranRepository;

    @Test
    @DisplayName("findByLowonganLowonganId returns empty list when no data")
    void testFindByLowonganLowonganId_NoData() {
        UUID randomLowonganId = UUID.randomUUID();
        List<Pendaftaran> result = pendaftaranRepository.findByLowonganLowonganId(randomLowonganId);
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty when no entries exist");
    }

    @Test
    @DisplayName("findByKandidatId returns empty list when no data")
    void testFindByKandidatId_NoData() {
        UUID randomKandidatId = UUID.randomUUID();
        List<Pendaftaran> result = pendaftaranRepository.findByKandidatId(randomKandidatId);
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty when no entries exist");
    }

    @Test
    @DisplayName("findByKandidatIdAndLowonganLowonganId returns empty list when no data")
    void testFindByKandidatIdAndLowonganLowonganId_NoData() {
        UUID randomKandidatId = UUID.randomUUID();
        UUID randomLowonganId = UUID.randomUUID();
        List<Pendaftaran> result = pendaftaranRepository
                .findByKandidatIdAndLowonganLowonganId(randomKandidatId, randomLowonganId);
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty when no entries exist");
    }
}