package com.farmdiary.api.repository.user;

import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final String nickname = "nickname";
    private final String email = "email@email.com";
    private final String password = "password";

    @BeforeEach
    public void setUp() {
        User user = User.builder()
                .nickName(nickname)
                .email(email)
                .password(password)
                .build();

        userRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }
    
    @Test
    @DisplayName("회원의 닉네임으로 회원 조회시 조회 성공")
    public void search_user_nickname_then_searched() {
        String searchNickName = nickname;

        Boolean result = userRepository.existsByNickname(searchNickName);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원의 이메일로 회원 조회시 조회 성공")
    public void search_user_email_then_searched() {
        String searchEmail = email;

        Boolean result = userRepository.existsByEmail(searchEmail);

        assertThat(result).isTrue();
    }
}