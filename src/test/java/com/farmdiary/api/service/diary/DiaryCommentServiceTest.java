package com.farmdiary.api.service.diary;

import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentResponse;
import com.farmdiary.api.dto.diary.comment.delete.DeleteDiaryCommentResponse;
import com.farmdiary.api.dto.diary.comment.getList.GetDiaryCommentsResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // DiaryComment 리스트
    List<DiaryComment> diaryComments = new ArrayList<>();
    Pageable page;
    final int pageNo = 0;
    final int pageSize = 5;

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
        diaryComment = null;
        diaryComments = null;
        page = null;
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
    
    @Test
    @DisplayName("사용자가 영농일지 댓글리스트 조회시 영농일지가 존재하지 않으면 ResourceNotFoundException 반환")
    void get_diary_comments_diary_not_exists_then_throw_ResourceNotFoundException() {
        // when
        when(diaryRepository.existsById(notExistsDiaryId)).thenReturn(false);

        // then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> diaryCommentService.getDiaryComments(notExistsDiaryId, pageNo, pageSize));
    }

    void insertComments() {
        for (int i = 0; i < 10; i++) {
            DiaryComment diaryComment = DiaryComment.builder().user(user).diary(diary).comment(comment + i).build();
            ReflectionTestUtils.setField(diaryComment, "id", Long.valueOf(i));
            diaryComments.add(diaryComment);
        }
    }

    @Test
    @DisplayName("사용자가 영농일지 댓글 조회시 영농일지 댓글 리스트 반환")
    void get_diary_comments_then_return_diary_comments() {
        // given
        insertComments();
        page = PageRequest.of(pageNo, pageSize);
        List<DiaryComment> comments = diaryComments.stream().limit(5).collect(Collectors.toList());
        Page<DiaryComment> diaryCommentPage = new PageImpl<>(comments, page, diaryComments.size());

        // when
        when(diaryRepository.existsById(diaryId)).thenReturn(true);
        when(diaryCommentRepository.getDiaryComments(diaryId, page)).thenReturn(diaryCommentPage);
        GetDiaryCommentsResponse diaryCommentsResponse = diaryCommentService.getDiaryComments(diaryId, pageNo, pageSize);

        // then
        assertThat(diaryCommentsResponse.getContents().size()).isEqualTo(5);
        assertThat(diaryCommentsResponse.getPage_no()).isEqualTo(pageNo);
        assertThat(diaryCommentsResponse.getPage_size()).isEqualTo(pageSize);
        assertThat(diaryCommentsResponse.getTotal_elements()).isEqualTo(10);
        assertThat(diaryCommentsResponse.getTotal_pages()).isEqualTo(2);
        assertThat(diaryCommentsResponse.getLast()).isEqualTo(false);
    }
}