package com.farmdiary.api.entity.token;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshToken {

    private final Long id;
    private final String token;

    @Builder
    public RefreshToken(Long id, String token) {
        this.id = id;
        this.token = token;
    }
}