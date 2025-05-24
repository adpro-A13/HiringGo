package id.ac.ui.cs.advprog.hiringgo.log.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogEnumsTest {

    @Test
    void testLogKategoriContains() {
        // Valid categories
        assertTrue(LogKategori.contains("ASISTENSI"));
        assertTrue(LogKategori.contains("MENGOREKSI"));
        assertTrue(LogKategori.contains("MENGAWAS"));
        assertTrue(LogKategori.contains("LAIN-LAIN"));

        // Invalid categories
        assertFalse(LogKategori.contains("UNKNOWN"));
        assertFalse(LogKategori.contains(""));
        assertFalse(LogKategori.contains(null));
    }

    @Test
    void testLogStatusContains() {
        // Valid statuses
        assertTrue(LogStatus.contains("MENUNGGU"));
        assertTrue(LogStatus.contains("DITERIMA"));
        assertTrue(LogStatus.contains("DITOLAK"));

        // Invalid statuses
        assertFalse(LogStatus.contains("PENDING"));
        assertFalse(LogStatus.contains(""));
        assertFalse(LogStatus.contains(null));
    }

    @Test
    void testLogKategoriGetValue() {
        assertEquals("ASISTENSI", LogKategori.ASISTENSI.getValue());
        assertEquals("MENGOREKSI", LogKategori.MENGOREKSI.getValue());
        assertEquals("MENGAWAS", LogKategori.MENGAWAS.getValue());
        assertEquals("LAIN-LAIN", LogKategori.LAIN_LAIN.getValue());
    }

    @Test
    void testLogStatusGetValue() {
        assertEquals("MENUNGGU", LogStatus.MENUNGGU.getValue());
        assertEquals("DITERIMA", LogStatus.DITERIMA.getValue());
        assertEquals("DITOLAK", LogStatus.DITOLAK.getValue());
    }
}