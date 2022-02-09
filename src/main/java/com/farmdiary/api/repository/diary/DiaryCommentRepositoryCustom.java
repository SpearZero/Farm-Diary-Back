package com.farmdiary.api.repository.diary;

import com.farmdiary.api.entity.diary.DiaryComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiaryCommentRepositoryCustom {

    Page<DiaryComment> getDiaryComments(Long diaryId, Pageable pageable);
}
