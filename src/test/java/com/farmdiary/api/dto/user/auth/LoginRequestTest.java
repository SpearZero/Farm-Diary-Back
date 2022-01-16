package com.farmdiary.api.dto.user.auth;

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

class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest(name = "{index} - input password = {0}(blank)")
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("패스워드에 공백이 전달되면 검증 실패")
    void loginRequest_password_blank_then_loginRequest_password_fail(String password) {
        // given
        String email = "email";
        LoginRequest request = new LoginRequest(email, password);

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    @ParameterizedTest(name = "{index} - input email = {0}(blank)")
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("이메일에 공백이 전달되면 검증 실패")
    void loginRequest_email_blank_then_loginRequest_email_fail(String email) {
        // given
        String password = "password";
        LoginRequest request = new LoginRequest(email, password);

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }
    
    @Test
    @DisplayName("정상적인 이메일과 패스워드가 전달되면 검증 성공")
    void loginRequest_email_password_normal_then_loginRequest_email_password_success() {
        // given
        String email = "email";
        String password = "password";
        LoginRequest request = new LoginRequest(email, password);

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
}