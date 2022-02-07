package com.farmdiary.api.entity.diary;

import com.farmdiary.api.entity.BaseTimeEntity;
import com.farmdiary.api.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Diary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 150, nullable = false)
    private String title;

    @Column(columnDefinition = "DATE", nullable = false)
    private LocalDate workDay;

    @Column(length = 210, nullable = false)
    private String field;

    @Column(length = 60, nullable = false)
    private String crop;

    @Column(nullable = false)
    private Double temperature;

    @Column(length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private Weather weather;

    @Column(nullable = false)
    private Integer precipitation;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String workDetail;

    @Builder
    public Diary(String title, LocalDate workDay, String field, String crop, Double temperature,
                 Weather weather, Integer precipitation, String workDetail) {
        this.title = title;
        this.workDay = workDay;
        this.field = field;
        this.crop = crop;
        this.temperature = temperature;
        this.weather = weather;
        this.precipitation = precipitation;
        this.workDetail = workDetail;
    }

    public void setUser(User user) {
        if (null != user) {
            user.getDiaries().add(this);
            this.user = user;
        }
    }

    private void updateTitle(String title) {
        if (null != title && !title.isBlank()) {
            this.title = title;
        }
    }

    private void updateWorkDay(LocalDate workDay) {
        if (null != workDay) {
            this.workDay = workDay;
        }
    }

    private void updateField(String field) {
        if (null != field && !field.isBlank()) {
            this.field = field;
        }
    }

    private void updateCrop(String crop) {
        if (null != crop && !crop.isBlank()) {
            this.crop = crop;
        }
    }

    private void updateTemperature(Double temperature) {
        if (null != temperature) {
            this.temperature = temperature;
        }
    }

    private void updateWeather(Weather weather) {
        if (null != weather && weather != this.weather) {
            this.weather = weather;
        }
    }

    private void updatePrecipitation(Integer precipitation) {
        if (null != precipitation && precipitation >= 0) {
            this.precipitation = precipitation;
        }
    }

    private void updateWorkDetail(String workDetail) {
        if (null != workDetail && !workDetail.isBlank()) {
            this.workDetail = workDetail;
        }
    }

    public void update(String title, LocalDate workDay, String field, String crop, Weather weather,
                        Double temperature, Integer precipitation, String workDetail) {
        this.updateTitle(title);
        this.updateWorkDay(workDay);
        this.updateField(field);
        this.updateCrop(crop);
        this.updateWeather(weather);
        this.updateTemperature(temperature);
        this.updatePrecipitation(precipitation);
        this.updateWorkDetail(workDetail);
    }
}
