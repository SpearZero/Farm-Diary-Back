package com.farmdiary.api.service.user;

import com.farmdiary.api.dto.user.auth.JwtResponse;
import com.farmdiary.api.dto.user.auth.LoginRequest;
import com.farmdiary.api.dto.user.auth.SignUpRequest;
import com.farmdiary.api.dto.user.auth.SignUpResponse;
import com.farmdiary.api.entity.user.GrantedRole;
import com.farmdiary.api.entity.user.Role;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.entity.user.UserRole;
import com.farmdiary.api.exception.DiaryApiException;
import com.farmdiary.api.exception.ResourceNotFoundException;
import com.farmdiary.api.repository.user.RoleRepository;
import com.farmdiary.api.repository.user.UserRepository;
import com.farmdiary.api.repository.user.UserRoleRepository;
import com.farmdiary.api.security.jwt.JwtUtils;
import com.farmdiary.api.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional(readOnly = true)
    public JwtResponse getAccessToken(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail());
    }

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

        Role role = roleRepository.findByName(GrantedRole.USER).orElseThrow(() -> new ResourceNotFoundException("ROLE", "USER"));

        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(role)
                .build();

        userRoleRepository.save(userRole);

        return new SignUpResponse(savedUser.getId());
    }
}
