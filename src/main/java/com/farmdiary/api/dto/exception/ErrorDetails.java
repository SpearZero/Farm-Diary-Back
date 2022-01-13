package com.farmdiary.api.dto.exception;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class ErrorDetails {

    private final LocalDateTime timestamp;
    private final Result message;
    private final String details;

    @Builder
    public ErrorDetails(LocalDateTime timestamp, Result message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}
