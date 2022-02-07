package com.farmdiary.api.service.diary;

import com.farmdiary.api.dto.diary.create.CreateDiaryRequest;
import com.farmdiary.api.dto.diary.create.CreateDiaryResponse;
import com.farmdiary.api.dto.diary.delete.DeleteDiaryResponse;
import com.farmdiary.api.dto.diary.get.GetDiaryResponse;
import com.farmdiary.api.dto.diary.getList.GetDiariesRequest;
import com.farmdiary.api.dto.diary.getList.GetDiariesResponse;
import com.farmdiary.api.dto.diary.update.UpdateDiaryRequest;
import com.farmdiary.api.dto.diary.update.UpdateDiaryResponse;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.DiaryApiException;
import com.farmdiary.api.exception.ResourceNotFoundException;
import com.farmdiary.api.repository.diary.DiaryRepository;
import com.farmdiary.api.repository.user.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("DiaryService 테스트")
@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @InjectMocks DiaryService diaryService;
    @Mock DiaryRepository diaryRepository;
    @Mock UserRepository userRepository;

    // User 정보
    final Long userId = 1L;
    final String email = "email@email.com";
    final String nickname = "nickName";
    final String password = "passW0rd1!";

    final Long otherUserId = 2L;
    final String otherEmail = "email2@email2.com";
    final String otherNickname = "otherNick";
    final String otherPassword = "passW0Rd1!";

    final Long notExistsUserId = 3L;

    // Diary 정보
    final Long diaryId = 1L;
    final String title = "title";
    final LocalDate workDay = LocalDate.of(2022, 1, 22);
    final String field = "field";
    final String crop = "crop";
    final Double temperature = 12.45;
    final Weather weather = Weather.SUNNY;
    final Integer precipitation = 100;
    final String workDetail = "workDetail";

    final Long notExistsDiaryId = 2L;
    
    // Diary 엔티티와 타입이 다른 CreateDiaryRequest 정보
    final BigDecimal requestTemperature = BigDecimal.valueOf(temperature);
    final String requestWeather = Weather.SUNNY.getCode();

    User user;
    User otherUser;
    Diary diary;

    // diary리스트
    List<Diary> diaries;
    Pageable page;
    final int pageNo = 0;
    final int pageSize = 5;

    @BeforeEach
    void setUp() {
        user = User.builder().email(email).nickName(nickname).password(password).build();
        ReflectionTestUtils.setField(user, "id", userId);

        otherUser = User.builder().email(otherEmail).nickName(otherEmail).password(otherPassword).build();
        ReflectionTestUtils.setField(otherUser, "id", otherUserId);

        diary = Diary.builder().title(title).workDay(workDay).field(field).crop(crop).temperature(temperature)
                .weather(weather).precipitation(precipitation).workDetail(workDetail).build();
        ReflectionTestUtils.setField(diary, "id", diaryId);

        diary.setUser(user);
    }

    @AfterEach
    void tearDown() {
        user = null;
        otherUser = null;
        diary = null;
        diaries = null;
    }

    @Test
    @DisplayName("사용자가 영농일지 저장시 존재하지 않는 사용자면 ResourceNotFoundException 반환")
    void save_diary_user_not_exists_then_throw_ResourceNotFoundExeption() {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop,
                requestTemperature, requestWeather, precipitation, workDetail);

        // when
        when(userRepository.findById(notExistsUserId)).thenThrow(new ResourceNotFoundException("사용자", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> diaryService.save(notExistsUserId, request));
    }

    @Test
    @DisplayName("사용자가 영농일지 저장시 영농일지 저장 성공")
    void save_diary_then_save_success() {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop,
                requestTemperature, requestWeather, precipitation, workDetail);

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(diaryRepository.save(any(Diary.class))).thenReturn(diary);
        CreateDiaryResponse response = diaryService.save(userId, request);

        // then
        assertThat(response.getDiary_id()).isEqualTo(diary.getId());
    }
    
    @Test
    @DisplayName("사용자가 영농일지 수정시 존재하지 않는 영농일지면 ResourceNotFoundException 반환")
    void update_diary_diary_not_exists_then_throw_ResoureceNotFoundException() {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                requestTemperature, requestWeather, precipitation, workDetail);

        // when
        when(diaryRepository.findDiaryAndUserById(notExistsDiaryId)).thenThrow(new ResourceNotFoundException("영농일지", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> diaryService.update(userId, notExistsDiaryId, updateDiaryRequest));
    }
    
    @Test
    @DisplayName("사용자가 영농일지 수정시 다른 사용자의 영농일지를 수정하는 경우 DiaryApiException 반환")
    void update_diary_other_user_diary_then_throw_DiaryApiException() {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                requestTemperature, requestWeather, precipitation, workDetail);

        // when
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenReturn(Optional.of(diary));

        // then
        Assertions.assertThrows(DiaryApiException.class,
                () -> diaryService.update(otherUserId, diaryId, updateDiaryRequest));
    }
    
    @Test
    @DisplayName("사용자가 영농일지 수정시 기온에 null 값을 전달하더라도 영농일지 수정 성공")
    void update_diary_temperature_null_then_update_success() {
        // given
        BigDecimal nullTemperature = null;
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                nullTemperature, requestWeather, precipitation, workDetail);

        // when
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenReturn(Optional.of(diary));
        UpdateDiaryResponse updateDiaryResponse = diaryService.update(userId, diaryId, updateDiaryRequest);

        // then
        assertThat(updateDiaryResponse.getDiary_id()).isEqualTo(diaryId);
    }
    
    @Test
    @DisplayName("사용자가 영농일지 수정시 날씨에 null 값을 전달하더라도 영농일지 수정 성공")
    void update_diary_weather_null_then_update_success() {
        // given
        String nullWeather = null;
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                requestTemperature, nullWeather, precipitation, workDetail);

        // when
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenReturn(Optional.of(diary));
        UpdateDiaryResponse updateDiaryResponse = diaryService.update(userId, diaryId, updateDiaryRequest);

        // then
        assertThat(updateDiaryResponse.getDiary_id()).isEqualTo(diaryId);
    }
    
    @Test
    @DisplayName("사용자가 영농일지 수정시 영농일지 수정 성공")
    void update_diary_then_update_success() {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                requestTemperature, requestWeather, precipitation, workDetail);

        // when
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenReturn(Optional.of(diary));
        UpdateDiaryResponse updateDiaryResponse = diaryService.update(userId, diaryId, updateDiaryRequest);

        // then
        assertThat(updateDiaryResponse.getDiary_id()).isEqualTo(diaryId);
    }

    @Test
    @DisplayName("사용자가 영농일지 삭제시 존재하지 않는 영농일지면 ResourceNotFoundException 반환")
    void delete_diary_diary_not_exists_then_throw_ResoureceNotFoundException() {
        // when
        when(diaryRepository.findDiaryAndUserById(notExistsDiaryId)).thenThrow(new ResourceNotFoundException("영농일지", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> diaryService.delete(userId, notExistsDiaryId));
    }

    @Test
    @DisplayName("사용자가 영농일지 삭제시 다른 사용자의 영농일지를 삭제하는 경우 DiaryApiException 반환")
    void delete_diary_other_user_diary_then_throw_DiaryApiException() {
        // when
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenReturn(Optional.of(diary));

        // then
        Assertions.assertThrows(DiaryApiException.class, () -> diaryService.delete(otherUserId, diaryId));
    }

    @Test
    @DisplayName("사용자가 영농일지 삭제시 영농일지 삭제 성공")
    void delete_diary_then_delete_success() {
        // when
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenReturn(Optional.of(diary));
        DeleteDiaryResponse updateDiaryResponse = diaryService.delete(userId, diaryId);

        // then
        assertThat(updateDiaryResponse.getDiary_id()).isEqualTo(diaryId);
    }
    
    @Test
    @DisplayName("사용자가 영농일지 조회시 존재하지 않는 영농일지면 ResourceNotFoundException 반환")
    void get_diary_diary_not_exists_then_throw_ResoureceNotFoundException() {
        // when
        when(diaryRepository.findDiaryAndUserById(notExistsDiaryId)).thenThrow(new ResourceNotFoundException("영농일지", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> diaryService.get(notExistsDiaryId));
    }
    
    @Test
    @DisplayName("사용자가 영농일지 조회시 영농일지 조회 성공")
    void get_diary_then_get_success() {
        // when
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenReturn(Optional.of(diary));
        GetDiaryResponse getDiaryResponse = diaryService.get(diaryId);
        GetDiaryResponse.DiaryUserDto userResponse = getDiaryResponse.getUser();
        GetDiaryResponse.DiaryDto diaryResponse = getDiaryResponse.getDiary();

        // then
        // user
        assertThat(userResponse.getUser_id()).isEqualTo(userId);
        assertThat(userResponse.getEmail()).isEqualTo(email);
        assertThat(userResponse.getNickname()).isEqualTo(nickname);

        // diary
        assertThat(diaryResponse.getDiary_id()).isEqualTo(diaryId);
        assertThat(diaryResponse.getTitle()).isEqualTo(title);
        assertThat(diaryResponse.getWork_day()).isEqualTo(workDay);
        assertThat(diaryResponse.getField()).isEqualTo(field);
        assertThat(diaryResponse.getCrop()).isEqualTo(crop);
        assertThat(diaryResponse.getTemperature()).isEqualTo(temperature);
        assertThat(diaryResponse.getWeather()).isEqualTo(weather.getViewName());
        assertThat(diaryResponse.getPrecipitation()).isEqualTo(precipitation);
        assertThat(diaryResponse.getWork_detail()).isEqualTo(workDetail);
    }

    void insertDiaries() {
        diaries = new ArrayList<>();
        for (int i = 2; i <= 11; i++) {
            Diary diary = Diary.builder().title(title + i).workDay(workDay).field(field + i).crop(crop + i)
                    .temperature(temperature).weather(weather).precipitation(precipitation).workDetail(workDetail + i)
                    .build();
            ReflectionTestUtils.setField(diary, "id", Long.valueOf(i));

            if (i % 2 == 0) {
                diary.setUser(user);
            } else {
                diary.setUser(otherUser);
            }

            diaries.add(diary);
        }
    }

    @Test
    @DisplayName("사용자가 영농일지 리스트 조회시 영농일지 리스트 반환")
    void get_diaries_then_return_diaries() {
        // given
        insertDiaries();
        page = PageRequest.of(pageNo, pageSize);
        List<Diary> searchDiaries = diaries.stream().limit(5).collect(Collectors.toList());
        Page<Diary> diaryPage = new PageImpl<>(searchDiaries, page, diaries.size());

        // when
        when(diaryRepository.searchDiary(any(GetDiariesRequest.class), any(Pageable.class))).thenReturn(diaryPage);
        GetDiariesResponse getDiariesResponse = diaryService.getDairies(pageNo, pageSize, null, null);

        // then
        assertThat(getDiariesResponse.getContent().size()).isEqualTo(5);
        assertThat(getDiariesResponse.getPage_no()).isEqualTo(0);
        assertThat(getDiariesResponse.getPage_size()).isEqualTo(5);
        assertThat(getDiariesResponse.getTotal_elements()).isEqualTo(10);
        assertThat(getDiariesResponse.getTotal_pages()).isEqualTo(2);
        assertThat(getDiariesResponse.getLast()).isEqualTo(false);
    }
}