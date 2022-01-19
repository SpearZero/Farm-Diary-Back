package com.farmdiary.api.repository.user;

import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@DisplayName("UserRepository 테스트")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final String nickname = "nickname";
    private final String email = "email@email.com";
    private final String password = "password";

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .nickName(nickname)
                .email(email)
                .password(password)
                .build();

        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
    
    @Test
    @DisplayName("회원의 닉네임으로 회원 조회시 조회 성공")
    void search_user_nickname_then_searched() {
        // given
        String searchNickName = nickname;

        // when
        Boolean result = userRepository.existsByNickname(searchNickName);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("회원의 이메일로 회원 조회시 조회 성공")
    void search_user_email_then_searched() {
        // givven
        String searchEmail = email;

        // when
        Boolean result = userRepository.existsByEmail(searchEmail);

        // theb
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("회원의 이메일로 회원 존재시 회원 반환")
    void search_user_email_then_return_email() {
        // given
        String email = "email@email.com";

        // when
        Optional<User> user = userRepository.findByEmail(email);

        // then
        assertThat(user.get()).isNotNull();
    }
}