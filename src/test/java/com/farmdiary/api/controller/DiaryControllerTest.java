package com.farmdiary.api.controller;

import com.farmdiary.api.dto.diary.CreateDiaryRequest;
import com.farmdiary.api.dto.diary.CreateDiaryResponse;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.security.jwt.AuthEntryPointJwt;
import com.farmdiary.api.security.jwt.JwtUtils;
import com.farmdiary.api.security.service.UserDetailsImpl;
import com.farmdiary.api.service.diary.DiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DiaryController 테스트")
@WebMvcTest(DiaryController.class)
class DiaryControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean DiaryService diaryService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private AuthEntryPointJwt authEntryPointJwt;
    @MockBean UserDetailsService userDetailsService;

    // Diary 정보
    final Long diaryId = 1L;
    final String title = "title";
    final LocalDate workDay = LocalDate.of(2022, 1, 25);
    final String field = "field";
    final String crop = "crop";
    final BigDecimal temperature = BigDecimal.valueOf(22.23);
    final String weather = Weather.SUNNY.getCode();
    final Integer precipitation = 0;
    final String workDetail = "workDetail";

    // UserDetails 정보
    final Long userId = 1L;
    final String email = "email@email.com";
    final String password = "password";
    final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

    @PostConstruct
    void userSetUp() {
        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(new UserDetailsImpl(userId, email, password, authorities));
    }

    // CreateDiaryRequest 테스트시 잘못된 날짜를 테스트 할 수 없어서 mvc로 테스트한다
    static Stream<String> invalidWorkDay() {
        return Stream.of("2022", "2022-11", "2022-02-52", "2022-01-43");
    }

    @ParameterizedTest(name = "{index} - input workDay = {0}")
    @MethodSource("invalidWorkDay")
    @DisplayName("영농일지 작성시 작업일자에 유효한 값이 들어오지 않으면 실패 응답 반환")
    @WithUserDetails(value = email)
    void post_diary_workday_invalid_the_fail(String workDay) throws Exception {
        Map<String, String> map = new HashMap<>(){{
            put("title", title);
            put("work_day", workDay);
            put("field", field);
            put("crop", crop);
            put("temperature", temperature.toString());
            put("weather", weather);
            put("precipitation", precipitation.toString());
            put("work_detail", workDetail);
        }};

        String body = objectMapper.writeValueAsString(map);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/diaries")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("영농일지 작성 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void post_diary_success_then_return_post_diary_response() throws Exception {
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        String body = objectMapper.writeValueAsString(request);

        when(diaryService.save(any(Long.class), any(CreateDiaryRequest.class))).thenReturn(new CreateDiaryResponse(diaryId));

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/diaries")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diary_id").value(diaryId));
    }
}