package com.farmdiary.api.service.diary;

import com.farmdiary.api.dto.diary.*;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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
    
    // Diary 엔티티와 타입이 다른 CreateDiaryRequest 정보
    final BigDecimal requestTemperature = BigDecimal.valueOf(temperature);
    final String requestWeather = Weather.SUNNY.getCode();

    User user;
    User otherUser;
    Diary diary;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email(email)
                .nickName(nickname)
                .password(password)
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        otherUser = User.builder()
                .email(otherEmail)
                .nickName(otherEmail)
                .password(otherPassword)
                .build();
        ReflectionTestUtils.setField(otherUser, "id", otherUserId);

        diary = Diary.builder()
                .title(title)
                .workDay(workDay)
                .field(field)
                .crop(crop)
                .temperature(temperature)
                .weather(weather)
                .precipitation(precipitation)
                .workDetail(workDetail)
                .build();
        ReflectionTestUtils.setField(diary, "id", diaryId);

        diary.setUser(user);
    }

    @AfterEach
    void tearDown() {
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자가 영농일지 저장시 존재하지 않는 사용자면 ResourceNotFoundException 반환")
    void save_diary_user_not_exists_then_throw_ResourceNotFoundExeption() {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop,
                requestTemperature, requestWeather, precipitation, workDetail);

        // when
        when(userRepository.findById(userId)).thenThrow(new ResourceNotFoundException("사용자", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> diaryService.save(userId, request));
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
    @DisplayName("사용자가 영농일지 수정시 존재하지 않는 사용자면 ResourceNotFoundException 반환")
    void update_diary_user_not_exists_then_throw_ResourceNotFoundExeption() {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                requestTemperature, requestWeather, precipitation, workDetail);

        // when
        when(userRepository.existsById(userId)).thenThrow(new ResourceNotFoundException("사용자", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> diaryService.update(userId, diaryId, updateDiaryRequest));
    }
    
    @Test
    @DisplayName("사용자가 영농일지 수정시 존재하지 않는 영농일지면 ResourceNotFoundException 반환")
    void update_diary_diary_not_exists_then_throw_ResoureceNotFoundException() {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                requestTemperature, requestWeather, precipitation, workDetail);

        // when
        when(userRepository.existsById(userId)).thenReturn(Boolean.TRUE);
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenThrow(new ResourceNotFoundException("영농일지", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> diaryService.update(userId, diaryId, updateDiaryRequest));
    }
    
    @Test
    @DisplayName("사용자가 영농일지 수정시 다른 사용자의 영농일지를 수정하는 경우 DiaryApiException 반환")
    void update_diary_other_user_diary_then_throw_DiaryApiException() {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                requestTemperature, requestWeather, precipitation, workDetail);

        // when
        when(userRepository.existsById(otherUserId)).thenReturn(Boolean.TRUE);
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenReturn(Optional.of(diary));

        // then
        Assertions.assertThrows(DiaryApiException.class,
                () -> diaryService.update(otherUserId, diaryId, updateDiaryRequest));
    }
    
    @Test
    @DisplayName("사용자가 영농일지 수정시 기온에 null값을 전달하더라도 영농일지 수정 성공")
    void update_diary_temperature_null_then_update_success() {
        // given
        BigDecimal nullTemperature = null;
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                nullTemperature, requestWeather, precipitation, workDetail);

        // when
        when(userRepository.existsById(userId)).thenReturn(Boolean.TRUE);
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
        when(userRepository.existsById(userId)).thenReturn(Boolean.TRUE);
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenReturn(Optional.of(diary));
        UpdateDiaryResponse updateDiaryResponse = diaryService.update(userId, diaryId, updateDiaryRequest);

        // then
        assertThat(updateDiaryResponse.getDiary_id()).isEqualTo(diaryId);
    }

    @Test
    @DisplayName("사용자가 영농일지 삭제시 존재하지 않는 사용자면 ResourceNotFoundException 반환")
    void delete_diary_user_not_exists_then_throw_ResourceNotFoundExeption() {
        // when
        when(userRepository.existsById(userId)).thenThrow(new ResourceNotFoundException("사용자", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> diaryService.delete(userId, diaryId));
    }

    @Test
    @DisplayName("사용자가 영농일지 삭제시 존재하지 않는 영농일지면 ResourceNotFoundException 반환")
    void delete_diary_diary_not_exists_then_throw_ResoureceNotFoundException() {
        // when
        when(userRepository.existsById(userId)).thenReturn(Boolean.TRUE);
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenThrow(new ResourceNotFoundException("영농일지", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> diaryService.delete(userId, diaryId));
    }

    @Test
    @DisplayName("사용자가 영농일지 삭제시 다른 사용자의 영농일지를 삭제하는 경우 DiaryApiException 반환")
    void delete_diary_other_user_diary_then_throw_DiaryApiException() {
        // when
        when(userRepository.existsById(otherUserId)).thenReturn(Boolean.TRUE);
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenReturn(Optional.of(diary));

        // then
        Assertions.assertThrows(DiaryApiException.class, () -> diaryService.delete(otherUserId, diaryId));
    }

    @Test
    @DisplayName("사용자가 영농일지 삭제시 영농일지 삭제 성공")
    void delete_diary_then_update_success() {
        // when
        when(userRepository.existsById(userId)).thenReturn(Boolean.TRUE);
        when(diaryRepository.findDiaryAndUserById(diaryId)).thenReturn(Optional.of(diary));
        DeleteDiaryResponse updateDiaryResponse = diaryService.delete(userId, diaryId);

        // then
        assertThat(updateDiaryResponse.getDiary_id()).isEqualTo(diaryId);
    }
}