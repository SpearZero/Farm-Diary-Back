package com.farmdiary.api.dto.token;

import lombok.Getter;

@Getter
public class JwtResponse {

    private String accesstoken;
    private String refreshtoken;
    private String type = "Bearer";
    private Long id;
    private String email;

    public JwtResponse(String accesstoken, String refreshtoken, Long id, String email) {
        this.accesstoken = accesstoken;
        this.refreshtoken = refreshtoken;
        this.id = id;
        this.email = email;
    }
}
