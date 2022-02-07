package com.farmdiary.api.entity.diary;

import com.farmdiary.api.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DiaryComment 테스트")
class DiaryCommentTest {

    // 영농일지 댓글 값
    final String comment = "comment";

    // 영농일지 필드 값
    final String title = "title";
    final LocalDate workDay = LocalDate.of(2022, 01, 05);
    final String field = "field";
    final String crop = "crop";
    final Double temperature = 0.0;
    final Optional<Weather> weather = Optional.of(Weather.SUNNY);
    final Integer precipitation = 0;
    final String workDetail = "workDetail";

    // 유저 필드 값
    final String nickName = "nickName";
    final String email = "email@email.com";
    final String password = "passwordD123!";

    Diary diary;
    User user;
    DiaryComment diaryComment;

    @BeforeEach
    void setUp() {
        user = User.builder().nickName(nickName).email(email).password(password).build();

        diary = Diary.builder().title(title).workDay(workDay).field(field).crop(crop).temperature(temperature)
                .weather(weather.get()).precipitation(precipitation).workDetail(workDetail).build();

        diaryComment = DiaryComment.builder().user(user).diary(diary).comment(comment).build();
    }

    static Stream<String> blankValue() {
        return Stream.of("", " ", "  ");
    }
    
    @Test
    @DisplayName("영농일지 댓글내용이 null일 경우 변경되지 않음")
    void update_diaryComment_comment_null_then_diaryComment_comment_not_changed() {
        // given
        String changedComment = null;

        // when
        diaryComment.updateComment(changedComment);

        // then
        assertThat(comment).isEqualTo(diaryComment.getComment());
    }

    @ParameterizedTest(name = "{index} - input comment = {0}(blank)")
    @MethodSource("blankValue")
    @DisplayName("영농일지 댓글내용이 공백일 경우 변경되지 않음")
    void update_diaryComment_comment_blank_then_diaryComment_comment_not_changed(String changedComment) {
        // when
        diaryComment.updateComment(changedComment);

        // then
        assertThat(comment).isEqualTo(diaryComment.getComment());
    }
    
    @Test
    @DisplayName("영농일지 댓글내용 변경 확인")
    void update_diaryComment_comment_then_diaryComment_comment_changed() {
        // given
        String changedComment = "changedComment";

        // when
        diaryComment.updateComment(changedComment);

        // then
        assertThat(changedComment).isEqualTo(diaryComment.getComment());
    }
}
