package com.farmdiary.api.dto.diary.comment.delete;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteDiaryCommentResponse {

    private Long diary_id;
    private Long comment_id;
}
