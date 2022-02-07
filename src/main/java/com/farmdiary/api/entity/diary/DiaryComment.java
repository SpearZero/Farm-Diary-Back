package com.farmdiary.api.entity.diary;

import com.farmdiary.api.entity.BaseTimeEntity;
import com.farmdiary.api.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DiaryComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Column(length = 384, nullable = false)
    private String comment;

    @Builder
    public DiaryComment(User user, Diary diary, String comment) {
        this.user = user;
        this.diary = diary;
        this.comment = comment;
    }

    public void updateComment(String comment) {
        if (null != comment && !comment.isBlank()) {
            this.comment = comment;
        }
    }
}
