package com.farmdiary.api.entity.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {
    
    // 유저 필드 값
    private final String nickName = "nickName";
    private final String email = "email@email.com";
    private final String password = "password";

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .nickName(nickName)
                .email(email)
                .password(password)
                .build();
    }

    @Test
    @DisplayName("이메일 인증 확인")
    public void verify_email_then_verified() {
        // when
        user.verifyEmail();

        // then
        assertThat(user.isEmailVerified()).isTrue();
    }
}