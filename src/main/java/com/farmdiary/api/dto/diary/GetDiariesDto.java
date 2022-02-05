package com.farmdiary.api.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetDiariesDto {

    // 영농일지 정보
    private Long diary_id;
    private String title;
    private LocalDateTime created_at;

    // 사용자 정보
    private Long user_id;
    private String nickname;
}
