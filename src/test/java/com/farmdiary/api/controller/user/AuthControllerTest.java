package com.farmdiary.api.controller.user;


import com.farmdiary.api.dto.user.SignUpRequest;
import com.farmdiary.api.dto.user.SignUpResponse;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AuthController 테스트")
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공시 SignUpResponse 응답 반환")
    public void signup_success_then_return_response() throws Exception {
        // given
        String nickName = "nickname";
        String email = "email@email.com";
        String password = "password0A!";

        SignUpRequest request = new SignUpRequest(nickName, email, password);
        User user = new User(nickName, email, password);
        ReflectionTestUtils.setField(user, "id", 1L);

        String body = objectMapper.writeValueAsString(request);

        when(userService.save(any(SignUpRequest.class))).thenReturn(new SignUpResponse(user));

        // when, then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
}