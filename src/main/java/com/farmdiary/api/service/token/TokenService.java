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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class TokenService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public JwtResponse getToken(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessToken(userDetails.getUsername());
        RefreshToken refreshToken = createRefreshToken(userDetails.getId());

        return new JwtResponse(accessToken, refreshToken.getToken(), userDetails.getId(), userDetails.getEmail());
    }

    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("사용자", "ID"));
        String generateRefreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder().id(user.getId()).token(generateRefreshToken).build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshTokenResponse getNewAccessToken(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefresh_token();

        // 잘못된 토큰일 경우 예외를 던진다.
        String email = getEmailFromRefreshToken(requestRefreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "EMAIL"));
        RefreshToken refreshToken = refreshTokenRepository.findById(user.getId())
                .orElseThrow(() -> new TokenException("리프레시 토큰을 찾을 수 없습니다. 리프레시 토큰을 재발급 받으세요."));

        String newAccesstoken = jwtUtils.generateAccessToken(user.getEmail());

        return new RefreshTokenResponse(newAccesstoken, refreshToken.getToken());
    }

    /**
     *
     * @param refreshToken
     * @return String
     *
     * jjwt 라이브러리는 토큰을 검증하는 도중에 예외를 발생시키기 때문에
     * 토큰을 검증하는 부분과 이메일을 추출하는 부분을 분리할 수 없다.
     *
     */
    private String getEmailFromRefreshToken(String refreshToken) {
        try {
            return jwtUtils.getUserNameFromJwtToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new TokenException("리프레시토큰 시간이 만료되었습니다. 리프레시 토큰을 재발급 받으세요.");
        } catch (Exception e) {
            throw new TokenException("잘못된 리프레시토큰입니다.");
        }
    }
}
