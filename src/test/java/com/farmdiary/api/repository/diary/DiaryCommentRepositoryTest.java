package com.farmdiary.api.repository.diary;

import com.farmdiary.api.config.QueryDslTestConfig;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.DiaryComment;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.GrantedRole;
import com.farmdiary.api.entity.user.Role;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.entity.user.UserRole;
import com.farmdiary.api.repository.user.RoleRepository;
import com.farmdiary.api.repository.user.UserRepository;
import com.farmdiary.api.repository.user.UserRoleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Import(QueryDslTestConfig.class)
@DisplayName("DiaryCommentRepository 테스트")
@DataJpaTest
class DiaryCommentRepositoryTest {

    // User 관련 repository
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired UserRoleRepository userRoleRepository;

    // user 정보
    final String nickname = "nickname";
    final String email = "email@email.com";
    final String password = "passW0rd1!";

    // Diary 관련 repository
    @Autowired DiaryRepository diaryRepository;
    @Autowired DiaryCommentRepository diaryCommentRepository;

    final String title = "title";
    final LocalDate workDay = LocalDate.of(2022, 1, 20);
    final String field = "field";
    final String crop = "crop";
    final Double temperature = 0.0;
    final Weather weather = Weather.SUNNY;
    final Integer precipitation = 100;
    final String workDetail = "workDetail";

    final String comment = "comment";

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


        DiaryComment diaryComment = DiaryComment.builder().user(user).diary(diary).comment(comment).build();
        diaryCommentRepository.save(diaryComment);
    }

    @AfterEach
    void tearDown() {
        userRoleRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
        diaryCommentRepository.deleteAll();
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("영농일지 댓글 조회시 회원과 영농일지도 함께 조회 성공")
    void search_diary_comment_then_searched_with_user_diary() {
        // given
        Long diaryCommentId = diaryCommentRepository.findAll().get(0).getId();

        // when
        Optional<DiaryComment> diaryCommentOptional
                = diaryCommentRepository.findDiaryCommentAndUserAndDiaryById(diaryCommentId);
        DiaryComment diaryComment = diaryCommentOptional.get();

        // then
        assertThat(diaryComment.getComment()).isEqualTo(comment);
        assertThat(diaryComment.getDiary()).isNotNull();
        assertThat(diaryComment.getUser()).isNotNull();
    }
}