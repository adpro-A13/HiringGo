package id.ac.ui.cs.advprog.hiringgo.matakuliah.exception;

import org.springframework.http.HttpStatus;

public class MataKuliahNotFoundException extends BaseException{
    public MataKuliahNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
