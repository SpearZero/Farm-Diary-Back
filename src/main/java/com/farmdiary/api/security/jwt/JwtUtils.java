package com.farmdiary.api.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${farmdiary.api.jwtSecret}")
    private String jwtSecret;

    @Value("${farmdiary.api.jwtExpirationMs}")
    private long jwtExpirationMs;

    @Value("${farmdiary.api.jwtRefreshExpirationMs}")
    private long jwtRefreshExpirationMs;

    public String generateAccessToken(String username) {
        return generateTokenFromUsername(username, TokenType.ACCESS_TOKEN);
    }

    public String generateRefreshToken(String username) {
        return generateTokenFromUsername(username, TokenType.REFRESH_TOKEN);
    }

    private String generateTokenFromUsername(String username, TokenType tokenType) {
        long expirationMs = tokenType == TokenType.ACCESS_TOKEN ? jwtExpirationMs : jwtRefreshExpirationMs;

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expirationMs))
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
     * jjwt 라이브러리는 토큰을 검증하는 도중에 예외를 발생시키기 때문에 토큰울 추출한 후 예외를 검증할 수 없다.
     *
     */
    public boolean validateJwtToken(String authToken) throws ExpiredJwtException, UnsupportedJwtException,
            MalformedJwtException, SignatureException, IllegalArgumentException{

        Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
        return true;
    }

}
