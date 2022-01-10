package com.farmdiary.api.dto.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class ErrorDetails {

    private final LocalDateTime timestamp;
    private final Result message;
    private final String details;

    @RequiredArgsConstructor
    @Getter
    public static class Result<T> {
        private final T description;
    }
}
