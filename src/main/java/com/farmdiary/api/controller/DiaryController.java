package com.farmdiary.api.controller;

import com.farmdiary.api.dto.diary.create.CreateDiaryRequest;
import com.farmdiary.api.dto.diary.create.CreateDiaryResponse;
import com.farmdiary.api.dto.diary.delete.DeleteDiaryResponse;
import com.farmdiary.api.dto.diary.get.GetDiaryResponse;
import com.farmdiary.api.dto.diary.getList.GetDiariesResponse;
import com.farmdiary.api.dto.diary.update.UpdateDiaryRequest;
import com.farmdiary.api.dto.diary.update.UpdateDiaryResponse;
import com.farmdiary.api.security.service.UserDetailsImpl;
import com.farmdiary.api.service.diary.DiaryService;
import com.farmdiary.api.utils.DiaryConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<CreateDiaryResponse> save(@Valid @RequestBody CreateDiaryRequest request,
                                                    @AuthenticationPrincipal UserDetailsImpl user) {
        return new ResponseEntity<>(diaryService.save(user.getId(), request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{id}")
    public ResponseEntity<UpdateDiaryResponse> update(@PathVariable("id") Long diaryId,
                                                      @Valid @RequestBody UpdateDiaryRequest request,
                                                      @AuthenticationPrincipal UserDetailsImpl user) {
        return new ResponseEntity<>(diaryService.update(user.getId(), diaryId, request), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteDiaryResponse> delete(@PathVariable("id") Long diaryId,
                                                      @AuthenticationPrincipal UserDetailsImpl user) {
        return new ResponseEntity<>(diaryService.delete(user.getId(), diaryId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetDiaryResponse> get(@PathVariable("id") Long diaryId) {
        return new ResponseEntity<>(diaryService.get(diaryId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<GetDiariesResponse> getDiaries(
            @RequestParam(value = "pageNo", defaultValue = DiaryConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = DiaryConstants.DEFAULT_DIARY_PAGE_SIZE, required = false) @Max(100) int pageSize,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "nickname", required = false) String nickName) {

        return new ResponseEntity<>(diaryService.getDairies(pageNo, pageSize, title, nickName), HttpStatus.OK);
    }
}
