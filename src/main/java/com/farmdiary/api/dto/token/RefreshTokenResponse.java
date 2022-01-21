package com.farmdiary.api.dto.token;

import lombok.Getter;

@Getter
public class RefreshTokenResponse {

    private String access_token;
    private String refresh_token;
    private String type = "Bearer";

    public RefreshTokenResponse(String accessToken, String refreshToken) {
        this.access_token = accessToken;
        this.refresh_token = refreshToken;
    }
}
