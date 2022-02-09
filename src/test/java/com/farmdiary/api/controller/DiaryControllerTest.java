package com.farmdiary.api.controller;

import com.farmdiary.api.dto.diary.create.CreateDiaryRequest;
import com.farmdiary.api.dto.diary.create.CreateDiaryResponse;
import com.farmdiary.api.dto.diary.delete.DeleteDiaryResponse;
import com.farmdiary.api.dto.diary.get.GetDiaryResponse;
import com.farmdiary.api.dto.diary.getList.GetDiariesDto;
import com.farmdiary.api.dto.diary.getList.GetDiariesResponse;
import com.farmdiary.api.dto.diary.update.UpdateDiaryRequest;
import com.farmdiary.api.dto.diary.update.UpdateDiaryResponse;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.User;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    // User 정보
    final String nickname = "nickName";

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

    @Test
    @DisplayName("영농일지 수정시 영농일지 제목만 들어올 경우 수정 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void update_diary_title_then_return_update_diary_response() throws Exception {
        // case
        Map<String, String> map = new HashMap<>(){{ put("title", title); }};
        String body = objectMapper.writeValueAsString(map);

        /// when
        when(diaryService.update(any(Long.class), any(Long.class), any(UpdateDiaryRequest.class)))
                .thenReturn(new UpdateDiaryResponse(diaryId));

        // then
        mvc.perform(MockMvcRequestBuilders.patch("/api/v1/diaries/"+diaryId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId));
    }
    
    @Test
    @DisplayName("영농일지 수정시 영농일지 작업날짜만 들어올 경우 수정 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void update_diary_workDay_then_return_update_diary_response() throws Exception {
        // case
        Map<String, String> map = new HashMap<>(){{ put("work_day", workDay.toString()); }};
        String body = objectMapper.writeValueAsString(map);

        // when
        when(diaryService.update(any(Long.class), any(Long.class), any(UpdateDiaryRequest.class)))
                .thenReturn(new UpdateDiaryResponse(diaryId));

        // then
        mvc.perform(MockMvcRequestBuilders.patch("/api/v1/diaries/"+diaryId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId));
    }

    @Test
    @DisplayName("영농일지 수정시 영농일지 필지만 들어올 경우 수정 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void update_diary_field_then_return_update_diary_response() throws Exception {
        // case
        Map<String, String> map = new HashMap<>(){{ put("field", field); }};
        String body = objectMapper.writeValueAsString(map);

        // when
        when(diaryService.update(any(Long.class), any(Long.class), any(UpdateDiaryRequest.class)))
                .thenReturn(new UpdateDiaryResponse(diaryId));

        // then
        mvc.perform(MockMvcRequestBuilders.patch("/api/v1/diaries/"+diaryId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId));
    }    
    
    @Test
    @DisplayName("영농일지 수정시 영농일지 작물만 들어올 경우 수정 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void update_diary_crop_then_return_update_diary_response() throws Exception {
        // case
        Map<String, String> map = new HashMap<>(){{ put("crop", crop); }};
        String body = objectMapper.writeValueAsString(map);

        // when
        when(diaryService.update(any(Long.class), any(Long.class), any(UpdateDiaryRequest.class)))
                .thenReturn(new UpdateDiaryResponse(diaryId));

        // then
        mvc.perform(MockMvcRequestBuilders.patch("/api/v1/diaries/"+diaryId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId));
    }

    @Test
    @DisplayName("영농일지 수정시 영농일지 기온만 들어올 경우 수정 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void update_diary_temperature_then_return_update_diary_response() throws Exception {
        // case
        Map<String, String> map = new HashMap<>(){{ put("temperature", temperature.toString()); }};
        String body = objectMapper.writeValueAsString(map);

        // when
        when(diaryService.update(any(Long.class), any(Long.class), any(UpdateDiaryRequest.class)))
                .thenReturn(new UpdateDiaryResponse(diaryId));

        // then
        mvc.perform(MockMvcRequestBuilders.patch("/api/v1/diaries/"+diaryId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId));
    }
    
    @Test
    @DisplayName("영농일지 수정시 영농일지 날씨만 들어올 경우 수정 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void update_diary_weather_then_return_update_diary_response() throws Exception {
        // case
        Map<String, String> map = new HashMap<>(){{ put("weather", weather); }};
        String body = objectMapper.writeValueAsString(map);

        // when
        when(diaryService.update(any(Long.class), any(Long.class), any(UpdateDiaryRequest.class)))
                .thenReturn(new UpdateDiaryResponse(diaryId));

        // then
        mvc.perform(MockMvcRequestBuilders.patch("/api/v1/diaries/"+diaryId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId));
    }

    @Test
    @DisplayName("영농일지 수정시 영농일지 강수량만 들어올 경우 수정 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void update_diary_precipitation_then_return_update_diary_response() throws Exception {
        // case
        Map<String, String> map = new HashMap<>(){{ put("precipitation", precipitation.toString()); }};
        String body = objectMapper.writeValueAsString(map);

        // when
        when(diaryService.update(any(Long.class), any(Long.class), any(UpdateDiaryRequest.class)))
                .thenReturn(new UpdateDiaryResponse(diaryId));

        // then
        mvc.perform(MockMvcRequestBuilders.patch("/api/v1/diaries/"+diaryId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId));
    }
    
    @Test
    @DisplayName("영농일지 수정시 영농일지 필지만 들어올 경우 수정 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void update_diary_workDetail_then_return_update_diary_response() throws Exception {
        // case
        Map<String, String> map = new HashMap<>(){{ put("work_detail", workDetail); }};
        String body = objectMapper.writeValueAsString(map);

        // when
        when(diaryService.update(any(Long.class), any(Long.class), any(UpdateDiaryRequest.class)))
                .thenReturn(new UpdateDiaryResponse(diaryId));

        // then
        mvc.perform(MockMvcRequestBuilders.patch("/api/v1/diaries/"+diaryId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId));
    }

    @Test
    @DisplayName("영농일지 삭제 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void delete_diary_success_then_return_delete_diary_response() throws Exception {
        // when
        when(diaryService.delete(userId, diaryId)).thenReturn(new DeleteDiaryResponse(diaryId));

        // then
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/diaries/" + diaryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId));
    }
    
    @Test
    @DisplayName("영농일지 조회시 조회 응답 반환")
    void get_diary_success_then_return_get_diary_response() throws Exception {
        // given
        User user = User.builder().email(email).nickName(nickname).password(password).build();
        ReflectionTestUtils.setField(user, "id", userId);

        Diary diary = Diary.builder().title(title).workDay(workDay).field(field).crop(crop)
                .temperature(temperature.doubleValue()).weather(Weather.weather(weather).get())
                .precipitation(precipitation).workDetail(workDetail).build();
        ReflectionTestUtils.setField(diary, "id", diaryId);
        diary.setUser(user);

        GetDiaryResponse getDiaryResponse = GetDiaryResponse.builder().user(user).diary(diary).build();

        // when
        when(diaryService.get(diaryId)).thenReturn(getDiaryResponse);

        // then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/diaries/"+diaryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.user_id").value(userId))
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.nickname").value(nickname))
                .andExpect(jsonPath("$.diary.diary_id").value(diaryId))
                .andExpect(jsonPath("$.diary.title").value(title))
                .andExpect(jsonPath("$.diary.work_day").value(workDay.toString()))
                .andExpect(jsonPath("$.diary.field").value(field))
                .andExpect(jsonPath("$.diary.crop").value(crop))
                .andExpect(jsonPath("$.diary.temperature").value(temperature.doubleValue()))
                .andExpect(jsonPath("$.diary.weather").value(Weather.weather(weather).get().getViewName()))
                .andExpect(jsonPath("$.diary.precipitation").value(precipitation))
                .andExpect(jsonPath("$.diary.work_detail").value(workDetail));
    }

    List<Diary> setDiaries(List<Diary> diaries) {
        User user = User.builder().email(email).nickName(nickname).password(password).build();
        ReflectionTestUtils.setField(user, "id", userId);

        for (int i = 1; i <= 5; i++) {
            Diary diary = Diary.builder().title(title + i).workDay(workDay).field(field + i).crop(crop + i)
                    .temperature(temperature.doubleValue()).weather(Weather.weather(weather).get())
                    .precipitation(precipitation).workDetail(workDetail + i).build();
            ReflectionTestUtils.setField(diary, "id", Long.valueOf(i));

            diary.setUser(user);
            diaries.add(diary);
        }

        return diaries;
    }
    
    @Test
    @DisplayName("영농일지 리스트 조회시 조회 응답 반환")
    void get_diary_list_success_then_return_get_diary_list_response() throws Exception {
        // given
        List<Diary> diaries = new ArrayList<>();
        setDiaries(diaries);
        List<GetDiariesDto> getDiaries = diaries.stream().map(diary -> new GetDiariesDto(diary.getId(), diary.getTitle(),
                diary.getCreatedAt(), diary.getUser().getId(), diary.getUser().getNickname())).collect(Collectors.toList());

        Pageable page = PageRequest.of(0, 5);
        Page<Diary> diaryPage = new PageImpl<>(diaries, page, diaries.size());

        GetDiariesResponse getDiariesResponse = new GetDiariesResponse(diaryPage.getNumber(), diaryPage.getSize(),
                getDiaries, diaryPage.getTotalElements(), diaryPage.getTotalPages(), diaryPage.isLast());

        // when
        when(diaryService.getDairies(0, 5, title, nickname)).thenReturn(getDiariesResponse);

        // then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/diaries")
                        .param("pageNo", "0")
                        .param("pageSize", "5")
                        .param("title", title)
                        .param("nickname", nickname)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_no").value(0))
                .andExpect(jsonPath("$.page_size").value(5))
                .andExpect(jsonPath("$.contents", hasSize(5)))
                .andExpect(jsonPath("$.total_elements").value(5))
                .andExpect(jsonPath("$.total_pages").value(1))
                .andExpect(jsonPath("$.last").value(true))
                .andDo(print());
    }
}