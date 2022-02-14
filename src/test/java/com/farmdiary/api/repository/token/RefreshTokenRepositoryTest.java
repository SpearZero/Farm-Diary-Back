package com.farmdiary.api.repository.token;

import com.farmdiary.api.entity.token.RefreshToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@DisplayName("RefreshTokenRepository 테스트")
@ExtendWith(MockitoExtension.class)
class RefreshTokenRepositoryTest {

    @Mock RedisTemplate redisTemplate;
    @Mock ValueOperations<String, String> valueOperations;
    @InjectMocks RefreshTokenRepositoryImpl refreshTokenRepository;

    final String refreshTokenKey = "refreshtoken:";
    final Long refreshTokenTTL = Long.valueOf(863820);

    final Long tokenId = 1L;
    final Long notExistsTokenId = 2L;
    final String refreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlbWFpbEBlbWFpbC5jb20iLCJpYXQiOjE2NDQ4MjAxODcsImV4c" +
            "CI6MTY0NDgyMzc4N30.UuIuMiRLV7HbNyB3Pr_fioJbBeMYECeSVwyrjA0dyiGY8MG3asm4jJYTRxJG465zRCgxIN1gUDhoBbwfGL7PnQ";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenRepository, "refreshTokenKey", refreshTokenKey);
        ReflectionTestUtils.setField(refreshTokenRepository, "refreshTokenTTL", refreshTokenTTL);
    }

    @Test
    @DisplayName("리프레시 토큰 저장시 저장 성공")
    void save_refresh_token_then_save_success() {
        // given
        RefreshToken token = new RefreshToken(tokenId, refreshToken);

        // when
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        RefreshToken save = refreshTokenRepository.save(token);

        // then
        verify(valueOperations, times(1)).set(refreshTokenKey + tokenId, refreshToken, refreshTokenTTL, TimeUnit.SECONDS);
        assertThat(save.getToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("토큰이 존재하지 않으면 Optional.empty()를 반환")
    void token_not_exists_then_return_empty(){
        // when
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(refreshTokenKey + notExistsTokenId)).thenReturn(null);
        Optional<RefreshToken> token = refreshTokenRepository.findById(notExistsTokenId);

        // then
        assertThat(token.isEmpty()).isTrue();
    }

    static Stream<String> blankValue() {
        return Stream.of("", " ", "  ");
    }

    @ParameterizedTest(name = "{index} - return token = {0}")
    @MethodSource("blankValue")
    @DisplayName("토큰이 빈 값이면 Optional.empty()를 반환")
    void token_blank_then_return_empty(String refreshToken) {
        // when
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(refreshTokenKey + tokenId)).thenReturn(refreshToken);
        Optional<RefreshToken> token = refreshTokenRepository.findById(tokenId);

        // then
        assertThat(token.isEmpty()).isTrue();
    }
    
    @Test
    @DisplayName("리프레시 토큰이 존재하면 RefreshToken 반환")
    void token_exists_then_return_RefreshToken() {
        // when
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(refreshTokenKey + tokenId)).thenReturn(refreshToken);
        Optional<RefreshToken> token = refreshTokenRepository.findById(tokenId);

        // then
        assertThat(token.get()).isNotNull();
        assertThat(token.get().getId()).isEqualTo(tokenId);
        assertThat(token.get().getToken()).isEqualTo(refreshToken);
    }
}