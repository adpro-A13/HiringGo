package id.ac.ui.cs.advprog.hiringgo.matakuliah.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MataKuliahExceptionHandlerTest {

    private final MataKuliahExceptionHandler handler = new MataKuliahExceptionHandler();

    @Test
    void testHandleMataKuliahNotFoundException() {
        BaseException ex = new MataKuliahNotFoundException("Mata kuliah tidak ditemukan");

        ResponseEntity<Map<String, Object>> response = handler.handleBaseException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("Mata kuliah tidak ditemukan", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void testHandleMataKuliahAlreadyExistException() {
        BaseException ex = new MataKuliahAlreadyExistException("Kode sudah digunakan");

        ResponseEntity<Map<String, Object>> response = handler.handleBaseException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Kode sudah digunakan", response.getBody().get("message"));
    }

    @Test
    void testHandleDosenEmailNotFoundException() {
        BaseException ex = new DosenEmailNotFound("Dosen tidak ditemukan");

        ResponseEntity<Map<String, Object>> response = handler.handleBaseException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Dosen tidak ditemukan", response.getBody().get("message"));
    }
}
