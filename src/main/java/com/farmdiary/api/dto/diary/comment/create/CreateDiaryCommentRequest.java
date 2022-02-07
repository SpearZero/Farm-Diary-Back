package com.farmdiary.api.dto.diary.comment.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiaryCommentRequest {

    @NotBlank
    @Length(max = 128)
    private String comment;
}
