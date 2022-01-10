package com.farmdiary.api.exception;

import org.springframework.http.HttpStatus;

public class DiaryApiException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public DiaryApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
