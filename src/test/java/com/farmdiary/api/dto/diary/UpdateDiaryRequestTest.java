package com.farmdiary.api.dto.diary;

import com.farmdiary.api.dto.diary.update.UpdateDiaryRequest;
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

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UpdateDiaryRequest 테스트")
class UpdateDiaryRequestTest {

    static Validator validator;
    
    final String title = "title";
    final LocalDate workDay = LocalDate.of(2022, 01, 22);
    final String field = "field";
    final String crop = "crop";
    final BigDecimal temperature = BigDecimal.valueOf(12.23);
    final String weather = Weather.SUNNY.getCode();
    final Integer precipitation = 0;
    final String workDetail = "workDetail";

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static Stream<String> emptyValue() {
        return Stream.of("", " ", "  ");
    }

    @ParameterizedTest(name = "{index} - input title = {0}(blank)")
    @MethodSource("emptyValue")
    @DisplayName("영농일지 제목에 공백이 들어올 경우 검증 실패")
    void UpdateDiaryRequest_title_empty_then_UpdateDiaryRequest_title_fail(String title) {
        // case
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        Assertions.assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> invalidTitle() {
        return Stream.of("오십오십오오십오십오오십오십오오십오십오오십오십오오십오십오오십오십오오십오십오오십오십오오십오십오!");
    }
    
    @ParameterizedTest(name = "{index} - input title = {0}")
    @MethodSource("invalidTitle")
    @DisplayName("영농일지 제목이 제한길이 초과시 검증 실패")
    void UpdateDiaryRequest_title_over_length_then_UpdateDiaryRequest_title_fail(String title) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> validTitle() {
        return Stream.of("유효한제목", "안녕하세요 작업입니다.", "제목이에요.");
    }

    @ParameterizedTest(name = "{index} - input title = {0}")
    @MethodSource("validTitle")
    @DisplayName("영농일지 제목이 유효한 경우 검증 성공")
    void UpdateDiaryRequest_title_valid_then_UpdateDiaryRequest_title_success(String title) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
    
    @Test
    @DisplayName("영농일지 제목이 null일 경우 검증 성공")
    void UpdateDiaryRequest_title_null_then_UpdateDiaryRequest_title_success() {
        // given
        String title = null;
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("영농일지 작업날짜에 null이 들어올 경우 검증 성공")
    void UpdateDiaryRequest_workDay_blank_then_UpdateDiaryRequest_workDay_success() {
        // given
        LocalDate workDay = null;
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    static Stream<LocalDate> validWorkDay() {
        return Stream.of(LocalDate.of(2019,05,05),
                LocalDate.of(2020,1,15),
                LocalDate.of(2022,01,21));
    }

    @ParameterizedTest(name = "{index} - input workDay = {0}")
    @MethodSource("validWorkDay")
    @DisplayName("영농일지 작업날짜가 유효한 경우 검증 성공")
    void UpdateDiaryRequest_workDay_valid_then_UpdateDiaryRequest_workDay_success(LocalDate workDay) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @ParameterizedTest(name = "{index} - input title = {0}(blank)")
    @MethodSource("emptyValue")
    @DisplayName("영농일지 필지에 공백이 들어올 경우 검증 실패")
    void UpdateDiaryRequest_field_empty_then_UpdateDiaryRequest_field_fail(String field) {
        // case
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        Assertions.assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> invalidField() {
        return Stream.of("제한길이초과입니다제한길이초과입니다제한길이초과입니다제한길이초과입니다제한" +
                "길이초과입니다제한길이초과입니다제한길이초과입니다제한길이초과입니다");
    }

    @ParameterizedTest(name = "{index} - input field = {0}")
    @MethodSource("invalidField")
    @DisplayName("영농일지 필지가 제한길이 초과시 검증 실패")
    void UpdateDiaryRequest_field_over_length_then_UpdateDiaryRequest_field_fail(String field) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> validField() {
        return Stream.of("1번지", "115-1번지", "3번지");
    }

    @ParameterizedTest(name = "{index} - input field = {0}")
    @MethodSource("validField")
    @DisplayName("영농일지 필지가 유효한 경우 검증 성공")
    void UpdateDiaryRequest_field_valid_then_UpdateDiaryRequest_field_success(String field) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("영농일지 필지가 null일 경우 검증 성공")
    void UpdateDiaryRequest_field_null_then_UpdateDiaryRequest_field_success() {
        // given
        String field = null;
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
    
    @ParameterizedTest(name = "{index} - input crop = {0}(blank)")
    @MethodSource("emptyValue")
    @DisplayName("영농일지 작목에 공백이 들어올 경우 검증 실패")
    void UpdateDiaryRequest_crop_empty_then_UpdateDiaryRequest_crop_fail(String crop) {
        // case
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        Assertions.assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> invalidCrop() {
        return Stream.of("작물작물작물작물작물작물작물작물작물작물!");
    }

    @ParameterizedTest(name = "{index} - input crop = {0}")
    @MethodSource("invalidCrop")
    @DisplayName("영농일지 작목 제한길이 초과시 검증 실패")
    void UpdateDiaryRequest_crop_over_length_then_UpdateDiaryRequest_crop_fail(String crop) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> validCrop() {
        return Stream.of("고구마", "사과", "콩");
    }

    @ParameterizedTest(name = "{index} - input crop = {0}")
    @MethodSource("validCrop")
    @DisplayName("영농일지 작목이 유효한 경우 검증 성공")
    void UpdateDiaryRequest_crop_valid_then_UpdateDiaryRequest_crop_success(String crop) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("영농일지 작목이 null일 경우 검증 성공")
    void UpdateDiaryRequest_crop_null_then_UpdateDiaryRequest_crop_success() {
        // given
        String crop = null;
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    static Stream<BigDecimal> invalidTemperature() {
        return Stream.of(BigDecimal.valueOf(111.11), BigDecimal.valueOf(11.123),
                BigDecimal.valueOf(111), BigDecimal.valueOf(0.123));
    }

    @ParameterizedTest(name = "{index} - input temperature = {0}")
    @MethodSource("invalidTemperature")
    @DisplayName("영농일지 기온이 유효하지 않은 경우 검증 실패")
    void UpdateDiaryRequest_temperature_invalid_then_UpdateDiaryRequest_temperature_fail(BigDecimal temperature) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<BigDecimal> validTemperature() {
        return Stream.of(BigDecimal.valueOf(00.00), BigDecimal.valueOf(99.99),
                BigDecimal.valueOf(23.42), BigDecimal.valueOf(19));
    }

    @ParameterizedTest(name = "{index} - input temperature = {0}")
    @MethodSource("validTemperature")
    @DisplayName("영농일지 기온이 유효한 경우 검증 성공")
    void UpdateDiaryRequest_temperature_valid_then_UpdateDiaryRequest_temperature_success(BigDecimal temperature) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("영농일지 기온이 null일 경우 검증 성공")
    void UpdateDiaryRequest_temperature_null_then_UpdateDiaryRequest_temperature_success() {
        // given
        BigDecimal temperature = null;
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    static Stream<String> invalidWeatherCode() {
        return Stream.of("W05", "W06", "W0");
    }

    @ParameterizedTest(name = "{index} - input weather = {0}")
    @MethodSource("invalidWeatherCode")
    @DisplayName("영농일지 날씨에 유효하지 않은 코드값이 들어올 경우 검증 실패")
    void UpdateDiaryRequest_weather_invalid_then_UpdateDiaryRequest_weather_fail(String weather) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

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
    void UpdateDiaryRequest_weather_valid_then_UpdateDiaryRequest_weather_fail(String weather) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("영농일지 날씨에 null값이 들어올 경우 검증 성공")
    void UpdateDiaryRequest_weather_null_then_UpdateDiaryRequest_weather_success() {
        // given
        String weather = null;
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    static Stream<Integer> invalidPrecipitation() {
        return Stream.of(-100, -52, -10);
    }

    @ParameterizedTest(name = "{index} - input precipitation = {0}")
    @MethodSource("invalidPrecipitation")
    @DisplayName("영농일지 강수량에 유효하지않은 값이 들어올 경우 검증 실패")
    void UpdateDiaryRequest_precipitation_invalid_then_UpdateDiaryRequest_precipitation_fail(Integer precipitation) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<Integer> validPrecipitation() {
        return Stream.of(100, 52, 10);
    }

    @ParameterizedTest(name = "{index} - input precipitation = {0}")
    @MethodSource("validPrecipitation")
    @DisplayName("영농일지 강수량에 유효한 값이 들어올 경우 검증 성공")
    void UpdateDiaryRequest_precipitation_valid_then_UpdateDiaryRequest_precipitation_success(Integer precipitation) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
    
    @Test
    @DisplayName("영농일지 강수량에 null값이 들어올 경우 검증 성공")
    void UpdateDiaryRequest_precipitation_null_then_UpdateDiaryRequest_precipitation_success() {
        // given
        Integer precipitation = null;
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @ParameterizedTest(name = "{index} - input workDetail = {0}(blank)")
    @MethodSource("emptyValue")
    @DisplayName("영농일지 작업내용이 공백이 들어올 경우 검증 실패")
    void UpdateDiaryRequest_work_detail_blank_then_UpdateDiaryRequest_work_detail_fail(String workDetail) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }


    static Stream<String> validWorkDetail() {
        return Stream.of("밭에 물주기", "밭에 거름뿌리기", "씨앗심기");
    }

    @ParameterizedTest(name = "{index} - input workDetail = {0}")
    @MethodSource("validWorkDetail")
    @DisplayName("영농일지 작업내용이 유효한 경우 검증 성공")
    void UpdateDiaryRequest_work_detail_valid_then_UpdateDiaryRequest_work_detail_success(String workDetail) {
        // given
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("영농일지 작업내용이 null값이 들어올 경우 검증 성공")
    void UpdateDiaryRequest_work_detail_null_then_UpdateDiaryRequest_work_detail_success() {
        // given
        String workDetail = null;
        UpdateDiaryRequest updateDiaryRequest = new UpdateDiaryRequest(title, workDay, field, crop,
                temperature, weather, precipitation, workDetail);

        // when
        Set<ConstraintViolation<UpdateDiaryRequest>> violations = validator.validate(updateDiaryRequest);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
}
