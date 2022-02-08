package com.farmdiary.api.dto.diary.comment.update;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateDiaryCommentResponse {

    private Long diary_id;
    private Long comment_id;
}
