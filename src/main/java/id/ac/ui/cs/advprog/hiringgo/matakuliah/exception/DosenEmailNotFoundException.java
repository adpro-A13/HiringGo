package id.ac.ui.cs.advprog.hiringgo.matakuliah.exception;

import org.springframework.http.HttpStatus;

public class DosenEmailNotFoundException extends BaseException{
    public DosenEmailNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
    public DosenEmailNotFoundException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND, cause);
    }
}
