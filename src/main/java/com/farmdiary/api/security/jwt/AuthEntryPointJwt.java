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

@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        Map<String, String> errors = new HashMap<>();
        errors.put("error", "인증에 실패했습니다.");

        Result<Map> message = new Result<Map>(errors);
        ErrorDetails errorDetails = ErrorDetails.getErrorDetails(message, request.getRequestURI());

        String errorResponse = objectMapper.writeValueAsString(errorDetails);

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorResponse);
    }
}
