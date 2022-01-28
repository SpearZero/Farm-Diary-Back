package com.farmdiary.api.controller;

import com.farmdiary.api.dto.diary.CreateDiaryRequest;
import com.farmdiary.api.dto.diary.UpdateDiaryRequest;
import com.farmdiary.api.security.service.UserDetailsImpl;
import com.farmdiary.api.service.diary.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody CreateDiaryRequest request,
                                  @AuthenticationPrincipal UserDetailsImpl user) {
        return new ResponseEntity<>(diaryService.save(user.getId(), request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long diaryId,
                                    @Valid @RequestBody UpdateDiaryRequest request,
                                    @AuthenticationPrincipal UserDetailsImpl user) {
        return new ResponseEntity<>(diaryService.update(user.getId(), diaryId, request), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long diaryId,
                                    @AuthenticationPrincipal UserDetailsImpl user) {
        return new ResponseEntity<>(diaryService.delete(user.getId(), diaryId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long diaryId) {
        return new ResponseEntity<>(diaryService.get(diaryId), HttpStatus.OK);
    }
}
