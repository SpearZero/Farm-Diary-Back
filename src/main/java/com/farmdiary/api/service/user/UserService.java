package com.farmdiary.api.service.user;

import com.farmdiary.api.dto.user.SignUpRequest;
import com.farmdiary.api.dto.user.SignUpResponse;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.DiaryApiException;
import com.farmdiary.api.repository.diary.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public SignUpResponse save(SignUpRequest signUpRequest) {

        userRepository.findByNickname(signUpRequest.getNickname()).ifPresent(s -> {
            throw new DiaryApiException(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임(nickname) 입니다.");
        });

        userRepository.findByEmail(signUpRequest.getEmail()).ifPresent(s -> {
            throw new DiaryApiException(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일(email) 입니다.");
        });

        User user = new User(signUpRequest.getNickname(), signUpRequest.getEmail(), signUpRequest.getPassword());

        User savedUser = userRepository.save(user);

        return new SignUpResponse(savedUser);
    }
}
