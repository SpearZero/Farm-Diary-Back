package com.farmdiary.api.service.token;

import com.farmdiary.api.dto.token.JwtResponse;
import com.farmdiary.api.dto.token.LoginRequest;
import com.farmdiary.api.dto.token.RefreshTokenRequest;
import com.farmdiary.api.dto.token.RefreshTokenResponse;
import com.farmdiary.api.entity.token.RefreshToken;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.RefreshTokenException;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public JwtResponse getToken(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessToken(userDetails.getUsername());
        RefreshToken refreshToken = createRefreshToken(userDetails.getId());

        return new JwtResponse(accessToken, refreshToken.getToken(), userDetails.getId(), userDetails.getEmail());
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("사용자", "ID"));
        String generateRefreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(generateRefreshToken)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        return savedToken;
    }

    @Transactional(readOnly = true)
    public RefreshTokenResponse getNewAccessToken(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefresh_token();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("리프레시토큰", requestRefreshToken));

        // 잘못된 토큰일 경우 예외를 던진다.
        RefreshToken validatedRefreshToken = validateRefreshToken(refreshToken);

        User user = validatedRefreshToken.getUser();
        String newAccesstoken = jwtUtils.generateAccessToken(user.getEmail());

        return new RefreshTokenResponse(newAccesstoken, validatedRefreshToken.getToken());
    }

    /**
     *
     * @param refreshToken
     * @return refreshToken
     * @Throw RefreshTokenException
     *
     * jjwt 라이브러리는 토큰을 검증하는 도중에 예외를 발생시키기 때문에
     * 토큰을 추출하고 검증할 수 없어서 예외를 감싸서 던진다.
     *
     */
    private RefreshToken validateRefreshToken(RefreshToken refreshToken) {
        try {
            jwtUtils.validateJwtToken(refreshToken.getToken());
        } catch (ExpiredJwtException e) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenException("리프레시토큰 시간이 만료되었습니다. 리프레시 토큰을 재발급 받으세요.");
        } catch (Exception e) {
            throw new RefreshTokenException("잘못된 리프레시토큰입니다.");
        }

        return refreshToken;
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
