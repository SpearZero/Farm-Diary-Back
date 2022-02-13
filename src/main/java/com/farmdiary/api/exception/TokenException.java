package com.farmdiary.api.exception;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException {

    public TokenException(String message) {
        super(message);
    }
}
