package com.farmdiary.api.exception;

import com.farmdiary.api.dto.exception.ErrorDetails;
import com.farmdiary.api.dto.exception.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DiaryApiException.class)
    public ResponseEntity<ErrorDetails> handleDiaryAPIException(DiaryApiException exception,
                                                                WebRequest webRequest) {

        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());

        Result result = new Result<Map>(errors);

        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                result, webRequest.getDescription(false));

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

        Result result = new Result<Map>(errors);
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                result, webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
