package com.farmdiary.api.entity.diary;

import com.farmdiary.api.entity.BaseEnum;
import com.farmdiary.api.exception.DiaryApiException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@AllArgsConstructor
public enum Weather implements BaseEnum {

    SUNNY("맑음", "W00"),
    CLOUDY("흐림", "W01"),
    RAINY("비", "W02"),
    SNOWY("눈", "W03"),
    ETC("기타", "W04");

    private String viewName;
    private String code;

    static Optional<Weather> weather(String code) {
        if (null == code || code.isBlank()) return Optional.empty();

        for (Weather weather : values()) {
            if (code.equals(weather.getCode())) {
                return Optional.of(weather);
            }
        }

        throw new DiaryApiException("코드값에 대한 날씨가 존재하지 않습니다.");
    }

    public String getViewName() {
        return viewName;
    }

    @Override
    public String getCode() {
        return code;
    }
}