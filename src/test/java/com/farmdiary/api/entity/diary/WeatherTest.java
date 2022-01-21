package com.farmdiary.api.entity.diary;

import com.farmdiary.api.exception.DiaryApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Weather 테스트")
class WeatherTest {

    static Stream<String> validWeatherCode() {
        return Stream.of(Weather.SUNNY.getCode(), Weather.CLOUDY.getCode(), Weather.RAINY.getCode(),
                Weather.SNOWY.getCode(), Weather.ETC.getCode());
    }

    @ParameterizedTest(name = "{index} - input code = {0}")
    @MethodSource("validWeatherCode")
    @DisplayName("코드값이 들어왔을 때 Weather Enum 반환")
    void input_code_then_return_enum_Weather(String code) {
        // when
        Optional<Weather> weather = Weather.weather(code);

        // then
        assertThat(weather.get()).isInstanceOf(Weather.class);
    }

    static Stream<String> invalidWeatherCode() {
        return Stream.of(null, "", " ", "  ");
    }

    @ParameterizedTest(name = "{index} - input code = {0}(blank)")
    @MethodSource("invalidWeatherCode")
    @DisplayName("코드값이 유효하지 않을 때 Optional.empty 반환")
    void input_blank_then_return_enum_Weather_ETC(String code) {
        // when
        Optional<Weather> weather = Weather.weather(code);

        // then
        assertThat(weather.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("잘못된 코드값이 들어왔을 때 DiaryApiException 예외 반환")
    void input_wrong_code_then_return_exception() {
        // given
        String wrongCode = "1234";

        // when, then
        assertThrows(DiaryApiException.class, () -> Weather.weather(wrongCode));
    }
}