package com.farmdiary.api.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchDiaryRequest {

    private String title;
    private String nickname;
}
