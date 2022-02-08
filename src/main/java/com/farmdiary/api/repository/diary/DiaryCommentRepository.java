package com.farmdiary.api.repository.diary;

import com.farmdiary.api.entity.diary.DiaryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DiaryCommentRepository extends JpaRepository<DiaryComment, Long> {

    @Query("select dc from DiaryComment dc join fetch dc.user join fetch dc.diary where dc.id = :id")
    Optional<DiaryComment> findDiaryCommentAndUserAndDiaryById(@Param("id") Long id);
}
