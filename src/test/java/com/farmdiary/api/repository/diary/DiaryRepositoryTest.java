package com.farmdiary.api.repository.diary;

import com.farmdiary.api.config.QueryDslTestConfig;
import com.farmdiary.api.dto.diary.getList.GetDiariesRequest;
import com.farmdiary.api.entity.diary.Diary;
import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.GrantedRole;
import com.farmdiary.api.entity.user.Role;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.entity.user.UserRole;
import com.farmdiary.api.repository.user.RoleRepository;
import com.farmdiary.api.repository.user.UserRepository;
import com.farmdiary.api.repository.user.UserRoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Import(QueryDslTestConfig.class)
@DisplayName("DiaryRepository 테스트")
@DataJpaTest
class DiaryRepositoryTest {

    // User 관련 repository
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired UserRoleRepository userRoleRepository;

    final String nickname = "nickname";
    final String email = "email@email.com";
    final String password = "password";

    final String otherNickname = "otherNickname";
    final String otherEmail = "other@other.com";
    final String otherPassword = "pssword";

    // Diary 관련 repository
    @Autowired DiaryRepository diaryRepository;

    final String title = "title";
    final LocalDate workDay = LocalDate.of(2022, 1, 20);
    final String field = "field";
    final String crop = "crop";
    final Double temperature = 0.0;
    final Weather weather = Weather.SUNNY;
    final Integer precipitation = 100;
    final String workDetail = "workDetail";

    final String searchTitle = "title1";
    final String searchNickname = nickname;

    void insertDiary() {
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

    void insertDiaries() {
        User user1 = User.builder().email(email).nickName(nickname).password(password).build();
        User user2 = User.builder().email(otherEmail).nickName(otherNickname).password(otherPassword).build();

        Role role = Role.builder().name(GrantedRole.ROLE_USER).build();
        roleRepository.save(role);

        userRepository.save(user1);
        userRepository.save(user2);

        UserRole userRole1 = UserRole.builder().user(user1).role(role).build();
        UserRole userRole2 = UserRole.builder().user(user2).role(role).build();

        userRoleRepository.save(userRole1);
        userRoleRepository.save(userRole2);

        for (int i = 0; i <= 50; i++) {
            Diary diary = Diary.builder().title(title+i).workDay(workDay).field(field).crop(crop)
                    .temperature(temperature).weather(weather).precipitation(precipitation)
                    .workDetail(workDetail).build();
            
            if (i % 2 == 0) {
                diary.setUser(user1);
            } else {
                diary.setUser(user2);
            }

            diaryRepository.save(diary);
        }
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("영농일지 조회시 회원도 함께 조회 성공")
    void search_diary_then_searched_with_user() {
        insertDiary();

        // given
        Long diaryId = diaryRepository.findAll().get(0).getId();

        // when
        Optional<Diary> diary = diaryRepository.findDiaryAndUserById(diaryId);

        // then
        // SELECT 쿼리가 한 번만 실행된다.
        assertThat(diary.get()).isNotNull();
        assertThat(diary.get().getUser()).isNotNull();
    }
    
    @Test
    @DisplayName("영농일지 제목으로 조회시 조회 성공")
    void search_diary_title_then_search_success() {
        // given
        insertDiaries();
        Pageable page = PageRequest.of(0, 100);
        GetDiariesRequest request = new GetDiariesRequest(searchTitle, null);

        // when
        Page<Diary> diaryPage = diaryRepository.searchDiary(request, page);

        // then
        List<Diary> result = diaryPage.getContent();
        assertThat(diaryPage.getTotalElements()).isEqualTo(11);
        assertThat(result).extracting("title").contains("title1", "title10", "title11",
                "title12", "title13", "title14", "title15", "title16", "title17", "title18", "title19");
    }
    
    @Test
    @DisplayName("영농일지 닉네임으로 조회시 조회 성공")
    void search_diary_nickname_then_search_success() {
        // given
        insertDiaries();
        Pageable page = PageRequest.of(0, 100);
        GetDiariesRequest request = new GetDiariesRequest(null, searchNickname);

        // when
        Page<Diary> diaryPage = diaryRepository.searchDiary(request, page);

        // then
        List<Diary> result = diaryPage.getContent();
        assertThat(diaryPage.getTotalElements()).isEqualTo(26);
        assertThat(result).extracting("user.nickname").containsExactly(nickname, nickname, nickname,
                nickname, nickname, nickname, nickname, nickname, nickname, nickname, nickname,
                nickname, nickname, nickname, nickname, nickname, nickname, nickname, nickname,
                nickname, nickname, nickname, nickname, nickname, nickname, nickname);
    }
    
    @Test
    @DisplayName("영농일지 제목과 닉네임으로 조회시 조회 성공")
    void search_diary_title_nickname_then_search_success() {
        // given
        insertDiaries();
        Pageable page = PageRequest.of(0, 100);
        GetDiariesRequest request = new GetDiariesRequest(searchTitle, searchNickname);

        // when
        Page<Diary> diaryPage = diaryRepository.searchDiary(request, page);

        // then
        List<Diary> result = diaryPage.getContent();
        assertThat(diaryPage.getTotalElements()).isEqualTo(5);
        assertThat(result).extracting("title").contains("title10", "title12", "title14", "title16", "title18");
        assertThat(result).extracting("user.nickname").containsExactly(nickname, nickname, nickname,
                nickname, nickname);
    }
    
    @Test
    @DisplayName("영농일지 제목과 닉네임을 설정하지 않으면 모든 결과 반환")
    void search_diary_no_condition_then_all_diary_return() {
        // given
        insertDiaries();
        Pageable page = PageRequest.of(0, 100);
        GetDiariesRequest request = new GetDiariesRequest(null, null);

        // when
        Page<Diary> diaryPage = diaryRepository.searchDiary(request, page);

        // then
        assertThat(diaryPage.getTotalElements()).isEqualTo(51);
    }
}