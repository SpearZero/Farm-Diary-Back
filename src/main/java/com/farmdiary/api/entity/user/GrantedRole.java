package com.farmdiary.api.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GrantedRole {

    USER("유저", "00"),
    ADMIN("관리자", "01");

    private String viewName;
    private String code;
}
