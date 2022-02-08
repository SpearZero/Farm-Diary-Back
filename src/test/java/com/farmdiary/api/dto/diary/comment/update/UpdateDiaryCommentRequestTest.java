package com.farmdiary.api.dto.diary.comment.update;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UpdateDiaryCommentRequest 테스트")
class UpdateDiaryCommentRequestTest {

    static Validator validator;

    final String comment = "comment";
    // 129글자
    final String invalidComment = "updatecommentupdatecommentupdatecommentupdatecommentupdatecommentupdatecomment" +
            "updatecommentupdatecommentupdatecommentupdatecommen";

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static Stream<String> blankValue() {
        return Stream.of(null, "", " ", "  ");
    }

    @ParameterizedTest(name = "{index} = input comment = {0}")
    @MethodSource("blankValue")
    @DisplayName("영농일지 댓글내용에 null 또는 공백이 들어올 경우 검증 실패")
    void UpdateDiaryCommentRequest_diary_comment_null_or_blank_then_UpdateDiaryCommentRequest_diary_comment_fail(
            String nullOrBlank) {
        // given
        UpdateDiaryCommentRequest request = new UpdateDiaryCommentRequest(nullOrBlank);

        // when
        Set<ConstraintViolation<UpdateDiaryCommentRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("영농일지 댓글내용 제한길이 초과시 검증 실패")
    void UpdateDiaryCommentRequest_diary_comment_over_length_then_UpdateDiaryCommentRequest_diary_comment_fail(){
        // given
        UpdateDiaryCommentRequest request = new UpdateDiaryCommentRequest(invalidComment);

        // when
        Set<ConstraintViolation<UpdateDiaryCommentRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }
    
    @Test
    @DisplayName("영농일지 댓글내용이 유효한 경우 검증 성공")
    void UpdateDiaryCommentRequest_diary_comment_valid_then_UpdateDiaryCommentRequest_diary_comment_success() {
        // given
        UpdateDiaryCommentRequest request = new UpdateDiaryCommentRequest(comment);

        // when
        Set<ConstraintViolation<UpdateDiaryCommentRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
}