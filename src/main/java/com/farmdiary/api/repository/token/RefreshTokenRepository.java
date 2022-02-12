package com.farmdiary.api.repository.token;

import com.farmdiary.api.entity.token.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findById(Long id);
}
