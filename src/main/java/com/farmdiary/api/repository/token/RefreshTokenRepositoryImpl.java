package com.farmdiary.api.repository.token;

import com.farmdiary.api.entity.token.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    @Value("${farmdiary.api.redis.refreshToken.key}")
    private String refreshTokenKey;

    @Value("${farmdiary.api.redis.refreshToken.expire}")
    private Long refreshTokenTTL;

    private final RedisTemplate redisTemplate;


    public RefreshToken save(RefreshToken refreshToken) {

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshTokenKey + refreshToken.getId(), refreshToken.getToken(),
                refreshTokenTTL, TimeUnit.SECONDS);

        return refreshToken;
    }

    public Optional<RefreshToken> findById(Long id) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String refreshToken = valueOperations.get(refreshTokenKey + id);

        return (null == refreshToken) || (refreshToken.isBlank()) ?
                Optional.empty() : Optional.of(RefreshToken.builder().id(id).token(refreshToken).build());
    }
}
