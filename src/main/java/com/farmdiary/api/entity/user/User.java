package com.farmdiary.api.entity.user;

import com.farmdiary.api.entity.BaseTimeEntity;
import com.farmdiary.api.entity.diary.Diary;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 45, unique = true, nullable = false)
    private String nickname;

    @Column(length = 50, unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 2000)
    private String profileImage;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Diary> diaries = new ArrayList<>();

    @Builder
    public User(String nickName, String email, String password) {
        this.nickname = nickName;
        this.email = email;
        this.password = password;
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }
}
