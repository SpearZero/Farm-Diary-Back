package com.farmdiary.api.entity.diary;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Weather {

    SUNNY("맑음", "00"),
    CLOUDY("흐림", "01"),
    RAINY("비", "02"),
    SNOWY("눈", "03"),
    ETC("기타", "04");

    private String viewName;
    private String code;

    static Optional<Weather> weather(String code) {
        if (null == code || code.isBlank()) return Optional.empty();

        for (Weather weather : values()) {
            if (code.equals(weather.getCode())) {
                return Optional.of(weather);
            }
        }

        throw new IllegalArgumentException("코드값에 대한 날씨가 존재하지 않습니다.");
    }
}