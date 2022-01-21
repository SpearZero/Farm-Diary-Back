package com.farmdiary.api.repository.diary;

import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.GrantedRole;
import com.farmdiary.api.entity.user.Role;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.entity.user.UserRole;
import com.farmdiary.api.repository.user.RoleRepository;
import com.farmdiary.api.repository.user.UserRepository;
import com.farmdiary.api.repository.user.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DiaryRepository 테스트")
@DataJpaTest
class DiaryRepositoryTest {

    // User 관련 repository
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRoleRepository userRoleRepository;

    private final String nickname = "nickname";
    private final String email = "email@email.com";
    private final String password = "password";

    // Diary 관련 repository
    @Autowired private DiaryRepository diaryRepository;

    private final String title = "title";
    private final LocalDate workDay = LocalDate.of(2022, 1, 20);
    private final String field = "field";
    private final String crop = "crop";
    private final Double temperature = 0.0;
    private final Weather weather = Weather.SUNNY;
    private final Integer precipitation = 100;
    private final String workDetail = "workDetail";

    @BeforeEach
    void setUp() {
        User user = User.builder().email(email).nickName(nickname).password(password).build();
        Role role = Role.builder().name(GrantedRole.ROLE_USER).build();

        roleRepository.save(role);
        userRepository.save(user);

        UserRole userRole = UserRole.builder().user(user).role(role).build();

        userRoleRepository.save(userRole);

        Diary diary = Diary.builder().title(title).workDay(workDay).field(field).crop(crop)
                .temperature(temperature).weather(weather).precipitation(precipitation)
                .workDetail(workDetail).build();
        diary.setUser(user);

        diaryRepository.save(diary);
    }

    @Test
    @DisplayName("영농일지 조회시 회원도 함께 조회된다.")
    void search_diary_then_searched_with_user() {
        // given
        Long diaryId = 1l;

        // when
        Optional<Diary> diary = diaryRepository.findDiaryAndUserById(diaryId);

        // then
        // SELECT 쿼리가 한 번만 실행된다.
        assertThat(diary.get()).isNotNull();
        assertThat(diary.get().getUser()).isNotNull();
    }
}