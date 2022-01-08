package com.farmdiary.api.entity.diary;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


class WeatherTest {

    @ParameterizedTest(name = "{index} - input code = {0}")
    @ValueSource(strings = {"00", "01", "02", "03", "04"})
    @DisplayName("코드값이 들어왔을 때 Weather Enum이 반환된다.")
    public void input_code_then_return_enum_Weather(String code) {
        // when
        Weather weather = Weather.weather(code);

        // then
        assertThat(weather).isInstanceOf(Weather.class);
    }

    @ParameterizedTest(name = "{index} - input code = {0}(blank)")
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("코드값이 공백으로 들어왔을 때 Weather.ETC가 반환된다.")
    public void input_blank_then_return_enum_Weather_ETC(String code) {
        // when
        Weather weather = Weather.weather(code);

        // then
        assertThat(weather).isSameAs(Weather.ETC);
    }

    @Test
    @DisplayName("코드값이 null로 들어왔을 때 Weather.ETC가 반환된다.")
    public void input_null_then_return_enum_Weather_ETC() {
        // given
        String nullCode = null;

        // when
        Weather weather = Weather.weather(nullCode);

        // then
        assertThat(weather).isSameAs(Weather.ETC);
    }

    @Test
    @DisplayName("잘못된 코드값이 들어왔을 때 예외를 반환한다.")
    public void input_wrong_code_then_return_exception() {
        String wrongCode = "1234";

        IllegalArgumentException exception
                = assertThrows(IllegalArgumentException.class, () -> Weather.weather(wrongCode));
        String message = exception.getMessage();
        assertThat(message).isEqualTo("코드값에 대한 날씨가 존재하지 않습니다.");
    }
}