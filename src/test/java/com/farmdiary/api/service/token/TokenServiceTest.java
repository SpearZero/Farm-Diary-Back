package com.farmdiary.api.service.token;

import com.farmdiary.api.dto.token.JwtResponse;
import com.farmdiary.api.dto.token.LoginRequest;
import com.farmdiary.api.dto.token.RefreshTokenRequest;
import com.farmdiary.api.dto.token.RefreshTokenResponse;
import com.farmdiary.api.entity.token.RefreshToken;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.TokenException;
import com.farmdiary.api.exception.ResourceNotFoundException;
import com.farmdiary.api.repository.token.RefreshTokenRepository;
import com.farmdiary.api.repository.user.UserRepository;
import com.farmdiary.api.security.jwt.JwtUtils;
import com.farmdiary.api.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("TokenService 테스트")
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks TokenService tokenService;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock UserRepository userRepository;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtUtils jwtUtils;
    @Mock Authentication authentication;

    final String email = "email@email.com";
    final String password = "passW0rd1!";
    final Long userId = 1L;

    final Long notExistsUserId = 2L;

    final String jwtSecret = "testSecret";
    final long jwtExpirationMs = 300000;
    final long jwtRefreshExpirationMs = 3000000;
    final long jwtRefreshExpiredExpirationMs = -1;

    final Long tokenId = 1L;

    String makeJwtToken(long tokenExpirationsMs) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + tokenExpirationsMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @Test
    @DisplayName("로그인시 가입된 사용자의 정보가 전달되면 토큰응답 발급")
    public void valid_login_info_then_return_JwtResponse() {
        // given
        LoginRequest loginRequest = new LoginRequest(email, password);

        User user = User.builder().email(email).build();
        ReflectionTestUtils.setField(user, "id", userId);
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        ReflectionTestUtils.setField(userDetails, "id", userId);

        String jwtToken = makeJwtToken(jwtExpirationMs);
        String generatedRefreshToken = makeJwtToken(jwtRefreshExpirationMs);
        RefreshToken refreshToken = RefreshToken.builder().id(userId).token(generatedRefreshToken).build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateAccessToken(userDetails.getUsername())).thenReturn(jwtToken);
        when(jwtUtils.generateRefreshToken(userDetails.getUsername())).thenReturn(generatedRefreshToken);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // when
        JwtResponse jwtResponse = tokenService.getToken(loginRequest);

        // then
        assertThat(jwtResponse).isNotNull();
        assertThat(jwtResponse.getAccess_token()).isEqualTo(jwtToken);
        assertThat(jwtResponse.getRefresh_token()).isEqualTo(generatedRefreshToken);
        assertThat(jwtResponse.getId()).isEqualTo(userId);
        assertThat(jwtResponse.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("리프레시토큰 생성시 사용자가 존재하지 않으면 ResourceNotFoundException 반환")
    void generate_refreshToken_if_user_not_exists_then_throw_ResourceNotFoundException() {
        when(userRepository.findById(notExistsUserId)).thenThrow(new ResourceNotFoundException("사용자", "ID"));

        // when, then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> tokenService.createRefreshToken(notExistsUserId));
    }
    
    @Test
    @DisplayName("리프레시토큰 생성시 사용자가 존재한다면 리프레시토큰 생성 성공")
    void generate_refreshToken_if_user_exists() {
        // given
        User user = User.builder().email(email).build();
        ReflectionTestUtils.setField(user, "id", userId);

        String generatedRefreshToken = makeJwtToken(jwtRefreshExpirationMs);
        RefreshToken refreshToken = RefreshToken.builder().id(userId).token(generatedRefreshToken).build();
        ReflectionTestUtils.setField(refreshToken, "id", tokenId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtUtils.generateRefreshToken(user.getEmail())).thenReturn(generatedRefreshToken);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // when
        RefreshToken savedRefreshToken = tokenService.createRefreshToken(userId);

        assertThat(savedRefreshToken.getId()).isEqualTo(userId);
        assertThat(savedRefreshToken.getToken()).isEqualTo(generatedRefreshToken);
    }
    
    @Test
    @DisplayName("리프레시토큰으로 액세스토큰 재발급시 사용자가 존재하지 않으면 ResourceNotFoundException 반환")
    void not_exists_user_get_accessToken_then_throw_ResourceNotFoundException() {
        // given
        User user = User.builder().email(email).build();
        ReflectionTestUtils.setField(user, "id", userId);
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(makeJwtToken(jwtRefreshExpirationMs));

        // when
        when(jwtUtils.getUserNameFromJwtToken(any(String.class))).thenReturn(email);
        when(userRepository.findByEmail(email)).thenThrow(new ResourceNotFoundException("사용자", "EMAIL"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> tokenService.getNewAccessToken(refreshTokenRequest));
    }

    @Test
    @DisplayName("리프레시토큰으로 액세스토큰 재발급시 기존에 리프레시토큰이 존재하지 않으면 RefreshTokenException 발생")
    void not_have_refreshToken_get_accessToken_then_throw_RefreshTokenException() {
        // given
        User user = User.builder().email(email).build();
        ReflectionTestUtils.setField(user, "id", userId);
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(makeJwtToken(jwtRefreshExpirationMs));

        // when, then
        when(jwtUtils.getUserNameFromJwtToken(any(String.class))).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.findById(userId))
                .thenThrow(new TokenException("리프레시 토큰을 찾을 수 없습니다. 리프레시 토큰을 재발급 받으세요."));

        Assertions.assertThrows(TokenException.class,
                () -> tokenService.getNewAccessToken(refreshTokenRequest));
    }

    @Test
    @DisplayName("리프레시토큰으로 액세스토큰 재발급시 기존 리프레시토큰의 기간이 만료되었다면 RefreshTokenException 발생")
    void refreshToken_expired_get_accessToken_then_throw_RefreshTokenException() {
        // given
        String generatedRefreshToken = makeJwtToken(jwtRefreshExpirationMs);
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(makeJwtToken(jwtRefreshExpirationMs));

        // when, then
        when(jwtUtils.getUserNameFromJwtToken(generatedRefreshToken)).thenThrow(new ExpiredJwtException(null, null, null));
        Assertions.assertThrows(TokenException.class, () -> tokenService.getNewAccessToken(refreshTokenRequest));
    }

    @Test
    @DisplayName("리프레시토큰으로 액세스토큰 재발급시 기존 리프레시토큰에 문제가 있다면 RefreshTokenException 발생")
    void refreshToken_invalid_get_accessToken_then_throw_RefreshTokenException() {
        // given
        String generatedRefreshToken = makeJwtToken(jwtRefreshExpirationMs);
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(makeJwtToken(jwtRefreshExpirationMs));

        // when, then
        when(jwtUtils.getUserNameFromJwtToken(generatedRefreshToken)).thenThrow(new IllegalArgumentException());
        Assertions.assertThrows(TokenException.class, () -> tokenService.getNewAccessToken(refreshTokenRequest));
    }

    @Test
    @DisplayName("리프레시토큰으로 액세스토큰 재발급시 리프레시토큰에 문제가 없다면 재발급이 성공")
    void refreshtoken_valid_get_accessToken_success() {
        // given
        String existsRefreshToken = makeJwtToken(jwtRefreshExpirationMs);
        String newAccessToken = makeJwtToken(jwtExpirationMs);

        User user = User.builder().email(email).build();
        ReflectionTestUtils.setField(user, "id", userId);

        RefreshToken refreshToken = RefreshToken.builder().id(userId).token(existsRefreshToken).build();

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(makeJwtToken(jwtRefreshExpirationMs));

        when(jwtUtils.getUserNameFromJwtToken(existsRefreshToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.findById(userId)).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.generateAccessToken(email)).thenReturn(newAccessToken);

        // when
        RefreshTokenResponse refreshTokenResponse = tokenService.getNewAccessToken(refreshTokenRequest);

        // then
        assertThat(refreshTokenResponse).isNotNull();
        assertThat(refreshTokenResponse.getAccess_token()).isEqualTo(newAccessToken);
        assertThat(refreshTokenResponse.getRefresh_token()).isEqualTo(existsRefreshToken);
    }
}