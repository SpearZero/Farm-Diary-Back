package com.farmdiary.api.repository.diary;

import com.farmdiary.api.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

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
    
    @Test
    @DisplayName("회원의 닉네임으로 회원 조회시 조회 성공")
    public void search_user_nickname_then_searched() {
        String searchNickName = nickname;

        Optional<User> byNickName = userRepository.findByNickname(searchNickName);

        assertThat(byNickName.get()).isNotNull();
    }

    @Test
    @DisplayName("회원의 이메일로 회원 조회시 조회 성공")
    public void search_user_email_then_searched() {
        String searchEmail = email;

        Optional<User> byEmail = userRepository.findByEmail(searchEmail);

        assertThat(byEmail.get()).isNotNull();
    }
}