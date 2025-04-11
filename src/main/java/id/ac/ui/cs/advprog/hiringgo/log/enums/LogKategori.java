package id.ac.ui.cs.advprog.hiringgo.log.enums;
import lombok.Getter;

@Getter
public enum LogKategori {
    ASISTENSI("ASISTENSI"),
    MENGOREKSI("MENGOREKSI"),
    MENGAWAS("MENGAWAS"),
    LAIN_LAIN("LAIN-LAIN");

    private final String value;

    private LogKategori(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        for (LogKategori kategori : LogKategori.values()) {
            if (kategori.value.equals(param)) {
                return true;
            }
        }
        return false;
    }
}
