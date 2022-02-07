package com.farmdiary.api.service.diary;

import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentResponse;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.DiaryComment;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.ResourceNotFoundException;
import com.farmdiary.api.repository.diary.DiaryCommentRepository;
import com.farmdiary.api.repository.diary.DiaryRepository;
import com.farmdiary.api.repository.user.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("DiaryCommentService 테스트")
@ExtendWith(MockitoExtension.class)
class DiaryCommentServiceTest {

    @InjectMocks DiaryCommentService diaryCommentService;
    @Mock DiaryRepository diaryRepository;
    @Mock DiaryCommentRepository diaryCommentRepository;
    @Mock UserRepository userRepository;

    // User 정보
    final Long userId = 1L;
    final String email = "email@email.com";
    final String nickname = "nickName";
    final String password = "passW0rd1!";

    final Long notExistsUserId = 2L;

    // Diary 정보
    final Long diaryId = 1L;
    final String title = "title";
    final LocalDate workDay = LocalDate.of(2022, 1, 22);
    final String field = "field";
    final String crop = "crop";
    final Double temperature = 12.45;
    final Weather weather = Weather.SUNNY;
    final Integer precipitation = 100;
    final String workDetail = "workDetail";

    final Long notExistsDiaryId = 2L;

    // DiaryComment 정보
    final Long diaryCommentId = 1L;
    final String comment = "comment";

    User user;
    Diary diary;
    DiaryComment diaryComment;

    @BeforeEach
    void setUp() {
        user = User.builder().email(email).nickName(nickname).password(password).build();
        ReflectionTestUtils.setField(user, "id", userId);

        diary = Diary.builder().title(title).workDay(workDay).field(field).crop(crop).temperature(temperature)
                .weather(weather).precipitation(precipitation).workDetail(workDetail).build();
        ReflectionTestUtils.setField(diary, "id", diaryId);

        diary.setUser(user);

        diaryComment = DiaryComment.builder().user(user).diary(diary).comment(comment).build();
        ReflectionTestUtils.setField(diaryComment, "id", diaryCommentId);
    }

    @AfterEach
    void tearDown() {
        user = null;
        diary = null;
    }

    @Test
    @DisplayName("사용자가 영농일지 댓글 작성시 존재하지 않는 사용자면 ResourceNotFoundException 반환")
    void save_diary_comment_user_not_exists_then_throw_ResourceNotFoundException() {
        // given
        CreateDiaryCommentRequest request = new CreateDiaryCommentRequest(comment);

        // when
        when(userRepository.findById(notExistsUserId)).thenThrow(new ResourceNotFoundException("사용자", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> diaryCommentService.save(notExistsUserId, diaryId, request));
    }
    
    @Test
    @DisplayName("사용자가 영농일지 댓글 작성시 존재하지 않는 영농일지면 ResourceNotFoundException 반환")
    void save_diary_comment_diary_not_exists_then_throw_ResourceNotFoundException() {
        // given
        CreateDiaryCommentRequest request = new CreateDiaryCommentRequest(comment);

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(diaryRepository.findById(notExistsDiaryId)).thenThrow(new ResourceNotFoundException("영농일지", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> diaryCommentService.save(userId, notExistsDiaryId, request));
    }
    
    @Test
    @DisplayName("사용자가 영농일지 댓글 작성시 영농일지 댓글 저장 성공")
    void save_diary_comment_then_save_success() {
        // given
        CreateDiaryCommentRequest request = new CreateDiaryCommentRequest(comment);

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(diary));
        when(diaryCommentRepository.save(any(DiaryComment.class))).thenReturn(diaryComment);

        CreateDiaryCommentResponse response = diaryCommentService.save(userId, diaryId, request);

        // then
        assertThat(response.getUser_id()).isEqualTo(userId);
        assertThat(response.getDiary_id()).isEqualTo(diaryId);
        assertThat(response.getComment_id()).isEqualTo(diaryCommentId);
    }
}