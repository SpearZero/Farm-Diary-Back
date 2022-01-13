package com.farmdiary.api.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s : %s 을(를) 찾을 수 없습니다.", fieldName, fieldValue));
    }
}
