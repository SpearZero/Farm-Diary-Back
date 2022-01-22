package com.farmdiary.api.dto.diary;

import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.validation.code.NullOrContainCode;
import com.farmdiary.api.validation.string.NotWhiteSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDiaryRequest {

    @NotWhiteSpace
    @Length(max = 50)
    private String title;

    @Nullable
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate work_day;

    @NotWhiteSpace
    @Length(max = 70)
    private String field;

    @NotWhiteSpace
    @Length(max = 20)
    private String crop;

    @Nullable
    @Digits(integer = 2, fraction = 2)
    private BigDecimal temperature;

    @NullOrContainCode(target = Weather.class, message = "유효한 값이 아닙니다.")
    private String weather;

    @Nullable
    @Min(value = 0)
    private Integer precipitation;

    @NotWhiteSpace
    private String work_detail;
}
