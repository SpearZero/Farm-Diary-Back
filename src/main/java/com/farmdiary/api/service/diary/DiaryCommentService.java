package com.farmdiary.api.service.diary;

import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentResponse;
import com.farmdiary.api.dto.diary.comment.delete.DeleteDiaryCommentResponse;
import com.farmdiary.api.dto.diary.comment.getList.GetDiaryCommentsDto;
import com.farmdiary.api.dto.diary.comment.getList.GetDiaryCommentsResponse;
import com.farmdiary.api.dto.diary.comment.update.UpdateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.update.UpdateDiaryCommentResponse;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.DiaryComment;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.DiaryApiException;
import com.farmdiary.api.exception.ResourceNotFoundException;
import com.farmdiary.api.repository.diary.DiaryCommentRepository;
import com.farmdiary.api.repository.diary.DiaryRepository;
import com.farmdiary.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class DiaryCommentService {

    private final DiaryRepository diaryRepository;
    private final DiaryCommentRepository diaryCommentRepository;
    private final UserRepository userRepository;

    public CreateDiaryCommentResponse save(Long userId, Long diaryId,
                                           CreateDiaryCommentRequest createDiaryCommentRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("사용자", "ID"));
        Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> new ResourceNotFoundException("영농일지", "ID"));

        DiaryComment diaryComment = DiaryComment.builder().user(user).diary(diary)
                .comment(createDiaryCommentRequest.getComment()).build();

        return new CreateDiaryCommentResponse(diary.getId(), diaryCommentRepository.save(diaryComment).getId());
    }

    public UpdateDiaryCommentResponse update(Long userId, Long diaryId, Long commentId,
                                            UpdateDiaryCommentRequest updateDiaryCommentRequest) {
        DiaryComment diaryComment = diaryCommentRepository.findDiaryCommentAndUserAndDiaryById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("영농일지 댓글", "ID"));

        Diary diary = diaryComment.getDiary();
        User user = diaryComment.getUser();

        if (!userId.equals(user.getId())) throw new DiaryApiException("댓글 작성자와 사용자가 일치하지 않습니다.");
        if (!diaryId.equals(diary.getId())) throw new DiaryApiException("해당 영농일지의 댓글이 아닙니다.");

        diaryComment.updateComment(updateDiaryCommentRequest.getComment());

        return new UpdateDiaryCommentResponse(diary.getId(), diaryComment.getId());
    }

    public DeleteDiaryCommentResponse delete(Long userId, Long diaryId, Long commentId) {
        DiaryComment diaryComment = diaryCommentRepository.findDiaryCommentAndUserAndDiaryById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("영농일지 댓글", "ID"));

        Diary diary = diaryComment.getDiary();
        User user = diaryComment.getUser();

        if (!userId.equals(user.getId())) throw new DiaryApiException("댓글 작성자와 사용자가 일치하지 않습니다.");
        if (!diaryId.equals(diary.getId())) throw new DiaryApiException("해당 영농일지의 댓글이 아닙니다.");

        diaryCommentRepository.delete(diaryComment);

        return new DeleteDiaryCommentResponse(diary.getId(), diaryComment.getId());
    }

    @Transactional(readOnly = true)
    public GetDiaryCommentsResponse getDiaryComments(Long diaryId, int pageNo, int pageSize) {
        if (!diaryRepository.existsById(diaryId)) throw new ResourceNotFoundException("영농일지", "ID");

        Pageable page = PageRequest.of(pageNo, pageSize);

        Page<DiaryComment> diaryCommentPage = diaryCommentRepository.getDiaryComments(diaryId, page);

        List<GetDiaryCommentsDto> comments = diaryCommentPage.getContent().stream().map(comment ->
                    new GetDiaryCommentsDto(comment.getId(), comment.getComment(), comment.getCreatedAt(),
                                            comment.getUser().getId(), comment.getUser().getNickname()))
                    .collect(Collectors.toList());

        return new GetDiaryCommentsResponse(diaryId, diaryCommentPage.getNumber(), diaryCommentPage.getSize(),
                                            comments, diaryCommentPage.getTotalElements(),
                                            diaryCommentPage.getTotalPages(), diaryCommentPage.isLast());
    }
}
