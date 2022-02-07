package com.farmdiary.api.service.diary;

import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentResponse;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.DiaryComment;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.ResourceNotFoundException;
import com.farmdiary.api.repository.diary.DiaryCommentRepository;
import com.farmdiary.api.repository.diary.DiaryRepository;
import com.farmdiary.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        DiaryComment savedComment = diaryCommentRepository.save(diaryComment);

        return new CreateDiaryCommentResponse(user.getId(), diary.getId(), savedComment.getId());
    }
}
