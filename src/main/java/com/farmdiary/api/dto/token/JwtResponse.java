package com.farmdiary.api.dto.token;

import lombok.Getter;

@Getter
public class JwtResponse {

    private String access_token;
    private String refresh_token;
    private String type = "Bearer";
    private Long id;
    private String email;

    public JwtResponse(String accessToken, String refreshToken, Long id, String email) {
        this.access_token = accessToken;
        this.refresh_token = refreshToken;
        this.id = id;
        this.email = email;
    }
}
