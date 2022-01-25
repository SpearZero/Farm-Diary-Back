package com.farmdiary.api.entity.diary;

import com.farmdiary.api.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Diary 테스트")
class DiaryTest {

    // 영농일지 필드 값
    final String title = "title";
    final LocalDate workDay = LocalDate.of(2022, 01, 05);
    final String field = "field";
    final String crop = "crop";
    final Double temperature = 0.0;
    final Optional<Weather> weather = Optional.of(Weather.SUNNY);
    final Integer precipitation = 0;
    final String workDetail = "workDetail";
    
    // 유저 필드 값
    final String nickName = "nickName";
    final String email = "email@email.com";
    final String password = "passwordD123!";

    Diary diary;
    User user;

    @BeforeEach
    void setUpDiary() {
        user = User.builder()
                .nickName(nickName)
                .email(email)
                .password(password)
                .build();

        diary = Diary.builder().title(title)
                .workDay(workDay)
                .field(field)
                .crop(crop)
                .temperature(temperature)
                .weather(weather.get())
                .precipitation(precipitation)
                .workDetail(workDetail)
                .build();
    }

    @Test
    @DisplayName("영농일지 제목 변경 확인")
    void update_diary_title_then_changed_diary_title() {
        // given
        String changedTitle = "changedTitle";

        // when
        diary.update(changedTitle, workDay, field, crop, weather, temperature, precipitation, workDetail);

        // then
        assertThat(title).isNotEqualTo(diary.getTitle());
        assertThat(changedTitle).isEqualTo(diary.getTitle());
    }

    @Test
    @DisplayName("영농일지 제목이 null일경우 변경되지 않음")
    void update_diary_title_null_then_diary_title_not_changed() {
        // given
        String changedTitle = null;

        // when
        diary.update(changedTitle, workDay, field, crop, weather, temperature, precipitation, workDetail);

        // then
        assertThat(title).isEqualTo(diary.getTitle());
    }

    @Test
    @DisplayName("영농일지 제목이 공백일 경우 변경되지 않음")
    void update_diary_title_blank_then_diary_title_not_changed() {
        // given
        String changedTitle = " ";

        // when
        diary.update(changedTitle, workDay, field, crop, weather, temperature, precipitation, workDetail);

        // then
        assertThat(title).isEqualTo(diary.getTitle());
    }

    @Test
    @DisplayName("영농일지 작업날짜 변경 확인")
    void update_diary_workDay_then_changed_diary_workDay() {
        // given
        LocalDate changedWorkDay = LocalDate.of(2022,01,04);

        // when
        diary.update(title, changedWorkDay, field, crop, weather, temperature, precipitation, workDetail);

        // then
        assertThat(workDay).isNotEqualTo(diary.getWorkDay());
        assertThat(changedWorkDay).isEqualTo(diary.getWorkDay());
    }
    
    @Test
    @DisplayName("영농일지 작업날짜가 null일경우 변경되지 않음")
    void update_diary_workDay_null_then_diary_workDay_not_changed() {
        // given
        LocalDate changedWorkDay = null;

        // when
        diary.update(title, changedWorkDay, field, crop, weather, temperature, precipitation, workDetail);

        // then
        assertThat(workDay).isEqualTo(diary.getWorkDay());
    }

    @Test
    @DisplayName("영농일지 작업필지 변경 확인")
    void update_diary_field_then_changed_diary_field() {
        // given
        String changedField = "changedField";

        // when
        diary.update(title, workDay, changedField, crop, weather, temperature, precipitation, workDetail);

        // then
        assertThat(field).isNotEqualTo(diary.getField());
        assertThat(changedField).isEqualTo(diary.getField());
    }

    @Test
    @DisplayName("영농일지 작업필지가 null일경우 변경되지 않음")
    void update_diary_field_null_then_diary_field_not_changed() {
        // given
        String changedField = null;

        // when
        diary.update(title, workDay, changedField, crop, weather, temperature, precipitation, workDetail);

        // then
        assertThat(field).isEqualTo(diary.getField());
    }

    @Test
    @DisplayName("영농일지 작업필지가 공백일 경우 변경되지 않음")
    void update_diary_field_blank_then_diary_field_not_changed() {
        // given
        String changedField = " ";

        // when
        diary.update(title, workDay, changedField, crop, weather, temperature, precipitation, workDetail);

        // then
        assertThat(field).isEqualTo(diary.getField());
    }

    @Test
    @DisplayName("영농일지 작목 변경 확인")
    void update_diary_crop_then_changed_diary_crop() {
        // given
        String changedCrop = "changedCrop";

        // when
        diary.update(title, workDay, field, changedCrop, weather, temperature, precipitation, workDetail);

        // then
        assertThat(crop).isNotEqualTo(diary.getCrop());
        assertThat(changedCrop).isEqualTo(diary.getCrop());
    }

    @Test
    @DisplayName("영농일지 작목이 null일경우 변경되지 않음")
    void update_diary_crop_null_then_diary_crop_not_changed() {
        // given
        String changedCrop = null;

        // when
        diary.update(title, workDay, field, changedCrop, weather, temperature, precipitation, workDetail);

        // then
        assertThat(crop).isEqualTo(diary.getCrop());
    }

