package com.farmdiary.api.dto.token;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RefreshTokenRequest 테스트")
class RefreshTokenRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest(name = "{index} - input refreshToken = {0}(blank)")
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("리프레시토큰에 공백이 전달되면 검증 실패")
    void refreshTokenRequest_token_blank_then_refreshTokenRequest_fail(String refreshToken) {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

        // when
        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }
    
    @Test
    @DisplayName("공백없는 리프레시토큰이 전달되면 검증 성공")
    void refreshTokenRequest_token_not_blank_then_refreshTokenRequest_success() {
        // given
        String refreshToken = "notBlank";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

        // when
        Set<ConstraintViolation<String>> violations = validator.validate(refreshToken);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
}