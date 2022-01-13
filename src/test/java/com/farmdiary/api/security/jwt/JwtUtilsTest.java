package com.farmdiary.api.security.jwt;

import com.farmdiary.api.ApiApplication;
import com.farmdiary.api.entity.user.GrantedRole;
import com.farmdiary.api.entity.user.Role;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.entity.user.UserRole;
import com.farmdiary.api.repository.user.RoleRepository;
import com.farmdiary.api.repository.user.UserRepository;
import com.farmdiary.api.repository.user.UserRoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ApiApplication.class)
@DisplayName("JwtUtils 테스트")
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String email = "email@email.com";
    private final String password = "passW0rd!";

    @BeforeEach
    public void setUp() {
        User user = userRepository.save(User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickName("nickName")
                .build());

        Role role = roleRepository.save(Role.builder()
                .name(GrantedRole.USER)
                .build());

        UserRole userRole = userRoleRepository.save(UserRole.builder()
                .user(user)
                .role(role).build());
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        userRoleRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("이메일과 패스워드가 전달되면 JWT 토큰이 생성된다.")
    public void pass_email_and_password_then_create_jwttoken() {
        // given
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        // when
        String jwtToken = jwtUtils.generateJwtToken(authentication);

        // then
        assertThat(jwtToken).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName("JWT 토큰으로부터 이메일을 추출한다.")
    public void pass_token_then_return_email() {
        // given
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // when
        String jwtToken = jwtUtils.generateJwtToken(authentication);
        String userName = jwtUtils.getUserNameFromJwtToken(jwtToken);

        assertThat(userName).isEqualTo(email);
    }

    @Test
    @Transactional
    @DisplayName("JWT의 시그니처 검증이 실패할 경우 false 반환")
    public void signautre_valid_fail_then_return_false() {
        // given
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // when
        String jwtToken = jwtUtils.generateJwtToken(authentication);
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "failSecret");
        boolean result = jwtUtils.validateJwtToken(jwtToken);

        // then
        assertThat(result).isFalse();
    }
    
    @Test
    @Transactional
    @DisplayName("JWT의 구조적인 문제가 발생할 경우 false 반환")
    public void formed_valid_fail_then_return_false() {
        // given
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // when
        String jwtToken = jwtUtils.generateJwtToken(authentication);
        boolean result = jwtUtils.validateJwtToken("malformed");

        // then
        assertThat(result).isFalse();
    }
    
    @Test
    @Transactional
    @DisplayName("JWT의 유효기간이 만료된 경우 false 반환")
    public void expired_then_return_false() {
        // given
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // when
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 0);
        String jwtToken = jwtUtils.generateJwtToken(authentication);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000);

        boolean result = jwtUtils.validateJwtToken(jwtToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("JWT 값으로 공백이 전달될 경우 false 반환")
    public void blank_valid_fail_then_return_false() {
        // given
        String token = "";

        // when
        boolean result = jwtUtils.validateJwtToken(token);

        // then
        assertThat(result).isFalse();
    }
    
    @Test
    @Transactional
    @DisplayName("정상적인 JWT 토큰이 전달될 경우 true 반환")
    public void token_valid_success_then_return_true() {
        // given
        // given
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        // when
        String jwtToken = jwtUtils.generateJwtToken(authentication);
        boolean result = jwtUtils.validateJwtToken(jwtToken);

        // then
        assertThat(result).isTrue();
    }
}