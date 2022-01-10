package com.farmdiary.api.dto.user;

import com.farmdiary.api.entity.user.User;
import lombok.Getter;

@Getter
public class SignUpResponse {
    private Long id;

    public SignUpResponse(User user) {
        id = user.getId();
    }
}
