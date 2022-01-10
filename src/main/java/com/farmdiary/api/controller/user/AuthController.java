package com.farmdiary.api.controller.user;

import com.farmdiary.api.dto.user.SignUpRequest;
import com.farmdiary.api.dto.user.SignUpResponse;
import com.farmdiary.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return new ResponseEntity<>(userService.save(signUpRequest), HttpStatus.CREATED);
    }
}
