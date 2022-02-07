package com.farmdiary.api.repository.diary;

import com.farmdiary.api.entity.diary.DiaryComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryCommentRepository extends JpaRepository<DiaryComment, Long> {
}
