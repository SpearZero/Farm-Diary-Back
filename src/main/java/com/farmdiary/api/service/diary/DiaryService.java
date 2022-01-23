package com.farmdiary.api.service.diary;

import com.farmdiary.api.dto.diary.CreateDiaryRequest;
import com.farmdiary.api.dto.diary.CreateDiaryResponse;
import com.farmdiary.api.dto.diary.UpdateDiaryRequest;
import com.farmdiary.api.dto.diary.UpdateDiaryResponse;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.ResourceNotFoundException;
import com.farmdiary.api.repository.diary.DiaryRepository;
import com.farmdiary.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateDiaryResponse save(Long userId, CreateDiaryRequest createDiaryRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("사용자", "ID"));

        Diary diary = createDiaryRequest.toEntity();
        diary.setUser(user);

        Diary savedDiary = diaryRepository.save(diary);

        return new CreateDiaryResponse(savedDiary.getId());
    }

    @Transactional
    public UpdateDiaryResponse update(Long userId, Long diaryId, UpdateDiaryRequest updateDiaryRequest) {
        if (!userRepository.existsById(userId)) throw new ResourceNotFoundException("사용자", "ID");

        Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> new ResourceNotFoundException("영농일지", "ID"));

        diary.update(updateDiaryRequest.getTitle(), updateDiaryRequest.getWork_day(), updateDiaryRequest.getField(),
                        updateDiaryRequest.getCrop(), Weather.weather(updateDiaryRequest.getWeather()),
                        updateDiaryRequest.getTemperature().doubleValue(), updateDiaryRequest.getPrecipitation(),
                        updateDiaryRequest.getWork_detail());

        return new UpdateDiaryResponse(diary.getId());
    }
}
