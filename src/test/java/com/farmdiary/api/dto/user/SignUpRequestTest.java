package com.farmdiary.api.dto.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SignUpRequest 테스트")
class SignUpRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static Stream<String> blankNickName() {
        return Stream.of(null, "", " ", "  ");
    }

    @ParameterizedTest(name = "{index} - input nickname = {0}")
    @MethodSource("blankNickName")
    @DisplayName("닉네임에 공백 또는 null이 들어올 경우 검증 실패")
    void signUpRequest_nickname_blank_then_signUpRequest_nickname_fail(String nickName){
        // given
        String email = "exam@exam.com";
        String password = "passwWord123!";
        SignUpRequest request = new SignUpRequest(nickName, email, password);

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> invalidNickName() {
        return Stream.of("a b c", "1 2 3", "특수문자!", "닉 네 임공백", "닉네임닉네임닉네임닉네임닉네임닉길이초과");
    }

    @ParameterizedTest(name = "{index} - input nickname = {0}")
    @MethodSource("invalidNickName")
    @DisplayName("닉네임 조건이 만족하지 않을경우 검증 실패")
    void signUpRequest_nickname_invalid_then_signUpRequest_nickname_fail(String nickName){
        // given
        String email = "exam@exam.com";
        String password = "passwWord123!";
        SignUpRequest request = new SignUpRequest(nickName, email, password);

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> validNickName() {
        return Stream.of("123", "군", "a", "닉네임", "nickName", "nickName123", "안녕nickName1");
    }
    
    @ParameterizedTest(name = "{index} - input nickname = {0}")
    @MethodSource("validNickName")
    @DisplayName("닉네임 조건이 만족할 경우 검증 성공")
    void signupRequest_nickname_valid_then_signUpRequest_nickname_true(String nickName) {
        // given
        String email = "exam@exam.com";
        String password = "passwWord123!";
        SignUpRequest request = new SignUpRequest(nickName, email, password);

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    static Stream<String> blankEmail() {
        return Stream.of(null, "", " ", "  ");
    }

    @ParameterizedTest(name = "{index} - input nickname = {0}")
    @MethodSource("blankEmail")
    @DisplayName("이메일에 공백 또는 null이 들어올 경우 검증 실패")
    void signupRequest_email_blank_then_signUpRequest_email_fail(String email) {
        // given
        String nickName = "nickName";
        String password = "passwWord123!";
        SignUpRequest request = new SignUpRequest(nickName, email, password);

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> invalidEmail() {
        return Stream.of(null, "", " ", "invalid", "email@");
    }

    @ParameterizedTest(name = "{index} - input nickname = {0}")
    @MethodSource("invalidEmail")
    @DisplayName("이메일 조건이 만족하지 않을 경우 검증 실패")
    void signupRequest_email_invalid_then_signUpRequest_email_fail(String email) {
        // given
        String nickName = "nickName";
        String password = "passwWord123!";
        SignUpRequest request = new SignUpRequest(nickName, email, password);

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> validEmail() {
        return Stream.of("eample@example.cpm");
    }

    @ParameterizedTest(name = "{index} - input nickname = {0}")
    @MethodSource("validEmail")
    @DisplayName("이메일 조건이 만족할 경우 검증 성공")
    void signupRequest_email_valid_then_signUpRequest_email_suuccess(String email) {
        // given
        String nickName = "nickName";
        String password = "passwWord123!";
        SignUpRequest request = new SignUpRequest(nickName, email, password);

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }

    static Stream<String> blankPassword() {
        return Stream.of(null, "", " ", "  ");
    }

    @ParameterizedTest(name = "{index} - input password = {0}")
    @MethodSource("blankPassword")
    @DisplayName("패스워드에 공백 또는 null이 들어올 경우 검증 실패")
    void signupRequest_password_blank_then_signUpRequest_password_false(String password) {
        // given
        String nickName = "nickName";
        String email = "exam@exam.com";
        SignUpRequest request = new SignUpRequest(nickName, email, password);

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> invalidPassword() {
        return Stream.of("12345678", "aaaaaaaa", "AAAAAAAA", "!!!!!!!!",
            "password123!@#",   // not have upper
            "PASSWORD123!@#",   // not have lower
            "passwordW!@#",     // not have digit
            "Password123"       // not have special
        );
    }

    @ParameterizedTest(name = "{index} - input password = {0}")
    @MethodSource("invalidPassword")
    @DisplayName("패스워드 조건이 만족하지 않을경우 검증 실패")
    void signupRequest_password_invalid_then_signUpRequest_password_false(String password) {
        // given
        String nickName = "nickName";
        String email = "exam@exam.com";
        SignUpRequest request = new SignUpRequest(nickName, email, password);

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isFalse();
    }

    static Stream<String> validPassword() {
        return Stream.of("passwordW123!", "pawdP12#", "pawdP12#pawdP12#aaaa", "a1!@#&()–[{}]:1A", "a1;',?/*~$^+=<>1A");
    }
    
    @ParameterizedTest(name = "{index} - input password = {0}")
    @MethodSource("validPassword")
    @DisplayName("패스워드 조건이 만족할 경우 검증 성공")
    void signupRequest_password_valid_then_signUpRequest_passowrd_true(String password) {
        // given
        String nickName = "nickName";
        String email = "exam@exam.com";
        SignUpRequest request = new SignUpRequest(nickName, email, password);

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.isEmpty()).isTrue();
    }
}