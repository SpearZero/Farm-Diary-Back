package com.farmdiary.api.repository.user;

import com.farmdiary.api.config.QueryDslTestConfig;
import com.farmdiary.api.entity.user.GrantedRole;
import com.farmdiary.api.entity.user.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Import(QueryDslTestConfig.class)
@DisplayName("RoleRepository 테스트")
@DataJpaTest
class RoleRepositoryTest {

    @Autowired RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        Role role = Role.builder()
                .name(GrantedRole.ROLE_USER)
                .build();

        roleRepository.save(role);
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }
    
    @Test
    @DisplayName("역할(Role) 명으로 역할 조회시 조회 성공")
    void search_role_name_then_searched() {
        // given
        GrantedRole grantedRole = GrantedRole.ROLE_USER;

        // when
        Optional<Role> role = roleRepository.findByName(grantedRole);

        // then
        assertThat(role.get()).isNotNull();
    }
}
