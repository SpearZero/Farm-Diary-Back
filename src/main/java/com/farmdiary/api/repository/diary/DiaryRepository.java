package com.farmdiary.api.repository.diary;

import com.farmdiary.api.entity.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

}
