package com.farmdiary.api.security.jwt;

import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtUtils 테스트")
@ExtendWith({MockitoExtension.class})
class JwtUtilsTest {

    @InjectMocks JwtUtils jwtUtils;
    @Mock Authentication authentication;

    final String email = "email@email.com";
    final String jwtSecret = "secret";
    final String invalidJwtSecret = "failSecret";
    final long jwtExpiration = 864000;
    final long jwtExpiredExpiration = 0;
    final long jwtRefreshExpiration = 8640000;
    final long jwtRefreshExpiredExpiration = 0;
    TokenType accessTokenType = TokenType.ACCESS_TOKEN;

    UserDetailsImpl userDetails;

    void setJwtTokenInfo(String jwtSecret, long jwtExpiration, long jwtRefreshExpiration) {
        User user = User.builder().email(email).build();
        userDetails = UserDetailsImpl.build(user);

        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpiration);
        ReflectionTestUtils.setField(jwtUtils, "jwtRefreshExpirationMs", jwtRefreshExpiration);
    }

    @Test
    @DisplayName("인증된 유저정보가 전달되면 accessToken 토큰 생성")
    void pass_verified_userdetails_then_create_accessToken() {
        // given
        setJwtTokenInfo(jwtSecret, jwtExpiration, jwtRefreshExpiration);

        // when
        String jwtToken = jwtUtils.generateAccessToken(userDetails.getUsername());

        // then
        assertThat(jwtToken).isNotNull();
    }

    @Test
    @DisplayName("인증된 유저정보가 전달되면 refreshToken 토큰 생성")
    void pass_verified_userdetails_then_create_refreshToken() {
        // given
        setJwtTokenInfo(jwtSecret, jwtExpiration, jwtRefreshExpiration);

        // when
        String jwtToken = jwtUtils.generateRefreshToken(email);

        // then
        assertThat(jwtToken).isNotNull();
    }

    @Test
    @DisplayName("accessToken으로부터 이메일 추출")
    void pass_accessToken_then_extract_email() {
        // given
        setJwtTokenInfo(jwtSecret, jwtExpiration, jwtRefreshExpiration);

        // when
        String jwtToken = jwtUtils.generateAccessToken(userDetails.getUsername());
        String userName = jwtUtils.getUserNameFromJwtToken(jwtToken);

        // then
        assertThat(userName).isEqualTo(email);
    }

    @Test
    @DisplayName("정상적인 accessToken 토큰이 전달될 경우 true 반환")
    void accessToken_verify_success_then_return_true() {
        // given
        setJwtTokenInfo(jwtSecret, jwtExpiration, jwtRefreshExpiration);
        String jwtToken = jwtUtils.generateAccessToken(userDetails.getUsername());

        // when
        boolean result = jwtUtils.validateJwtToken(jwtToken);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("정상적인 refreshToken 토큰이 전달될 경우 true 반환")
    void refreshToken_verify_success_then_return_true() {
        // given
        setJwtTokenInfo(jwtSecret, jwtExpiration, jwtRefreshExpiration);
        String refreshToken = jwtUtils.generateRefreshToken(email);

        // when
        boolean result = jwtUtils.validateJwtToken(refreshToken);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("accessToken의 시그니처 검증이 실패할 경우 SignatureException 반환")
    void accessToken_signature_verify_fail_then_throw_SignatureException() {
        // given
        setJwtTokenInfo(jwtSecret, jwtExpiration, jwtRefreshExpiration);
        String jwtToken = jwtUtils.generateAccessToken(userDetails.getUsername());
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", invalidJwtSecret);

        // when, then
        Assertions.assertThrows(SignatureException.class, () -> jwtUtils.validateJwtToken(jwtToken));
    }

    @Test
    @DisplayName("refreshToken의 시그니처 검증이 실패할 경우 SignatureException 반환")
    void refreshToken_signature_verify_fail_then_throw_SignatureException() {
        // given
        setJwtTokenInfo(jwtSecret, jwtExpiration, jwtRefreshExpiration);
        String jwtToken = jwtUtils.generateRefreshToken(email);
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", invalidJwtSecret);

        // when, then
        Assertions.assertThrows(SignatureException.class, () -> jwtUtils.validateJwtToken(jwtToken));
    }
    
    @Test
    @DisplayName("accessToken의 구조적인 문제가 있는경우 MalformedJwtException 반환")
    void accessToken_formed_verify_fail_then_throw_MalformedJwtException() {
        // given
        setJwtTokenInfo(jwtSecret, jwtExpiration, jwtRefreshExpiration);
        String jwtToken = "Bearer " + jwtUtils.generateAccessToken(userDetails.getUsername());

        // when, then
        Assertions.assertThrows(MalformedJwtException.class, () -> jwtUtils.validateJwtToken(jwtToken));
    }

    @Test
    @DisplayName("refreshToken의 구조적인 문제가 있는경우 MalformedJwtException 반환")
    void refreshToken_formed_verify_fail_then_throw_MalformedJwtException() {
        // given
        setJwtTokenInfo(jwtSecret, jwtExpiration, jwtRefreshExpiration);
        String refreshToken = "Bearer " + jwtUtils.generateRefreshToken(email);

        // when, then
        Assertions.assertThrows(MalformedJwtException.class, () -> jwtUtils.validateJwtToken(refreshToken));
    }
    
    @Test
    @DisplayName("accessToken의 유효기간이 만료된 경우 ExpiredJwtException 반환")
    void accessToken_expired_then_throw_ExpiredJwtException() {
        // given
        setJwtTokenInfo(jwtSecret, jwtExpiredExpiration, jwtRefreshExpiration);
        String jwtToken = jwtUtils.generateAccessToken(userDetails.getUsername());

        // when, then
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtils.validateJwtToken(jwtToken));
    }

    @Test
    @DisplayName("refreshToken의 유효기간이 만료된 경우 ExpiredJwtException 반환")
    void refreshToken_expired_then_throw_ExpiredJwtException() {
        // given
        setJwtTokenInfo(jwtSecret, jwtExpiration, jwtRefreshExpiredExpiration);
        String refreshToken = jwtUtils.generateRefreshToken(email);

        // when, then
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtils.validateJwtToken(refreshToken));
    }

    @Test
    @DisplayName("token 값으로 공백이 전달될 경우 IllegalArgumentException 반환")
    void blank_verity_fail_then_throw_IllegalArgumentException() {
        // given
        String token = "";

        // when, then
        Assertions.assertThrows(IllegalArgumentException.class, () -> jwtUtils.validateJwtToken(token));
    }
}