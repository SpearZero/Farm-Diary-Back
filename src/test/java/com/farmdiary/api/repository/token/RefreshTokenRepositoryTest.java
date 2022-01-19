package com.farmdiary.api.repository.token;

import com.farmdiary.api.entity.token.RefreshToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RefreshTokenRepository 테스트")
@DataJpaTest
class RefreshTokenRepositoryTest {

    @Autowired private RefreshTokenRepository refreshTokenRepository;
    private final String token = "refreshToken";

    @BeforeEach()
    void setUp() {
        RefreshToken refreshToken = RefreshToken.builder()
                        .token(token)
                        .build();

        refreshTokenRepository.save(refreshToken);
    }

    @AfterEach
    void tearDown() { refreshTokenRepository.deleteAll(); }

    @Test
    @DisplayName("refreshToken값으로 refreshToken조회시 조회 성공")
    void serach_refreshToken_then_searched() {
        // when
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(this.token);

        // then
        assertThat(refreshToken.get()).isNotNull();
    }


}