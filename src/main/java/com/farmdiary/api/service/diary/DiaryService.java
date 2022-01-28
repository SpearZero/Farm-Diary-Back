package com.farmdiary.api.service.diary;

import com.farmdiary.api.dto.diary.*;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.DiaryApiException;
import com.farmdiary.api.exception.ResourceNotFoundException;
import com.farmdiary.api.repository.diary.DiaryRepository;
import com.farmdiary.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    public CreateDiaryResponse save(Long userId, CreateDiaryRequest createDiaryRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("사용자", "ID"));

        Diary diary = createDiaryRequest.toEntity();
        diary.setUser(user);

        Diary savedDiary = diaryRepository.save(diary);

        return new CreateDiaryResponse(savedDiary.getId());
    }

    public UpdateDiaryResponse update(Long userId, Long diaryId, UpdateDiaryRequest updateDiaryRequest) {

        Diary diary = diaryRepository.findDiaryAndUserById(diaryId)
                .orElseThrow(() -> new ResourceNotFoundException("영농일지", "ID"));

        if (!userId.equals(diary.getUser().getId())) throw new DiaryApiException("작성자와 일치하지 않습니다.");

        Double temperature = null == updateDiaryRequest.getTemperature()
                ? null : updateDiaryRequest.getTemperature().doubleValue();

        diary.update(updateDiaryRequest.getTitle(), updateDiaryRequest.getWork_day(), updateDiaryRequest.getField(),
                        updateDiaryRequest.getCrop(), Weather.weather(updateDiaryRequest.getWeather()),
                        temperature, updateDiaryRequest.getPrecipitation(),
                        updateDiaryRequest.getWork_detail());

        return new UpdateDiaryResponse(diary.getId());
    }

    public DeleteDiaryResponse delete(Long userId, Long diaryId) {

        Diary diary = diaryRepository.findDiaryAndUserById(diaryId)
                .orElseThrow(() -> new ResourceNotFoundException("영농일지", "ID"));

        if (!userId.equals(diary.getUser().getId())) throw new DiaryApiException("작성자와 일치하지 않습니다.");

        diaryRepository.delete(diary);

        return new DeleteDiaryResponse(diary.getId());
    }

    public GetDiaryResponse get(Long diaryId) {

        Diary diary = diaryRepository.findDiaryAndUserById(diaryId)
                .orElseThrow(() -> new ResourceNotFoundException("영농일지", "ID"));

        User user = diary.getUser();
        GetDiaryResponse diaryResponse = GetDiaryResponse.builder().user(user).diary(diary).build();

        return diaryResponse;
    }
}
