package com.farmdiary.api.security.jwt;

import com.farmdiary.api.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${farmdiary.api.jwtSecret}")
    private String jwtSecret;

    @Value("${farmdiary.api.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
                .getBody().getSubject();
    }

    /**
     *
     * @param authToken
     * @return boolean
     * @throw ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException
     *
     * 이 메서드를 사용하는 곳에서 예외를 처리해줘야 한다.
     * Jwt 라이브러리에서 각각의 예외를 확인하는 메서드가 존재하지 않음.
     */
    public boolean validateJwtToken(String authToken) throws ExpiredJwtException, UnsupportedJwtException,
            MalformedJwtException, SignatureException, IllegalArgumentException{

        Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
        return true;
    }

}
