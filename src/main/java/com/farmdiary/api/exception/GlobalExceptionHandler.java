package com.farmdiary.api.exception;

import com.farmdiary.api.exception.dto.ErrorDetails;
import com.farmdiary.api.exception.dto.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
