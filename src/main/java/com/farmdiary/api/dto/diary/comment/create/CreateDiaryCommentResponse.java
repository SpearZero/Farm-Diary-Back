package com.farmdiary.api.dto.diary.comment.create;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateDiaryCommentResponse {

    private Long diary_id;
    private Long comment_id;
}
