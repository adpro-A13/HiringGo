package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums;

import lombok.Getter;

@Getter
public enum StatusLowongan {
    DIBUKA("DIBUKA"),
    DITUTUP("DITUTUP");

    private final String value;

    StatusLowongan(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        for (StatusLowongan s : StatusLowongan.values()) {
            if (s.value.equalsIgnoreCase(param)) {
                return true;
            }
        }
        return false;
    }
}
