package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums;

import lombok.Getter;

@Getter
public enum StatusPendaftaran {
    BELUM_DIPROSES("BELUM_DIPROSES"),
    DITERIMA("DITERIMA"),
    DITOLAK("DITOLAK");

    private final String value;

    StatusPendaftaran(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        for (StatusPendaftaran s : StatusPendaftaran.values()) {
            if (s.value.equalsIgnoreCase(param)) {
                return true;
            }
        }
        return false;
    }
}
