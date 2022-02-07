package com.farmdiary.api.repository.diary;

import com.farmdiary.api.dto.diary.getList.GetDiariesRequest;
import com.farmdiary.api.entity.diary.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiaryRepositoryCustom {

    Page<Diary> searchDiary(GetDiariesRequest condition, Pageable pageable);
}
