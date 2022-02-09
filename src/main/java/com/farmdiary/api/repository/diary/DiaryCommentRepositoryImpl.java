package com.farmdiary.api.repository.diary;

import com.farmdiary.api.entity.diary.DiaryComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.farmdiary.api.entity.diary.QDiaryComment.diaryComment;
import static com.farmdiary.api.entity.diary.QDiary.diary;

@RequiredArgsConstructor
public class DiaryCommentRepositoryImpl implements DiaryCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DiaryComment> getDiaryComments(Long diaryId, Pageable pageable) {
        List<DiaryComment> comments = queryFactory
                .select(diaryComment)
                .from(diaryComment)
                .leftJoin(diaryComment.diary, diary)
                .fetchJoin()
                .where(diary.id.eq(diaryId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(diaryComment.createdAt.desc())
                .fetch();

        long total = queryFactory
                .select(diaryComment)
                .from(diaryComment)
                .leftJoin(diaryComment.diary, diary)
                .fetchJoin()
                .where(diary.id.eq(diaryId))
                .fetchCount();

        return PageableExecutionUtils.getPage(comments, pageable, () -> total);
    }
}
