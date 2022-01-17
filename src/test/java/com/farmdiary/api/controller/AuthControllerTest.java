package com.farmdiary.api.controller;


import com.farmdiary.api.controller.AuthController;
import com.farmdiary.api.dto.user.auth.JwtResponse;
import com.farmdiary.api.dto.user.auth.LoginRequest;
import com.farmdiary.api.dto.user.auth.SignUpRequest;
import com.farmdiary.api.dto.user.auth.SignUpResponse;
import com.farmdiary.api.security.jwt.AuthEntryPointJwt;
import com.farmdiary.api.security.jwt.JwtUtils;
import com.farmdiary.api.security.service.UserDetailsServiceImpl;
import com.farmdiary.api.service.token.TokenService;
import com.farmdiary.api.service.user.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AuthController 테스트")
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthService authService;
    @MockBean private TokenService tokenService;
    @MockBean private UserDetailsServiceImpl userdetailsService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private AuthEntryPointJwt authEntryPointJwt;

    private final String nickname = "nickname";
    private final String email = "email@email.com";
    private final String password = "password0A!";
    private final Long userId = 1L;

    private final String jwtSecret = "testSecret";
    private final long jwtExpirationMs = 300000;

    private final String type = "Bearer";

    @Test
    @DisplayName("회원가입 성공시 SignUpResponse 응답 반환")
    void signup_success_then_return_response() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest(nickname, email, password);
        String body = objectMapper.writeValueAsString(request);

        when(authService.save(any(SignUpRequest.class))).thenReturn(new SignUpResponse(userId));

        // when, then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    @DisplayName("로그인 성공시 JwtResponse 응답 반환")
    void login_success_then_return_response() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(email, password);
        String body = objectMapper.writeValueAsString(loginRequest);

        when(tokenService.getAccessToken(any(LoginRequest.class))).thenReturn(new JwtResponse(makeJwtToken(), userId, email));

        // when, then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/signin")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.type").value(type))
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.email").value(email));
    }

    private String makeJwtToken() {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

}