package com.farmdiary.api.service.user;

import com.farmdiary.api.dto.user.auth.SignUpRequest;
import com.farmdiary.api.dto.user.auth.SignUpResponse;
import com.farmdiary.api.entity.user.GrantedRole;
import com.farmdiary.api.entity.user.Role;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.DiaryApiException;
import com.farmdiary.api.exception.ResourceNotFoundException;
import com.farmdiary.api.repository.user.RoleRepository;
import com.farmdiary.api.repository.user.UserRepository;
import com.farmdiary.api.repository.user.UserRoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks private AuthService authService;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRoleRepository userRoleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private final String nickName = "nickName";
    private final String email = "email@email.com";
    private final String password = "passW0rd1!";
    private final Long userId = 1L;

    @Test
    @DisplayName("회원 가입시 유저 닉네임이 중복되면 DiaryAPIException 예외 발생")
    void user_nickname_duplicated_then_throw_DiaryApiException() {
        // given
        SignUpRequest signUpRequest = new SignUpRequest(nickName, email, password);

        when(userRepository.existsByNickname(anyString())).thenReturn(Boolean.TRUE);

        // when, then
        assertThrows(DiaryApiException.class, () -> authService.save(signUpRequest));
    }

    @Test
    @DisplayName("회원 가입시 유저 메일이 중복되면 DiaryAPIException 예외 발생")
    void user_email_duplicated_then_throw_DiaryApiException() {
        // given
        SignUpRequest signUpRequest = new SignUpRequest(nickName, email, password);

        when(userRepository.existsByNickname(anyString())).thenReturn(Boolean.FALSE);
        when(userRepository.existsByEmail(anyString())).thenReturn(Boolean.TRUE);

        // when, then
        assertThrows(DiaryApiException.class, () -> authService.save(signUpRequest));
    }
    
    @Test
    @DisplayName("회원가입시 Role이 존재하지 않으면 ResourceNotFoundException 예외 발생")
    void role_not_exists_then_thorw_ResourceNotFoundException() {
        // given
        SignUpRequest signUpRequest = new SignUpRequest(nickName, email, password);

        when(userRepository.existsByNickname(anyString())).thenReturn(Boolean.FALSE);
        when(userRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(roleRepository.findByName(GrantedRole.ROLE_USER)).thenReturn(Optional.empty());

        // when, then
        assertThrows(ResourceNotFoundException.class, () -> authService.save(signUpRequest));
    }
    
    @Test
    @DisplayName("회원 가입시 중복된 정보(닉네임, 이메일)이 없으면 가입 성공")
    void not_have_duplicated_information_then_signup_success() {
        // given
        SignUpRequest signUpRequest = new SignUpRequest(nickName, email, password);

        User signUpUser = new User(nickName, email, password);
        ReflectionTestUtils.setField(signUpUser, "id", userId);

        Role role = Role.builder().name(GrantedRole.ROLE_USER).build();
        Long roleId = 1L;
        ReflectionTestUtils.setField(role, "id", roleId);

        when(userRepository.existsByNickname(anyString())).thenReturn(Boolean.FALSE);
        when(userRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);
        when(userRepository.save(any(User.class))).thenReturn(signUpUser);
        when(roleRepository.findByName(GrantedRole.ROLE_USER)).thenReturn(Optional.of(role));

        // when
        SignUpResponse response = authService.save(signUpRequest);

        // then
        assertThat(response.getId()).isEqualTo(userId);
    }
}