package com.farmdiary.api.dto.token;

import lombok.Getter;

@Getter
public class RefreshTokenResponse {

    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";

    public RefreshTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
