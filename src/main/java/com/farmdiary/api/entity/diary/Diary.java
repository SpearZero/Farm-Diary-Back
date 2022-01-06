package com.farmdiary.api.entity.diary;

import com.farmdiary.api.entity.BaseTimeEntity;
import com.farmdiary.api.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

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

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "DATE", nullable = false)
    private LocalDate workDay;

    @Column(length = 200, nullable = false)
    private String field;

    @Column(length = 50, nullable = false)
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
        if (null != user && user.isEmailVerified()) {
            user.getDiaries().add(this);
            this.user = user;
        }
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateWorkDay(LocalDate workDay) {
        this.workDay = workDay;
    }

    public void updateField(String field) {
        this.field = field;
    }

    public void updateCrop(String crop) {
        this.crop = crop;
    }

    public void updateTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void updateWeather(Weather weather) {
        this.weather = weather;
    }

    public void updatePrecipitation(Integer precipitation) {
        this.precipitation = precipitation;
    }

    public void updateWorkDetail(String workDetail) {
        this.workDetail = workDetail;
    }
}
