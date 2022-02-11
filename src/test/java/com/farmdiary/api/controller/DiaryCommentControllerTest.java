package com.farmdiary.api.controller;

import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.create.CreateDiaryCommentResponse;
import com.farmdiary.api.dto.diary.comment.delete.DeleteDiaryCommentResponse;
import com.farmdiary.api.dto.diary.comment.getList.GetDiaryCommentsDto;
import com.farmdiary.api.dto.diary.comment.getList.GetDiaryCommentsResponse;
import com.farmdiary.api.dto.diary.comment.update.UpdateDiaryCommentRequest;
import com.farmdiary.api.dto.diary.comment.update.UpdateDiaryCommentResponse;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.DiaryComment;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.User;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.PostConstruct;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    // Diary 정보
    final Long diaryId = 1L;
    final String title = "title";
    final LocalDate workDay = LocalDate.of(2022, 1, 25);
    final String field = "field";
    final String crop = "crop";
    final BigDecimal temperature = BigDecimal.valueOf(22.23);
    final String weather = Weather.SUNNY.getCode();
    final Integer precipitation = 0;
    final String workDetail = "workDetail";

    // DiaryComment 정보
    final Long commentId = 1L;
    final String comment = "comment";
    final String updateComment = "changed Comment!";

    // UserDetails 정보
    final Long userId = 1L;
    final String email = "email@email.com";
    final String password = "passW0rd1!";
    final String nickname = "nickname";
    final List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

    // Page정보
    final int pageNo = 0;
    final int pageSize = 5;
    final int overPageSize = 101;

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
    
    @Test
    @DisplayName("영농일지 댓글 조회시 영농일지 ID가 전달되지 않으면 조회 실패 응답 반환")
    void get_diary_comments_not_have_diaryId_then_return_faiL_response() throws Exception{
        // then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/diaries/test/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("영농일지 댓글 리스트 조회시 리스트 크기가 100이상일 경우 실패 응답 반환")
    void get_diary_comment_list_size_over_100_then_return_fail_response() throws Exception {
        // then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/diaries/" + diaryId + "/comments")
                        .param("pageNo", String.valueOf(pageNo))
                        .param("pageSize", String.valueOf(overPageSize))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    void setDiaryComments(List<DiaryComment> comments) {
        User user = User.builder().email(email).nickName(nickname).password(password).build();
        ReflectionTestUtils.setField(user, "id", userId);

        Diary diary = Diary.builder().title(title).workDay(workDay).field(field).crop(crop)
                .temperature(temperature.doubleValue()).weather(Weather.weather(weather).get())
                .precipitation(precipitation).workDetail(workDetail).build();
        ReflectionTestUtils.setField(diary, "id", diaryId);

        for (int i = 0; i < 10; i++) {
            DiaryComment diaryComment = DiaryComment.builder().user(user).diary(diary).comment(comment).build();
            ReflectionTestUtils.setField(diaryComment, "id", Long.valueOf(i));
            ReflectionTestUtils.setField(diaryComment, "createdAt", LocalDateTime.now());

            comments.add(diaryComment);
        }
    }

    @Test
    @DisplayName("영농일지 댓글 리스트 조회시 조회 응답 반환")
    void get_diary_comment_list_success_then_get_diary_comment_list_response() throws Exception {
        // given
        List<DiaryComment> comments = new ArrayList<>();
        setDiaryComments(comments);
        List<GetDiaryCommentsDto> getComments = comments.stream().limit(pageSize).map(comment -> new GetDiaryCommentsDto(
                comment.getId(), comment.getComment(), comment.getCreatedAt(), comment.getUser().getId(),
                comment.getUser().getNickname())).collect(Collectors.toList());

        Pageable page = PageRequest.of(0, 5);
        Page<DiaryComment> diaryCommentPage = new PageImpl<>(comments, page, comments.size());

        GetDiaryCommentsResponse getDiaryCommentsResponse = new GetDiaryCommentsResponse(diaryId, diaryCommentPage.getNumber(),
                diaryCommentPage.getSize(), getComments, diaryCommentPage.getTotalElements(), diaryCommentPage.getTotalPages(),
                diaryCommentPage.isLast());

        // when
        when(diaryCommentService.getDiaryComments(diaryId,pageNo, pageSize)).thenReturn(getDiaryCommentsResponse);

        // then
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/diaries/" + diaryId + "/comments")
                        .param("pageNo", String.valueOf(pageNo))
                        .param("pageSize", String.valueOf(pageSize))
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diary_id").value(diaryId))
                .andExpect(jsonPath("$.page_no").value(pageNo))
                .andExpect(jsonPath("$.page_size").value(pageSize))
                .andExpect(jsonPath("$.contents", hasSize(5)))
                .andExpect(jsonPath("$.total_elements").value(10))
                .andExpect(jsonPath("$.total_pages").value(2))
                .andExpect(jsonPath("$.last").value(false))
                .andDo(print());
    }

}