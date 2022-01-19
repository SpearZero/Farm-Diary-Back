package com.farmdiary.api.service.user;

import com.farmdiary.api.dto.user.SignUpRequest;
import com.farmdiary.api.dto.user.SignUpResponse;
import com.farmdiary.api.entity.user.GrantedRole;
import com.farmdiary.api.entity.user.Role;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.entity.user.UserRole;
import com.farmdiary.api.exception.DiaryApiException;
import com.farmdiary.api.exception.ResourceNotFoundException;
import com.farmdiary.api.repository.user.RoleRepository;
import com.farmdiary.api.repository.user.UserRepository;
import com.farmdiary.api.repository.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public SignUpResponse save(SignUpRequest signUpRequest) {

        if (userRepository.existsByNickname(signUpRequest.getNickname())) {
            throw new DiaryApiException("이미 존재하는 닉네임(nickname) 입니다.");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new DiaryApiException("이미 존재하는 이메일(email) 입니다.");
        }

        User user = User.builder().nickName(signUpRequest.getNickname())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        Role role = roleRepository.findByName(GrantedRole.ROLE_USER).orElseThrow(() -> new ResourceNotFoundException("ROLE", "USER"));

        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(role)
                .build();

        userRoleRepository.save(userRole);

        return new SignUpResponse(savedUser.getId());
    }
}
