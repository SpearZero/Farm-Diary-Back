package com.farmdiary.api.dto.diary;

import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.validation.code.NullOrContainCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

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

    @NotNull
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

    @NullOrContainCode(target = Weather.class, message = "유효한 값이 아닙니다.")
    private String weather = Weather.ETC.getCode();

    @Nullable
    @Min(value = 0)
    private Integer precipitation = 0;

    @NotBlank
    private String work_detail;

    public Diary toEntity() {
        Weather codeToWeather = Weather.weather(this.weather).get();

        return Diary.builder()
                .title(title)
                .workDay(work_day)
                .field(field)
                .crop(crop)
                .temperature(temperature.doubleValue())
                .weather(codeToWeather)
                .precipitation(precipitation)
                .workDetail(work_detail)
                .build();
    }
}
