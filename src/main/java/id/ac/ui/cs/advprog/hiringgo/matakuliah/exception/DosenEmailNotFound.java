package id.ac.ui.cs.advprog.hiringgo.matakuliah.exception;

import org.springframework.http.HttpStatus;

public class DosenEmailNotFound extends BaseException{
    public DosenEmailNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
    public DosenEmailNotFound(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND, cause);
    }
}
