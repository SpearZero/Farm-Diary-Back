package com.farmdiary.api.exception.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorDetails {

    private final LocalDateTime timestamp;
    private final Result message;
    private final String details;

    @Builder(access = AccessLevel.PRIVATE)
    private ErrorDetails(LocalDateTime timestamp, Result message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    public static ErrorDetails getErrorDetails(Result message, String details) {
        return ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(message)
                .details(details)
                .build();
    }
}
