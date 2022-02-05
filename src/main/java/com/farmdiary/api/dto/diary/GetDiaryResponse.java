package com.farmdiary.api.dto.diary;

import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class GetDiaryResponse {

    // 유저 정보
    private DiaryUserDto user;
    // 영농일지 정보
    private DiaryDto diary;

    @Getter
    @AllArgsConstructor
    public class DiaryUserDto {

        private Long user_id;
        private String email;
        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    public class DiaryDto {

        private Long diary_id;
        private String title;
        private LocalDate work_day;
        private String field;
        private String crop;
        private Double temperature;
        private String weather;
        private Integer precipitation;
        private String work_detail;
    }

    @Builder
    public GetDiaryResponse(User user, Diary diary) {
        this.user = new DiaryUserDto(user.getId(), user.getEmail(), user.getNickname());
        this.diary = new DiaryDto(diary.getId(), diary.getTitle(), diary.getWorkDay(),
                diary.getField(), diary.getCrop(), diary.getTemperature(),
                diary.getWeather().getViewName(), diary.getPrecipitation(), diary.getWorkDetail());
    }
}
