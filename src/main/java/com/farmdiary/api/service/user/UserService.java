package com.farmdiary.api.service.user;

import com.farmdiary.api.dto.user.SignUpRequest;
import com.farmdiary.api.dto.user.SignUpResponse;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.DiaryApiException;
import com.farmdiary.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public SignUpResponse save(SignUpRequest signUpRequest) {

        if (userRepository.existsByNickname(signUpRequest.getNickname())) {
            throw new DiaryApiException("이미 존재하는 닉네임(nickname) 입니다.");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new DiaryApiException("이미 존재하는 이메일(email) 입니다.");
        }

        User user = new User(signUpRequest.getNickname(), signUpRequest.getEmail(), signUpRequest.getPassword());

        User savedUser = userRepository.save(user);

        return new SignUpResponse(savedUser);
    }
}
