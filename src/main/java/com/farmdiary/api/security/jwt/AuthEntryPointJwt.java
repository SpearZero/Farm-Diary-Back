package com.farmdiary.api.security.jwt;

import com.farmdiary.api.exception.dto.ErrorDetails;
import com.farmdiary.api.exception.dto.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        AuthErrorCode errorCode = Optional.ofNullable((AuthErrorCode)request.getAttribute("authErrorCode"))
                .orElseGet(() -> AuthErrorCode.ETC);

        Map<String, String> errors = new HashMap<>();
        errors.put("error", errorCode.getMessage());

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails = ErrorDetails.getErrorDetails(message, "errorCode : " + errorCode.getCode());

        String errorResponse = objectMapper.writeValueAsString(errorDetails);

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorResponse);
    }
}
