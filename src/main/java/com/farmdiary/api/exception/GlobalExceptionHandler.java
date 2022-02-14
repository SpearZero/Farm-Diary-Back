package com.farmdiary.api.exception;

import com.farmdiary.api.exception.dto.ErrorDetails;
import com.farmdiary.api.exception.dto.Result;
import com.farmdiary.api.security.jwt.AuthErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 일반적인 예외(ex, DB에 중복된 값이 존재)
    @ExceptionHandler(DiaryApiException.class)
    public ResponseEntity<ErrorDetails> handleDiaryAPIException(DiaryApiException exception,
                                                                WebRequest webRequest) {

        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails
                = ErrorDetails.getErrorDetails(message, webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // DB에 값이 존재하지 않으면 발생
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                        WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails
                = ErrorDetails.getErrorDetails(message, webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // 토큰 관련 예외
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErrorDetails> handleRefreshTokenException(TokenException exception,
                                                                    WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails
                = ErrorDetails.getErrorDetails(message, webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // 접근 거부시 발생(권한이 없음)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(AccessDeniedException exception,
                                                                    WebRequest webRequest) {
        String errorMessage = "접근이 거부되었습니다.";
        Object ErrorCode = webRequest.getAttribute("authErrorCode", webRequest.SCOPE_REQUEST);
        if ((null != ErrorCode) && (ErrorCode instanceof AuthErrorCode)) {
            errorMessage = (AuthErrorCode) ErrorCode == AuthErrorCode.EXPIRED ? "접근이 거부되었습니다.(토큰 유효기간 만료)" : errorMessage;
        }

        Map<String, String> errors = new HashMap<>();
        errors.put("error", errorMessage);

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails
                = ErrorDetails.getErrorDetails(message, webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    // 잘못된 username 또는 password 사용시 발생
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(BadCredentialsException exception,
                                                                      WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "잘못된 인증 정보입니다.");

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails
                = ErrorDetails.getErrorDetails(message, webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // 메서드 인자로 설정된 validation이 유효하지 않을 경우 발생
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(ConstraintViolationException exception,
                                                                           WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails
                = ErrorDetails.getErrorDetails(message, webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // url 경로의 타입과 맞지 않을 경우 발생
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            WebRequest webRequest) {

        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails
                = ErrorDetails.getErrorDetails(message, webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // 형식에 맞지 않은 json 전달시 발생
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        errors.put("error", "필드 값의 형식이 잘못 되었습니다.");

        System.out.println(ex.getCause());

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails
                = ErrorDetails.getErrorDetails(message, request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // dto validation예외시 발생
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest webRequest) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails
                = ErrorDetails.getErrorDetails(message, webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // 나머지 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception, WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails
                = ErrorDetails.getErrorDetails(message, webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
