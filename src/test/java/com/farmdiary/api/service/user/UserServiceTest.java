package com.farmdiary.api.service.user;

import com.farmdiary.api.dto.user.SignUpRequest;
import com.farmdiary.api.dto.user.SignUpResponse;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.DiaryApiException;
import com.farmdiary.api.repository.diary.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private final String nickName = "nickName";
    private final String email = "email@email.com";
    private final String password = "password";

    @Test
    @DisplayName("회원 가입시 유저 닉네임이 중복되면 DiaryAPIException 예외 발생")
    public void user_nickname_duplicated_then_throw_DiaryApiException() {
        // given
        SignUpRequest signUpRequest = new SignUpRequest(nickName, email, password);
        User user = new User(nickName, email, password);

        when(userRepository.findByNickname(anyString())).thenReturn(Optional.of(user));

        // when, then
        assertThrows(DiaryApiException.class, () -> userService.save(signUpRequest));
    }

    @Test
    @DisplayName("회원 가입시 유저 메일이 중복되면 DiaryAPIException 예외 발생")
    public void user_email_duplicated_then_throw_DiaryApiException() {
        // given
        SignUpRequest signUpRequest = new SignUpRequest(nickName, email, password);
        User user = new User(nickName, email, password);

        when(userRepository.findByNickname(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // when, then
        assertThrows(DiaryApiException.class, () -> userService.save(signUpRequest));
    }
    
    @Test
    @DisplayName("회원 가입시 중복된 정보(닉네임, 이메일)이 없으면 가입 성공")
    public void not_have_duplicated_information_then_signup_success() {
        // given
        SignUpRequest signUpRequest = new SignUpRequest(nickName, email, password);

        User signUpUser = new User(nickName, email, password);
        Long userId = 1L;
        ReflectionTestUtils.setField(signUpUser, "id", userId);

        when(userRepository.findByNickname(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(signUpUser);

        // when
        SignUpResponse response = userService.save(signUpRequest);

        // then
        assertThat(response.getId()).isEqualTo(userId);
    }
}