package com.farmdiary.api.dto.user.auth;

import lombok.Getter;

@Getter
public class JwtResponse {

    private String accessToken;
    private String type = "Bearer";
    private Long id;
    private String email;

    public JwtResponse(String accessToken, Long id, String email) {
        this.accessToken = accessToken;
        this.id = id;
        this.email = email;
    }
}
