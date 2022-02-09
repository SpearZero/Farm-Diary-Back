package com.farmdiary.api.dto.diary.comment.getList;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GetDiaryCommentsDto {

    // 영농일지 댓글 정보
    private Long comment_id;
    private String comment;
    private LocalDateTime created_at;
    
    // 사용자 정보
    private Long user_id;
    private String nickname;
}
