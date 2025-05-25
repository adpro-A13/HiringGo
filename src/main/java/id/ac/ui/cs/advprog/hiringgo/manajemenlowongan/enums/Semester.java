package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.enums;

import lombok.Getter;

@Getter
public enum Semester {
    GANJIL("GANJIL"),
    GENAP("GENAP");

    private final String value;

    Semester(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        for (Semester s : Semester.values()) {
            if (s.value.equalsIgnoreCase(param)) {
                return true;
            }
        }
        return false;
    }
}