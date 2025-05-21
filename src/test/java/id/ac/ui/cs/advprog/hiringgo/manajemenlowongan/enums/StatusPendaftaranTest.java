package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class StatusPendaftaranTest {

    @Test
    void testEnumValues() {
        StatusPendaftaran[] values = StatusPendaftaran.values();
        assertEquals(3, values.length);
        assertEquals(StatusPendaftaran.BELUM_DIPROSES, values[0]);
        assertEquals(StatusPendaftaran.DITERIMA, values[1]);
        assertEquals(StatusPendaftaran.DITOLAK, values[2]);
    }

    @Test
    void testValueGetter() {
        assertEquals("BELUM_DIPROSES", StatusPendaftaran.BELUM_DIPROSES.getValue());
        assertEquals("DITERIMA", StatusPendaftaran.DITERIMA.getValue());
        assertEquals("DITOLAK", StatusPendaftaran.DITOLAK.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"BELUM_DIPROSES", "DITERIMA", "DITOLAK"})
    void testContainsWithExactMatch(String value) {
        assertTrue(StatusPendaftaran.contains(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"belum_diproses", "diterima", "ditolak", "Belum_Diproses", "Diterima", "Ditolak"})
    void testContainsWithDifferentCase(String value) {
        assertTrue(StatusPendaftaran.contains(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "APPROVED", "REJECTED", "PROCESSED", "INVALID_STATUS"})
    void testContainsWithNonExistentValues(String value) {
        assertFalse(StatusPendaftaran.contains(value));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testContainsWithNullOrEmpty(String value) {
        assertFalse(StatusPendaftaran.contains(value));
    }

    @Test
    void testToString() {
        assertEquals("BELUM_DIPROSES", StatusPendaftaran.BELUM_DIPROSES.toString());
        assertEquals("DITERIMA", StatusPendaftaran.DITERIMA.toString());
        assertEquals("DITOLAK", StatusPendaftaran.DITOLAK.toString());
    }

    @Test
    void testValueOf() {
        assertEquals(StatusPendaftaran.BELUM_DIPROSES, StatusPendaftaran.valueOf("BELUM_DIPROSES"));
        assertEquals(StatusPendaftaran.DITERIMA, StatusPendaftaran.valueOf("DITERIMA"));
        assertEquals(StatusPendaftaran.DITOLAK, StatusPendaftaran.valueOf("DITOLAK"));

        assertThrows(IllegalArgumentException.class, () -> StatusPendaftaran.valueOf("INVALID"));
    }
}