package com.farmdiary.api.repository.diary;

import com.farmdiary.api.dto.diary.getList.GetDiariesRequest;
import com.farmdiary.api.entity.diary.Diary;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.farmdiary.api.entity.diary.QDiary.diary;
import static com.farmdiary.api.entity.user.QUser.user;

@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Diary> searchDiary(GetDiariesRequest condition, Pageable pageable) {
        List<Diary> diaries = queryFactory
                .select(diary)
                .from(diary)
                .leftJoin(diary.user, user).fetchJoin()
                .where(titleContain(condition.getTitle()),
                    nicknameContain(condition.getNickname()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(diary.createdAt.desc())
                .fetch();

        long total = queryFactory
                .select(diary)
                .from(diary)
                .leftJoin(diary.user, user).fetchJoin()
                .where(titleContain(condition.getTitle()),
                        nicknameContain(condition.getNickname()))
                .fetchCount();

        return PageableExecutionUtils.getPage(diaries, pageable, () -> total);
    }

    private BooleanExpression titleContain(String title) {
        return StringUtils.hasText(title) ? diary.title.contains(title) : null;
    }

    private BooleanExpression nicknameContain(String nickname) {
        return StringUtils.hasText(nickname) ? diary.user.nickname.contains(nickname) : null;
    }
}
