package com.farmdiary.api.security.jwt;

import com.farmdiary.api.entity.diary.Weather;
import com.farmdiary.api.entity.user.User;
import com.farmdiary.api.security.service.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthTokenFilter 테스트")
@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @InjectMocks private AuthTokenFilter authTokenFilter;
    @Mock private JwtUtils jwtUtils;
    @Mock private UserDetailsService userDetailsService;
    @Mock private Authentication authentication;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;
    private AuthTokenFilter jwtFilter;

    private final String authorizationHeader = "Authorization";
    private final String email = "email@email.com";
    private final String jwtSecret = "secret";
    private final long jwtExpirationMs = 864000;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        jwtFilter = new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private String makeJwtToken() {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 authentication이 설정되지 않음")
    void not_have_authorization_header_then_not_set_authentication() throws ServletException, IOException {
        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @ParameterizedTest(name = "{index} - input header = {0}(blank)")
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Authorization 헤더가 빈 문자열이면 authentication이 설정되지 않음")
    void blank_authorization_header_then_not_set_authentication(String blankAuthorizationHeader)
            throws ServletException, IOException {
        // when
        request.addHeader(authorizationHeader, blankAuthorizationHeader);
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
    
    @Test
    @DisplayName("Authorization 헤더가 Bearer 로 시작하지 않으면 authentication이 설정되지 않음")
    void authorization_header_not_startswith_beadrer_then_not_set_authentiction()
            throws ServletException, IOException {
        // case
        String notBearerHeader = "notBearer aaa";

        // when
        request.addHeader(authorizationHeader, notBearerHeader);
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Authorization 헤더와 토큰이 정상이면 authentication이 설정됨")
    void valid_authorization_header_and_token_then_set_authentication() throws ServletException, IOException {
        // case
        String jwtToken = makeJwtToken();
        String tokenHeader = "Bearer " + jwtToken;
        request.addHeader(authorizationHeader, tokenHeader);

        User user = User.builder().email(email).build();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        when(jwtUtils.validateJwtToken(jwtToken)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(jwtToken)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }
}