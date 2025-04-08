package com.javanc.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(401, "UNAUTHENTICATED", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "UNAUTHORIZED", HttpStatus.FORBIDDEN),;
    private int code;
    private String message;
    private HttpStatus httpStatus;
}
