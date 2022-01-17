package com.farmdiary.api.service.token;

import com.farmdiary.api.dto.user.auth.JwtResponse;
import com.farmdiary.api.dto.user.auth.LoginRequest;
import com.farmdiary.api.security.jwt.JwtUtils;
import com.farmdiary.api.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional(readOnly = true)
    public JwtResponse getAccessToken(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessToken(userDetails);

        return new JwtResponse(accessToken, userDetails.getId(), userDetails.getEmail());
    }
}
