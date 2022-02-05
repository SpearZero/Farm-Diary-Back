package com.farmdiary.api.repository.diary;

import com.farmdiary.api.dto.diary.SearchDiaryRequest;
import com.farmdiary.api.entity.diary.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiaryRepositoryCustom {

    Page<Diary> searchDiary(SearchDiaryRequest condition, Pageable pageable);
}
