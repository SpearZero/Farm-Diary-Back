package com.farmdiary.api.dto.diary;

import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.validation.ContainCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiaryRequest {

    @NotBlank
    @Length(max = 50)
    private String title;

    @NotNull(message = "값이 존재하지 않습니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate work_day;

    @NotBlank
    @Length(max = 70)
    private String field;

    @NotBlank
    @Length(max = 20)
    private String crop;

    @NotNull
    @Digits(integer = 2, fraction = 2)
    private BigDecimal temperature;

    @NotBlank
    @ContainCode(target = Weather.class, message = "유효한 값이 아닙니다.")
    private String weather;

    @NotNull
    @Min(value = 0)
    private Integer precipitation;

    @NotBlank
    private String work_detail;
}
