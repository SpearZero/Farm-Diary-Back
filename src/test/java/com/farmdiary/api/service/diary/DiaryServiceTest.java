package com.farmdiary.api.service.diary;

import com.farmdiary.api.dto.diary.CreateDiaryRequest;
import com.farmdiary.api.dto.diary.CreateDiaryResponse;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.User;
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
    Diary diary;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email(email)
                .nickName(nickname)
                .password(password)
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

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
    }

    @AfterEach
    void tearDown() {
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자가 영농일지 저장시 존재하지 않는 사용자면 ResourceNotFoundException 반환")
    void save_diary_user_not_exists_then_throw_ResourceNotFoundExeption() {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, requestTemperature,
                requestWeather, precipitation, workDetail);

        // when
        when(userRepository.findById(userId)).thenThrow(new ResourceNotFoundException("사용자", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> diaryService.save(userId, request));
    }

    @Test
    @DisplayName("사용자가 영농일지 저장시 영농일지 저장 성공")
    void save_diary_then_save_success() {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, requestTemperature,
                requestWeather, precipitation, workDetail);

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(diaryRepository.save(any(Diary.class))).thenReturn(diary);
        CreateDiaryResponse response = diaryService.save(userId, request);

        // then
        assertThat(response.getDiary_id()).isEqualTo(diary.getId());
    }
}