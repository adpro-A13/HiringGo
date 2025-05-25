package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.DaftarResponse;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.dto.LowonganDTO;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Pendaftaran;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResponseBuilderService {

    public ResponseEntity<Map<String, Object>> buildSuccessResponse(
            String message, String dataKey, Object data) {
        return buildResponse(HttpStatus.OK, 200, message, dataKey, data);
    }

    public ResponseEntity<Map<String, Object>> buildCreatedResponse(
            String message, String dataKey, Object data) {
        return buildResponse(HttpStatus.CREATED, 201, message, dataKey, data);
    }

    public ResponseEntity<Map<String, Object>> buildRegistrationResponse(Pendaftaran pendaftaran) {
        DaftarResponse daftarResponse = new DaftarResponse(
                true,
                "Berhasil mendaftar asisten dosen",
                pendaftaran
        );

        return buildCreatedResponse(
                "Berhasil mendaftar asisten dosen",
                "pendaftaran",
                daftarResponse
        );
    }

    public ResponseEntity<Map<String, Object>> buildListResponse(
            String message, List<LowonganDTO> items) {
        Map<String, Object> responseData = createBaseResponse(200, message);
        responseData.put("lowongan_list", items);
        responseData.put("total_lowongan", items.size());

        return ResponseEntity.ok(wrapResponse(responseData));
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus httpStatus, int statusCode, String message, String dataKey, Object data) {
        Map<String, Object> responseData = createBaseResponse(statusCode, message);
        responseData.put(dataKey, data);

        return ResponseEntity.status(httpStatus).body(wrapResponse(responseData));
    }

    private Map<String, Object> createBaseResponse(int statusCode, String message) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status_code", statusCode);
        responseData.put("message", message);
        responseData.put("timestamp", System.currentTimeMillis());
        return responseData;
    }

    private Map<String, Object> wrapResponse(Map<String, Object> responseData) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", responseData);
        return result;
    }
}