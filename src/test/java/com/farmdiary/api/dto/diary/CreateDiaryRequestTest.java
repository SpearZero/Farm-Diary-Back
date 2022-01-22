package com.farmdiary.api.dto.diary;

import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.Weather;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PostDiaryRequest 테스트")
class CreateDiaryRequestTest {

    static Validator validator;

    final String title = "title";
    final LocalDate workDay = LocalDate.of(2022, 01,21);
    final String field = "field";
    final String crop = "crop";
    final BigDecimal temperature = BigDecimal.valueOf(12.45);
    final String weather = Weather.SUNNY.getCode();
    final Integer precipitation = 100;
    final String workDetail = "workDetail";

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("toEntity 호출시 Diary Entity 반환")
    void CreateDiaryRequest_to_Entity_return_Entity() {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Diary diary = request.toEntity();

        // then
        assertThat(diary.getTitle()).isEqualTo(title);
        assertThat(diary.getWorkDay()).isEqualTo(workDay);
        assertThat(diary.getField()).isEqualTo(field);
        assertThat(diary.getCrop()).isEqualTo(crop);
        assertThat(diary.getTemperature()).isEqualTo(temperature.doubleValue());
        assertThat(diary.getWeather()).isEqualTo(Weather.weather(weather).get());
        assertThat(diary.getPrecipitation()).isEqualTo(precipitation);
        assertThat(diary.getWorkDetail()).isEqualTo(workDetail);
    }

    static Stream<String> blankValue() {
        return Stream.of(null, "", " ", "  ");
    }

    @ParameterizedTest(name = "{index} - input title = {0}")
    @MethodSource("blankValue")
    @DisplayName("영농일지 제목에 공백 또는 null이 들어올 경우 검증 실패")
    void CreateDiaryRequest_title_blank_then_CreateDiaryRequest_title_fail(String title) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> invalidTitle() {
        return Stream.of("안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요" +
                "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요!");
    }

    @ParameterizedTest(name = "{index} - input title = {0}")
    @MethodSource("invalidTitle")
    @DisplayName("영농일지 제목이 제한길이 초과시 검증 실패")
    void CreateDiaryRequest_title_over_length_then_CreateDiaryRequest_title_fail(String title) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> validTitle() {
        return Stream.of("유효한제목", "안녕하세요 작업입니다.", "제목이에요.");
    }

