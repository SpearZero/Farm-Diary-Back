package com.farmdiary.api.security.jwt;

import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.security.service.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private final String email = "email@email.com";
    private final String jwtSecret = "secret";
    private final String invalidJwtSecret = "failSecret";
    private final int jwtExpiration = 864000;
    private final int jwtExpiredExpiration = 0;

    private UserDetailsImpl userDetails;
    
    private void setJwtTokenInfo(String jwtSecret, int jwtExpiration) {
        User user = User.builder().email(email).build();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpiration);

        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    @DisplayName("인증된 유저정보가 전달되면 JWT 토큰 생성")
    public void pass_verified_userdetails_then_create_jwttoken() {
        setJwtTokenInfo(jwtSecret, jwtExpiration);

        // when
        String jwtToken = jwtUtils.generateJwtToken(authentication);

        // then
        assertThat(jwtToken).isNotNull();
    }

    @Test
    @DisplayName("JWT 토큰으로부터 이메일 추출")
    public void pass_token_then_return_email() {
        setJwtTokenInfo(jwtSecret, jwtExpiration);

        // when
        String jwtToken = jwtUtils.generateJwtToken(authentication);
        String userName = jwtUtils.getUserNameFromJwtToken(jwtToken);

        // then
        assertThat(userName).isEqualTo(email);
    }

    @Test
    @DisplayName("정상적인 JWT 토큰이 전달될 경우 true 반환")
    public void token_verify_success_then_return_true() {
        setJwtTokenInfo(jwtSecret, jwtExpiration);

        // when
        String jwtToken = jwtUtils.generateJwtToken(authentication);
        boolean result = jwtUtils.validateJwtToken(jwtToken);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("JWT의 시그니처 검증이 실패할 경우 false 반환")
    public void signature_verify_fail_then_return_false() {
        setJwtTokenInfo(jwtSecret, jwtExpiration);

        // when
        String jwtToken = jwtUtils.generateJwtToken(authentication);

        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", invalidJwtSecret);
        boolean result = jwtUtils.validateJwtToken(jwtToken);

        // then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("JWT의 구조적인 문제가 있는경우 false 반환")
    public void formed_verify_fail_then_return_false() {
        // when
        boolean result = jwtUtils.validateJwtToken("malformed");

        // then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("JWT의 유효기간이 만료된 경우 false 반환")
    public void jwt_expired_then_return_false() {
        setJwtTokenInfo(jwtSecret, jwtExpiredExpiration);

        // when
        String jwtToken = jwtUtils.generateJwtToken(authentication);
        boolean result = jwtUtils.validateJwtToken(jwtToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("JWT값으로 공백이 전달될 경우 false 반환")
    public void blank_verity_fail_then_return_false() {
        // given
        String token = "";

        // when
        boolean result = jwtUtils.validateJwtToken(token);

        // then
        assertThat(result).isFalse();
    }
}