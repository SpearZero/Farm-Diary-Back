package com.farmdiary.api.dto.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Result<T> {
    private final T description;
}
