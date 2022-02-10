package com.farmdiary.api.controller;

import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentResponse;
import com.farmdiary.api.security.service.UserDetailsImpl;
import com.farmdiary.api.service.diary.DiaryCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/diaries")
public class DiaryCommentController {

    private final DiaryCommentService diaryCommentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{diaryId}/comments")
    public ResponseEntity<CreateDiaryCommentResponse> save(@PathVariable("diaryId") Long diaryId,
                                                           @Valid @RequestBody CreateDiaryCommentRequest request,
                                                           @AuthenticationPrincipal UserDetailsImpl user) {
        return new ResponseEntity<>(diaryCommentService.save(user.getId(), diaryId, request), HttpStatus.CREATED);
    }
}
