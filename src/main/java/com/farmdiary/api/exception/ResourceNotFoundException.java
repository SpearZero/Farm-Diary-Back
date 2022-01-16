package com.farmdiary.api.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName) {
        super(String.format("%s : %s 을(를) 찾을 수 없습니다.", resourceName, fieldName));
    }
}
