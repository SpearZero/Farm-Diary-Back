package com.farmdiary.api.service.token;

import com.farmdiary.api.dto.user.auth.JwtResponse;
import com.farmdiary.api.dto.user.auth.LoginRequest;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.security.jwt.JwtUtils;
import com.farmdiary.api.security.service.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks private TokenService tokenService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtils jwtUtils;
    @Mock private Authentication authentication;

    private final String email = "email@email.com";
    private final String password = "passW0rd1!";
    private final Long userId = 1L;

    private final String jwtSecret = "testSecret";
    private final long jwtExpirationMs = 300000;

    @Test
    @DisplayName("로그인시 가입된 사용자의 정보가 전달되면 로그인 성공")
    public void valid_login_info_then_login_success() {
        // given
        LoginRequest loginRequest = new LoginRequest(email, password);
        UserDetailsImpl userDetails = UserDetailsImpl.build(User
                .builder()
                .email(email)
                .build());
        ReflectionTestUtils.setField(userDetails, "id", userId);
        String jwtToken = makeJwtToken();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateAccessToken(userDetails)).thenReturn(jwtToken);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // when
        JwtResponse accessToken = tokenService.getAccessToken(loginRequest);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(accessToken.getAccessToken()).isEqualTo(jwtToken);
        assertThat(accessToken.getId()).isEqualTo(userId);
        assertThat(accessToken.getEmail()).isEqualTo(email);
    }

    private String makeJwtToken() {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}