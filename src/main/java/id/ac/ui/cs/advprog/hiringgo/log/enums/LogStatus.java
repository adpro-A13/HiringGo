package id.ac.ui.cs.advprog.hiringgo.log.enums;
import lombok.Getter;

@Getter
public enum LogStatus {
    MENUNGGU("MENUNGGU"),
    DITERIMA("DITERIMA"),
    DITOLAK("DITOLAK");

    private final String value;

    private LogStatus(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        for (LogStatus status : LogStatus.values()) {
            if (status.value.equals(param)) {
                return true;
            }
        }
        return false;
    }
}
