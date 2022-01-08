package com.farmdiary.api.dto.user;

import com.farmdiary.api.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{1,15}$", message = "한글,영어,숫자 1~15자리를 입력해주세요")
    private String nickName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
            message = "소문자,대문자,특수문자(!@#&()–[{}]:;',?/*~$^+=<>),숫자를 포함한 8~15자리를 입력해주세요"
    )
    private String password;

    public User toEntity(){
        return User.builder()
                .email(email)
                .nickName(nickName)
                .password(password)
                .build();
    }
}