    @ParameterizedTest(name = "{index} - input title = {0}")
    @MethodSource("validTitle")
    @DisplayName("영농일지 제목이 유효한 경우 검증 성공")
    void CreateDiaryRequest_title_valid_then_CreateDiaryRequest_title_success(String title) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    // @NotNull은 테스트 할 수 있지만, @DateTimeFormat은 Validator로 테스트할 수 없어서 mvc로 테스트 해야 한다.
    @Test
    @DisplayName("영농일지 작업날짜에 null이 들어올 경우 검증 실패")
    void CreateDiaryRequest_workDay_blank_then_CreateDiaryRequest_workDay_fail() {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, null, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        Assertions.assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<LocalDate> validWorkDay() {
        return Stream.of(LocalDate.of(2019,05,05),
                LocalDate.of(2020,1,15),
                LocalDate.of(2022,01,21));
    }

    @ParameterizedTest(name = "{index} - input workDay = {0}")
    @MethodSource("validWorkDay")
    @DisplayName("영농일지 작업날짜가 유효한 경우 검증 성공")
    void CreateDiaryRequest_workDay_valid_then_CreateDiaryRequest_workDay_success(LocalDate workDay) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @ParameterizedTest(name = "{index} - input field = {0}")
    @MethodSource("blankValue")
    @DisplayName("영농일지 필지에 공백 또는 null이 들어올 경우 검증 실패")
    void CreateDiaryRequest_field_blank_then_CreateDiaryRequest_field_fail(String field) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> invalidField() {
        return Stream.of("작업필지작업필지작업필지작업필지작업필지작업필지작업필지작업필지작업필지작업필지작업필지작업필지작업필지작업필지작업필지작업필지작업필지!!!");
    }

    @ParameterizedTest(name = "{index} - input field = {0}")
    @MethodSource("invalidField")
    @DisplayName("영농일지 필지 제한길이 초과시 검증 실패")
    void CreateDiaryRequest_field_over_length_then_CreateDiaryRequest_field_fail(String field) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> validField() {
        return Stream.of("1번지", "10-5번지", "222-11번지");
    }

    @ParameterizedTest(name = "{index} - input field = {0}")
    @MethodSource("validField")
    @DisplayName("영농일지 필지가 유효한 경우 검증 성공")
    void CreateDiaryRequest_field_valid_then_CreateDiaryRequest_field_success(String field) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("영농일지 기온이 null인경우 검증 실패")
    void CreateDiaryRequest_temperature_null_then_CreateDiaryRequest_temperature_fail() {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, null,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<BigDecimal> invalidTemperature() {
        return Stream.of(BigDecimal.valueOf(111.11), BigDecimal.valueOf(11.123), BigDecimal.valueOf(111), BigDecimal.valueOf(0.123));
    }

    @ParameterizedTest(name = "{index} - input temperature = {0}")
    @MethodSource("invalidTemperature")
    @DisplayName("영농일지 기온이 유효하지 않은 경우 검증 실패")
    void CreateDiaryRequest_temperature_invalid_then_CreateDiaryRequest_temperature_fail(BigDecimal temperature) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<BigDecimal> validTemperature() {
        return Stream.of(BigDecimal.valueOf(00.00), BigDecimal.valueOf(99.99), BigDecimal.valueOf(23.42), BigDecimal.valueOf(19));
    }

    @ParameterizedTest(name = "{index} - input temperature = {0}")
    @MethodSource("validTemperature")
    @DisplayName("영농일지 기온이 유효한 경우 검증 성공")
    void CreateDiaryRequest_temperature_valid_then_CreateDiaryRequest_temperature_success(BigDecimal temperature) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @ParameterizedTest(name = "{index} - input crop = {0}")
    @MethodSource("blankValue")
    @DisplayName("영농일지 작목에 공백 또는 null이 들어올 경우 검증 실패")
    void CreateDiaryRequest_crop_blank_then_CreateDiaryRequest_crop_fail(String crop) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> invalidCrop() {
        return Stream.of("작목작목작목작목작목작목작목작목작목작목!");
    }

    @ParameterizedTest(name = "{index} - input crop = {0}")
    @MethodSource("invalidCrop")
    @DisplayName("영농일지 작목 제한길이 초과시 검증 실패")
    void CreateDiaryRequest_crop_over_length_then_CreateDiaryRequest_crop_fail(String crop) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> validCrop() {
        return Stream.of("딸기", "사과", "배추");
    }

    @ParameterizedTest(name = "{index} - input crop = {0}")
    @MethodSource("validCrop")
    @DisplayName("영농일지 작목이 유효한 경우 검증 성공")
    void CreateDiaryRequest_crop_valid_then_CreateDiaryRequest_crop_success(String crop) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @ParameterizedTest(name = "{index} - input weather = {0}")
    @MethodSource("blankValue")
    @DisplayName("영농일지 날씨에 공백 또는 null이 들어올 경우 검증 실패")
    void CreateDiaryRequest_weather_blank_then_CreateDiaryRequest_weather_fail(String weather) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> invalidWeatherCode() {
        return Stream.of("W05", "W06", "W0");
    }

    @ParameterizedTest(name = "{index} - input weather = {0}")
    @MethodSource("invalidWeatherCode")
    @DisplayName("영농일지 날씨에 유효하지 않은 코드값이 들어올 경우 검증 실패")
    void CreateDiaryRequest_weather_invalid_then_CreateDiaryRequest_weather_fail(String weather) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> validWeatherCode() {
        return Stream.of(Weather.SUNNY.getCode(), Weather.CLOUDY.getCode(), Weather.RAINY.getCode(),
                Weather.SNOWY.getCode(), Weather.ETC.getCode());
    }

    @ParameterizedTest(name = "{index} - input weather = {0}")
    @MethodSource("validWeatherCode")
    @DisplayName("영농일지 날씨에 유효한 코드값이 들어올 경우 검증 성공")
    void CreateDiaryRequest_weather_valid_then_CreateDiaryRequest_weather_fail(String weather) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("영농일지 강수량에 null이 들어올 경우 검증 실패")
    void CreateDiaryRequest_precipitation_null_then_CreateDiaryRequest_precipitation_fail() {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, null, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<Integer> invalidPrecipitation() {
        return Stream.of(-100, -52, -10);
    }

    @ParameterizedTest(name = "{index} - input precipitation = {0}")
    @MethodSource("invalidPrecipitation")
    @DisplayName("영농일지 강수량에 유효하지않은 값이 들어올 경우 검증 실패")
    void CreateDiaryRequest_precipitation_invalid_then_CreateDiaryRequest_precipitation_fail(Integer precipitation) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<Integer> validPrecipitation() {
        return Stream.of(100, 52, 10);
    }

    @ParameterizedTest(name = "{index} - input precipitation = {0}")
    @MethodSource("validPrecipitation")
    @DisplayName("영농일지 강수량에 유효한 값이 들어올 경우 검증 성공")
    void CreateDiaryRequest_precipitation_valid_then_CreateDiaryRequest_precipitation_success(Integer precipitation) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @ParameterizedTest(name = "{index} - input workDetail = {0}")
    @MethodSource("blankValue")
    @DisplayName("영농일지 작업내용이 공백 또는 null이 들어올 경우 검증 실패")
    void CreateDiaryRequest_work_detail_blank_then_CreateDiaryRequest_work_detail_fail(String workDetail) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }


    static Stream<String> validWorkDetail() {
        return Stream.of("밭에 물주기", "밭에 거름뿌리기", "씨앗심기");
    }

    @ParameterizedTest(name = "{index} - input workDetail = {0}")
    @MethodSource("validWorkDetail")
    @DisplayName("영농일지 작업내용이 유효한 경우 검증 성공")
    void CreateDiaryRequest_work_detail_valid_then_CreateDiaryRequest_work_detail_success(String workDetail) {
        // given
        CreateDiaryRequest request = new CreateDiaryRequest(title, workDay, field, crop, temperature,
                weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<CreateDiaryRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
}