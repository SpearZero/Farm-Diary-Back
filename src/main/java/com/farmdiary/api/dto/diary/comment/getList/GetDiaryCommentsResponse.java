package com.farmdiary.api.dto.diary.comment.getList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetDiaryCommentsResponse {

    private Long diary_id;

    private Integer page_no;
    private Integer page_size;

    private List<GetDiaryCommentsDto> contents;

    private Long total_elements;
    private Integer total_pages;
    private Boolean last;
}
