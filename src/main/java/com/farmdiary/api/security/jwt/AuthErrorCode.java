package com.farmdiary.api.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorCode {

    ETC("인증에 실패했습니다.", "AE00"),
    EXPIRED("JWT 토큰 기간이 만료되었습니다.", "AE01");

    private String message;
    private String code;
}
