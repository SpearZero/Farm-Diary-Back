package com.farmdiary.api.controller;

import com.farmdiary.api.dto.token.JwtResponse;
import com.farmdiary.api.dto.token.LoginRequest;
import com.farmdiary.api.dto.token.RefreshTokenRequest;
import com.farmdiary.api.dto.token.RefreshTokenResponse;
import com.farmdiary.api.dto.user.SignUpRequest;
import com.farmdiary.api.dto.user.SignUpResponse;
import com.farmdiary.api.service.token.TokenService;
import com.farmdiary.api.service.user.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final TokenService tokenService;
    private final AuthService authService;

    @GetMapping("/signin")
    public ResponseEntity<JwtResponse> signIn(@Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(tokenService.getToken(loginRequest), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return new ResponseEntity<>(authService.save(signUpRequest), HttpStatus.CREATED);
    }

    @PostMapping("/accesstoken")
    public ResponseEntity<RefreshTokenResponse> getAccessToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return new ResponseEntity<>(tokenService.getNewAccessToken(refreshTokenRequest), HttpStatus.CREATED);
    }
}
