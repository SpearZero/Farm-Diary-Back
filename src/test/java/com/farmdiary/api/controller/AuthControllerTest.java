package com.farmdiary.api.controller;


import com.farmdiary.api.dto.token.JwtResponse;
import com.farmdiary.api.dto.token.LoginRequest;
import com.farmdiary.api.dto.token.RefreshTokenRequest;
import com.farmdiary.api.dto.token.RefreshTokenResponse;
import com.farmdiary.api.dto.user.SignUpRequest;
import com.farmdiary.api.dto.user.SignUpResponse;
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
    private final long jwtRefreshExpirationMs = 3000000;

    private final String type = "Bearer";

    @Test
    @DisplayName("회원가입 성공시 회원가입 성공 응답 반환")
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
    @DisplayName("로그인 성공시 토큰 응답 반환")
    void login_success_then_return_response() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(email, password);
        String body = objectMapper.writeValueAsString(loginRequest);

        when(tokenService.getToken(any(LoginRequest.class))).thenReturn(
                new JwtResponse(makeJwtToken(jwtExpirationMs), makeJwtToken(jwtRefreshExpirationMs), userId, email));

        // when, then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/signin")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.access_token").isNotEmpty())
            .andExpect(jsonPath("$.refresh_token").isNotEmpty())
            .andExpect(jsonPath("$.type").value(type))
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.email").value(email));
    }

    private String makeJwtToken(long expirationMs) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    
    @Test
    @DisplayName("리프레시토큰 전달시 새로운 액세스토큰 반환")
    void refreshtoken_success_then_return_accesstoken() throws Exception {
        // given
        String generatedRefreshToken = makeJwtToken(jwtRefreshExpirationMs);
        String newAccessToken = makeJwtToken(jwtExpirationMs);
        RefreshTokenRequest request = new RefreshTokenRequest(generatedRefreshToken);
        String body = objectMapper.writeValueAsString(request);


        when(tokenService.getNewAccessToken(any(RefreshTokenRequest.class))).thenReturn(
                new RefreshTokenResponse(newAccessToken, generatedRefreshToken));

        // when, then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/accessToken")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.access_token").isNotEmpty())
            .andExpect(jsonPath("$.refresh_token").isNotEmpty())
            .andExpect(jsonPath("$.type").value(type));
    }
}