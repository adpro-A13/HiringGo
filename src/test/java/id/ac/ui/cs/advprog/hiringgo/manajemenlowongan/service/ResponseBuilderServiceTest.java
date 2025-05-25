package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.authentication.model.Mahasiswa;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums.StatusPendaftaran;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResponseBuilderServiceTest {

    @InjectMocks
    private ResponseBuilderService responseBuilderService;

    private Pendaftaran testPendaftaran;
    private List<LowonganDTO> testLowonganList;

    @BeforeEach
    void setUp() {
        // Setup test pendaftaran
        Mahasiswa testMahasiswa = new Mahasiswa();
        testMahasiswa.setId(UUID.randomUUID());
        testMahasiswa.setUsername("mahasiswa@test.com");

        Lowongan testLowongan = new Lowongan();
        testLowongan.setLowonganId(UUID.randomUUID());

        testPendaftaran = new Pendaftaran();
        testPendaftaran.setPendaftaranId(UUID.randomUUID());
        testPendaftaran.setKandidat(testMahasiswa);
        testPendaftaran.setLowongan(testLowongan);
        testPendaftaran.setIpk(new BigDecimal("3.75"));
        testPendaftaran.setSks(20);
        testPendaftaran.setStatus(StatusPendaftaran.BELUM_DIPROSES);

        // Setup test lowongan list
        testLowonganList = Arrays.asList(
                new LowonganDTO(),
                new LowonganDTO()
        );
    }

    @Test
    void testBuildSuccessResponse() {
        // Arrange
        String message = "Operation successful";
        String dataKey = "test_data";
        String testData = "Sample data";

        // Act
        ResponseEntity<Map<String, Object>> response = responseBuilderService
                .buildSuccessResponse(message, dataKey, testData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("data"));

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertEquals(200, data.get("status_code"));
        assertEquals(message, data.get("message"));
        assertEquals(testData, data.get(dataKey));
        assertNotNull(data.get("timestamp"));
    }

    @Test
    void testBuildCreatedResponse() {
        // Arrange
        String message = "Resource created";
        String dataKey = "created_item";
        Map<String, String> testData = Map.of("id", "123");

        // Act
        ResponseEntity<Map<String, Object>> response = responseBuilderService
                .buildCreatedResponse(message, dataKey, testData);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertEquals(201, data.get("status_code"));
        assertEquals(message, data.get("message"));
        assertEquals(testData, data.get(dataKey));
        assertNotNull(data.get("timestamp"));
    }

    @Test
    void testBuildRegistrationResponse() {
        // Act
        ResponseEntity<Map<String, Object>> response = responseBuilderService
                .buildRegistrationResponse(testPendaftaran);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertEquals(201, data.get("status_code"));
        assertEquals("Berhasil mendaftar asisten dosen", data.get("message"));
        assertNotNull(data.get("timestamp"));

        // Verify DaftarResponse object - test only the methods that exist
        Object pendaftaranData = data.get("pendaftaran");
        assertInstanceOf(DaftarResponse.class, pendaftaranData);

        DaftarResponse daftarResponse = (DaftarResponse) pendaftaranData;
        assertTrue(daftarResponse.isSuccess());
        assertEquals("Berhasil mendaftar asisten dosen", daftarResponse.getMessage());
        // Remove the line that calls non-existent getPendaftaran() method
    }

    @Test
    void testBuildListResponse() {
        // Arrange
        String message = "List retrieved successfully";

        // Act
        ResponseEntity<Map<String, Object>> response = responseBuilderService
                .buildListResponse(message, testLowonganList);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertEquals(200, data.get("status_code"));
        assertEquals(message, data.get("message"));
        assertEquals(testLowonganList, data.get("lowongan_list"));
        assertEquals(2, data.get("total_lowongan"));
        assertNotNull(data.get("timestamp"));
    }

    @Test
    void testBuildListResponseWithEmptyList() {
        // Arrange
        List<LowonganDTO> emptyList = new ArrayList<>();
        String message = "No items found";

        // Act
        ResponseEntity<Map<String, Object>> response = responseBuilderService
                .buildListResponse(message, emptyList);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertEquals(0, data.get("total_lowongan"));
        assertEquals(emptyList, data.get("lowongan_list"));
    }

    @Test
    void testBuildResponseWithNullData() {
        // Act
        ResponseEntity<Map<String, Object>> response = responseBuilderService
                .buildSuccessResponse("Success with null", "nullable_data", null);

        // Assert
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertTrue(data.containsKey("nullable_data"));
        assertNull(data.get("nullable_data"));
    }
}