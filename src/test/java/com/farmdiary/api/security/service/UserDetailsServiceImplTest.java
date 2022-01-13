package com.farmdiary.api.security.service;

import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("존재하지 않는 이메일로 조회시 UsernameNotFoundException 반환")
    public void search_not_exists_email_then_return_exception() {
        // given
        String email = "email@email.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when, then
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }

    @Test
    @DisplayName("존재하는 이메일로 조회시 UserDetails 반환")
    public void search_exists_email_then_return_userdetails() {
        // given
        String email = "email@email.com";
        String password = "passW0rd1!";
        String nickname = "nickname";

        User user = User.builder()
                .email(email)
                .password(password)
                .nickName(nickname)
                .build();

        Long id = 1L;
        ReflectionTestUtils.setField(user, "id", id);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // then
        assertThat(userDetails).isInstanceOf(UserDetailsImpl.class);
    }
}