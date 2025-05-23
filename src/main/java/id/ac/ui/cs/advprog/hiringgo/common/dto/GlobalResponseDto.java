package id.ac.ui.cs.advprog.hiringgo.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalResponseDto<T> {
    private boolean success;
    private int statusCode;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> GlobalResponseDto<T> success(T data, String message) {
        return GlobalResponseDto.<T>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> GlobalResponseDto<T> success(T data, String message, HttpStatus statusCode) {
        return GlobalResponseDto.<T>builder()
                .success(true)
                .statusCode(statusCode.value())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> GlobalResponseDto<T> success(String message) {
        return GlobalResponseDto.<T>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> GlobalResponseDto<T> error(String message, HttpStatus statusCode) {
        return GlobalResponseDto.<T>builder()
                .success(false)
                .statusCode(statusCode.value())
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> GlobalResponseDto<T> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }

    public static <T> GlobalResponseDto<T> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }

    public static <T> GlobalResponseDto<T> serverError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
