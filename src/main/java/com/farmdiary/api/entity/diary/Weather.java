package com.farmdiary.api.entity.diary;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
}
