package com.farmdiary.api.dto.diary.getList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetDiariesRequest {

    private String title;
    private String nickname;
}
