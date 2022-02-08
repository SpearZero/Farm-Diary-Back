package com.farmdiary.api.service.diary;

import com.farmdiary.api.dto.diary.create.CreateDiaryRequest;
import com.farmdiary.api.dto.diary.create.CreateDiaryResponse;
import com.farmdiary.api.dto.diary.delete.DeleteDiaryResponse;
import com.farmdiary.api.dto.diary.get.GetDiaryResponse;
import com.farmdiary.api.dto.diary.getList.GetDiariesDto;
import com.farmdiary.api.dto.diary.getList.GetDiariesRequest;
import com.farmdiary.api.dto.diary.getList.GetDiariesResponse;
import com.farmdiary.api.dto.diary.update.UpdateDiaryRequest;
import com.farmdiary.api.dto.diary.update.UpdateDiaryResponse;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.exception.DiaryApiException;
import com.farmdiary.api.exception.ResourceNotFoundException;
import com.farmdiary.api.repository.diary.DiaryRepository;
import com.farmdiary.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


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

        // 영농일지 수정시에 null 값을 이용한다.
        Weather weather = Weather.weather(updateDiaryRequest.getWeather()).orElseGet(() -> null);

        diary.update(updateDiaryRequest.getTitle(), updateDiaryRequest.getWork_day(), updateDiaryRequest.getField(),
                     updateDiaryRequest.getCrop(), weather, temperature,
                     updateDiaryRequest.getPrecipitation(), updateDiaryRequest.getWork_detail());

        return new UpdateDiaryResponse(diary.getId());
    }

    public DeleteDiaryResponse delete(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findDiaryAndUserById(diaryId)
                .orElseThrow(() -> new ResourceNotFoundException("영농일지", "ID"));

        if (!userId.equals(diary.getUser().getId())) throw new DiaryApiException("작성자와 일치하지 않습니다.");

        diaryRepository.delete(diary);

        return new DeleteDiaryResponse(diary.getId());
    }

    @Transactional(readOnly = true)
    public GetDiaryResponse get(Long diaryId) {
        Diary diary = diaryRepository.findDiaryAndUserById(diaryId)
                .orElseThrow(() -> new ResourceNotFoundException("영농일지", "ID"));

        User user = diary.getUser();

        return GetDiaryResponse.builder().user(user).diary(diary).build();
    }

    @Transactional(readOnly = true)
    public GetDiariesResponse getDairies(int pageNo, int pageSize, String title, String nickName) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        GetDiariesRequest diariesRequest = new GetDiariesRequest(title, nickName);

        Page<Diary> diaryPage = diaryRepository.searchDiary(diariesRequest, page);

        List<GetDiariesDto> diaries = diaryPage.getContent().stream().map(diary ->
                new GetDiariesDto(diary.getId(), diary.getTitle(), diary.getCreatedAt(), diary.getUser().getId(),
                                  diary.getUser().getNickname())).collect(Collectors.toList());

        return new GetDiariesResponse(diaryPage.getNumber(), diaryPage.getSize(), diaries,
                                      diaryPage.getTotalElements(), diaryPage.getTotalPages(),
                                      diaryPage.isLast());
    }
}
