package com.farmdiary.api.dto.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class Result<T> {
    private final T description;

    public Result(T description) {
        this.description = description;
    }
}
