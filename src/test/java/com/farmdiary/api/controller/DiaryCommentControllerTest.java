package com.farmdiary.api.controller;

import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentResponse;
import com.farmdiary.api.dto.diary.comment.delete.DeleteDiaryCommentResponse;
import com.farmdiary.api.dto.diary.comment.update.UpdateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.update.UpdateDiaryCommentResponse;
import com.farmdiary.api.security.jwt.AuthEntryPointJwt;
import com.farmdiary.api.security.jwt.JwtUtils;
import com.farmdiary.api.security.service.UserDetailsImpl;
import com.farmdiary.api.service.diary.DiaryCommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.PostConstruct;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("DiaryCommentController 테스트")
@WebMvcTest(DiaryCommentController.class)
class DiaryCommentControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean DiaryCommentService diaryCommentService;
    @MockBean JwtUtils jwtUtils;
    @MockBean AuthEntryPointJwt authEntryPointJwt;
    @MockBean UserDetailsService userDetailsService;

    // 영농일지 댓글 정보
    final Long diaryId = 1L;
    final Long commentId = 1L;
    final String comment = "comment";
    final String updateComment = "changed Comment!";

    // UserDetails 정보
    final Long userId = 1L;
    final String email = "email@email.com";
    final String password = "passW0rd1!";
    final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

    @PostConstruct
    void userSetUp() {
        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(new UserDetailsImpl(userId, email, password, authorities));
    }

    @Test
    @DisplayName("영농일지 댓글 작성 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void post_diary_comment_success_then_return_post_diary_comment_response() throws Exception {
        // given
        CreateDiaryCommentRequest request = new CreateDiaryCommentRequest(comment);
        String body = objectMapper.writeValueAsString(request);

        // when
        when(diaryCommentService.save(any(Long.class), any(Long.class), any(CreateDiaryCommentRequest.class)))
                .thenReturn(new CreateDiaryCommentResponse(diaryId, commentId));

        // then
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/diaries/"+diaryId+"/comments")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diary_id").value(diaryId))
                .andExpect(jsonPath("$.comment_id").value(commentId));
    }
    
    @Test
    @DisplayName("영농일지 댓글 수정 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void update_diary_comment_success_then_return_update_diary_comment_response() throws Exception {
        // given
        UpdateDiaryCommentRequest request = new UpdateDiaryCommentRequest(updateComment);
        String body = objectMapper.writeValueAsString(request);

        // when
        when(diaryCommentService.update(any(Long.class), any(Long.class), any(Long.class),
                any(UpdateDiaryCommentRequest.class))).thenReturn(new UpdateDiaryCommentResponse(diaryId, commentId));

        // then
        mvc.perform(MockMvcRequestBuilders.put("/api/v1/diaries/"+diaryId+"/comments/"+commentId)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId))
                .andExpect(jsonPath("$.comment_id").value(commentId));
    }
    
    @Test
    @DisplayName("영농일지 댓글 삭제 성공시 성공 응답 반환")
    @WithUserDetails(value = email)
    void delete_diary_comment_success_then_return_delete_diary_comment_response() throws Exception {
        // when
        when(diaryCommentService.delete(userId, diaryId, commentId))
                .thenReturn(new DeleteDiaryCommentResponse(diaryId, commentId));

        // then
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/diaries/"+diaryId+"/comments/"+commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId))
                .andExpect(jsonPath("$.comment_id").value(commentId));
    }
}