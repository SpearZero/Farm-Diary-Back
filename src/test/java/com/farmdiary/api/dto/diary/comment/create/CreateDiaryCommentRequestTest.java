package com.farmdiary.api.dto.diary.comment.create;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CreateDiaryCommentRequest 테스트")
class CreateDiaryCommentRequestTest {

    static Validator validator;

    final String comment = "comment";
    // 129글자
    final String invalidComment = "commentcommentcommentcommentcommentcommentcommentcommentcommentcomment" +
            "commentcommentcommentcommentcommentcommentcommentcommentaaa";

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("영농일지 댓글내용 제한길이 초과시 검증 실패")
    void CreateDiaryCommentRequest_diary_comment_over_length_then_CreateDiaryCommentRequest_diary_comment_fail() {
        // given
        CreateDiaryCommentRequest request = new CreateDiaryCommentRequest(invalidComment);

        // when
        Set<ConstraintViolation<CreateDiaryCommentRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }
    
    @Test
    @DisplayName("영농일지 댓글내용이 유효한 경우 검증 성공")
    void CreateDiaryCommentRequest_diary_comment_valid_then_CreateDiaryCommentRequest_diary_comment_success() {
        // given
        CreateDiaryCommentRequest request = new CreateDiaryCommentRequest(comment);

        // when
        Set<ConstraintViolation<CreateDiaryCommentRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
}