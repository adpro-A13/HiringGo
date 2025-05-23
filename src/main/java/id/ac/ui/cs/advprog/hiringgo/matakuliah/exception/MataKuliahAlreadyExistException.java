package id.ac.ui.cs.advprog.hiringgo.matakuliah.exception;

import org.springframework.http.HttpStatus;

public class MataKuliahAlreadyExistException extends BaseException {
    public MataKuliahAlreadyExistException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public MataKuliahAlreadyExistException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
    }
}