    @Test
    @DisplayName("영농일지 작목이 공백일 경우 변경되지 않음")
    void update_diary_crop_blank_then_diary_crop_not_changed() {
        // given
        String changedCrop = " ";

        // when
        diary.update(title, workDay, field, changedCrop, weather, temperature, precipitation, workDetail);

        // then
        assertThat(crop).isEqualTo(diary.getCrop());
    }

    @Test
    @DisplayName("영농일지 기온 변경 확인")
    void update_diary_temperature_then_changed_diary_temperature() {
        // given
        Double changedTemperature = 0.1;

        // when
        diary.update(title, workDay, field, crop, weather, changedTemperature, precipitation, workDetail);

        // then
        assertThat(temperature).isNotEqualTo(diary.getTemperature());
        assertThat(changedTemperature).isEqualTo(diary.getTemperature());
    }

    @Test
    @DisplayName("영농일지 기온이 null일경우 변경되지 않음")
    void update_diary_temperature_null_then_diary_temperature_not_changed() {
        // given
        Double changedTemperature = null;

        // when
        diary.update(title, workDay, field, crop, weather, changedTemperature, precipitation, workDetail);

        // then
        assertThat(temperature).isEqualTo(diary.getTemperature());
    }

    @Test
    @DisplayName("영농일지 날씨 변경 확인")
    void update_diary_weather_then_changed_diary_weather() {
        // given
        Optional<Weather> changedWeather = Optional.of(Weather.RAINY);

        // when
        diary.update(title, workDay, field, crop, changedWeather, temperature, precipitation, workDetail);

        // then
        assertThat(weather.get()).isNotSameAs(diary.getWeather());
        assertThat(changedWeather.get()).isSameAs(diary.getWeather());
    }

    @Test
    @DisplayName("영농일지 날씨가 null일경우 변경되지 않음")
    void update_diary_weather_null_then_diary_weather_not_changed() {
        // given
        Optional<Weather> changedWeather = Optional.empty();

        // when
        diary.update(title, workDay, field, crop, changedWeather, temperature, precipitation, workDetail);

        // then
        assertThat(weather.get()).isSameAs(diary.getWeather());
    }
    
    @Test
    @DisplayName("영농일지 강수량 변경 확인")
    void update_diary_precipitation_then_changed_diary_precipitation() {
        // given
        Integer changedPrecipitation = 10;

        // when
        diary.update(title, workDay, field, crop, weather, temperature, changedPrecipitation, workDetail);

        // then
        assertThat(precipitation).isNotEqualTo(diary.getPrecipitation());
        assertThat(changedPrecipitation).isEqualTo(diary.getPrecipitation());
    }

    @Test
    @DisplayName("영농일지 강수량이 null일경우 변경되지 않음")
    void update_diary_precipitation_null_then_diary_precipitation_not_changed() {
        // given
        Integer changedPrecipitation = null;

        // when
        diary.update(title, workDay, field, crop, weather, temperature, changedPrecipitation, workDetail);

        // then
        assertThat(precipitation).isEqualTo(diary.getPrecipitation());
    }

    @Test
    @DisplayName("영농일지 강수량이 0미만일 경우 변경되지 않음")
    void update_diary_precipitation_minus_then_diary_precipitation_not_changed() {
        // given
        Integer changedPrecipitation = -1;

        // when
        diary.update(title, workDay, field, crop, weather, temperature, changedPrecipitation, workDetail);

        // then
        assertThat(precipitation).isEqualTo(diary.getPrecipitation());
    }

    @Test
    @DisplayName("영농일지 작업내용 변경 확인")
    void update_diary_workDetail_then_changed_diary_workDetail() {
        // given
        String changedWorkDetail = "changedWorkDetail";

        // when
        diary.update(title, workDay, field, crop, weather, temperature, precipitation, changedWorkDetail);

        // then
        assertThat(workDetail).isNotEqualTo(diary.getWorkDetail());
        assertThat(changedWorkDetail).isEqualTo(diary.getWorkDetail());
    }

    @Test
    @DisplayName("영농일지 작업내용이 null일경우 변경되지 않음")
    void update_diary_workDeatil_null_then_diary_workDetail_not_changed() {
        // given
        String changedWorkDetail = null;

        // when
        diary.update(title, workDay, field, crop, weather, temperature, precipitation, changedWorkDetail);

        // then
        assertThat(workDetail).isEqualTo(diary.getWorkDetail());
    }

    @Test
    @DisplayName("영농일지 작업내용이 공백일 경우 변경되지 않음")
    void update_diary_workDetail_blank_then_diary_workDetail_not_changed() {
        // given
        String changedWorkDetail = " ";

        // when
        diary.update(title, workDay, field, crop, weather, temperature, precipitation, changedWorkDetail);

        // then
        assertThat(workDetail).isEqualTo(diary.getWorkDetail());
    }

    @Test
    @DisplayName("유저가 null이면 연관관계 설정이 되지않음")
    void user_null_then_user_not_set() {
        // given
        User nullUser = null;

        // when
        diary.setUser(nullUser);

        // then
        assertThat(diary.getUser()).isNull();
    }

    @Test
    @DisplayName("유저가 null이 아니면 연관관계 설정")
    void user_verified_and_not_null_user_set() {
        // when
        diary.setUser(user);

        // then
        assertThat(diary.getUser()).isSameAs(user);
    }
}