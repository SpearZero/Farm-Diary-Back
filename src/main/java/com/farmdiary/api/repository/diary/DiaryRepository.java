package com.farmdiary.api.repository.diary;

import com.farmdiary.api.entity.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom {

    @Query("select d from Diary d join fetch d.user where d.id = :id")
    Optional<Diary> findDiaryAndUserById(@Param("id") Long id);
}
