package com.farmdiary.api.service.diary;

import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentResponse;
import com.farmdiary.api.dto.diary.comment.delete.DeleteDiaryCommentResponse;
import com.farmdiary.api.dto.diary.comment.update.UpdateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.update.UpdateDiaryCommentResponse;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.DiaryComment;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.DiaryApiException;
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

    final Long notExistsDiaryCommentId = 2L;

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
        assertThat(response.getDiary_id()).isEqualTo(diaryId);
        assertThat(response.getComment_id()).isEqualTo(diaryCommentId);
    }

    @Test
    @DisplayName("사용자가 영농일지 댓글 수정시 존재하지 않는 댓글이면 ResourceNotFoundException 반환")
    void update_diary_comment_diary_comment_not_exists_then_throw_ResourceNotFoundException() {
        // given
        UpdateDiaryCommentRequest request = new UpdateDiaryCommentRequest(comment);

        // when
        when(diaryCommentRepository.findDiaryCommentAndUserAndDiaryById(notExistsDiaryCommentId))
                .thenThrow(new ResourceNotFoundException("사용자", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> diaryCommentService.update(userId, diaryId, notExistsDiaryCommentId, request));
    }
    
    @Test
    @DisplayName("사용자가 영농일지 댓글 수정시 댓글 작성자와 수정자가 다르면 DiaryApiException 반환")
    void update_diary_comment_user_not_same_then_throw_DiaryApiException() {
        // given
        UpdateDiaryCommentRequest request = new UpdateDiaryCommentRequest(comment);

        // when
        when(diaryCommentRepository.findDiaryCommentAndUserAndDiaryById(diaryCommentId))
                .thenReturn(Optional.of(diaryComment));

        // then
        Assertions.assertThrows(DiaryApiException.class,
                () -> diaryCommentService.update(notExistsUserId, diaryId, diaryCommentId, request));
    }
    
    @Test
    @DisplayName("사용자가 영농일지 댓글 수정시 조회된 영농일지와 댓글이 달린 영농일지가 다르면 DiaryApiException 반환")
    void update_diary_comment_diary_not_same_then_throw_DiaryApiException() {
        // given
        UpdateDiaryCommentRequest request = new UpdateDiaryCommentRequest(comment);

        // when
        when(diaryCommentRepository.findDiaryCommentAndUserAndDiaryById(diaryCommentId))
                .thenReturn(Optional.of(diaryComment));

        // then
        Assertions.assertThrows(DiaryApiException.class,
                () -> diaryCommentService.update(userId, notExistsDiaryId, diaryCommentId, request));
    }
    
    @Test
    @DisplayName("사용자가 영농일지 댓글 수정시 댓글 수정 성공")
    void update_diary_comment_then_update_success() {
        // given
        UpdateDiaryCommentRequest request = new UpdateDiaryCommentRequest(comment);

        // when
        when(diaryCommentRepository.findDiaryCommentAndUserAndDiaryById(diaryCommentId))
                .thenReturn(Optional.of(diaryComment));
        UpdateDiaryCommentResponse response = diaryCommentService.update(userId, diaryId, diaryCommentId, request);

        // then
        assertThat(response.getDiary_id()).isEqualTo(diaryId);
        assertThat(response.getComment_id()).isEqualTo(diaryCommentId);
    }

    @Test
    @DisplayName("사용자가 영농일지 댓글 삭제시 존재하지 않는 댓글이면 ResourceNotFoundException 반환")
    void delete_diary_comment_diary_comment_not_exists_then_throw_ResourceNotFoundException() {
        // when
        when(diaryCommentRepository.findDiaryCommentAndUserAndDiaryById(notExistsDiaryCommentId))
                .thenThrow(new ResourceNotFoundException("영농일지 댓글", "ID"));

        // then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> diaryCommentService.delete(userId, diaryId, notExistsDiaryCommentId));
    }

    @Test
    @DisplayName("사용자가 영농일지 댓글 삭제시 댓글 작성자와 삭제자가 다르면 DiaryApiException 반환")
    void delete_diary_comment_user_not_same_then_throw_DiaryApiException() {
        // when
        when(diaryCommentRepository.findDiaryCommentAndUserAndDiaryById(diaryCommentId))
                .thenReturn(Optional.of(diaryComment));

        // then
        Assertions.assertThrows(DiaryApiException.class,
                () -> diaryCommentService.delete(notExistsUserId, diaryId, diaryCommentId));
    }

    @Test
    @DisplayName("사용자가 영농일지 댓글 삭제시 조회된 영농일지와 댓글이 달린 영농일지가 다르면 DiaryApiException 반환")
    void delete_diary_comment_diary_not_same_then_throw_DiaryApiException() {
        // when
        when(diaryCommentRepository.findDiaryCommentAndUserAndDiaryById(diaryCommentId))
                .thenReturn(Optional.of(diaryComment));

        // then
        Assertions.assertThrows(DiaryApiException.class,
                () -> diaryCommentService.delete(userId, notExistsDiaryId, diaryCommentId));
    }

    @Test
    @DisplayName("사용자가 영농일지 댓글 삭제시 댓글 삭제 성공")
    void delete_diary_comment_then_delete_success() {
        // when
        when(diaryCommentRepository.findDiaryCommentAndUserAndDiaryById(diaryCommentId))
                .thenReturn(Optional.of(diaryComment));
        DeleteDiaryCommentResponse response = diaryCommentService.delete(userId, diaryId, diaryCommentId);

        // then
        assertThat(response.getDiary_id()).isEqualTo(diaryId);
        assertThat(response.getComment_id()).isEqualTo(diaryCommentId);
    }
}