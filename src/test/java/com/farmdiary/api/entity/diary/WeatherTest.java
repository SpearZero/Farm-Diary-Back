package com.farmdiary.api.entity.diary;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


class WeatherTest {

    @ParameterizedTest(name = "{index} - input code = {0}")
    @ValueSource(strings = {"00", "01", "02", "03", "04"})
    @DisplayName("코드값이 들어왔을 때 Weather Enum 반환")
    public void input_code_then_return_enum_Weather(String code) {
        // when
        Optional<Weather> weather = Weather.weather(code);

        // then
        assertThat(weather.get()).isInstanceOf(Weather.class);
    }

    @ParameterizedTest(name = "{index} - input code = {0}(blank)")
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("코드값이 공백으로 들어왔을 때 Optional.empty 반환")
    public void input_blank_then_return_enum_Weather_ETC(String code) {
        // when
        Optional<Weather> weather = Weather.weather(code);

        // then
        assertThat(weather.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("코드값이 null로 들어왔을 때 Optional.empty 반환")
    public void input_null_then_return_enum_Weather_ETC() {
        // given
        String nullCode = null;

        // when
        Optional<Weather> weather = Weather.weather(nullCode);

        // then
        assertThat(weather.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("잘못된 코드값이 들어왔을 때 IllegalArgumentException 예외 반환")
    public void input_wrong_code_then_return_exception() {
        // given
        String wrongCode = "1234";

        // when, then
        assertThrows(IllegalArgumentException.class, () -> Weather.weather(wrongCode));
    }
}