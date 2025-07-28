package org.example.fanzip.auth.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProcessor {
    static private final long ACCESS_TOKEN_VALID_MILISECOND=30*60*1000L;//30분
    static private final long
            REFRESH_TOKEN_VALID_MILISECOND=7*24*60*60*1000L;//7일

    private final Key key;

    public JwtProcessor(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    //JWT 생성(access token)
    public String generateToken(Long userId, String subject, Long validityMillis) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+validityMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateAccessToken(Long userId) {
        return generateToken(userId, "ACCESS_TOKEN", ACCESS_TOKEN_VALID_MILISECOND);
    }
    public String generateRefreshToken(Long userId) {
        return generateToken(userId, "REFRESH_TOKEN", REFRESH_TOKEN_VALID_MILISECOND);
    }

    //JWT 검증(유효 기간 검증)-해석 불가인 경우 예외 발생
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch(JwtException|IllegalArgumentException e){
            return false;
        }
    }
    // JWT 토큰에서 Claims 추출
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //JWT 토큰에서 userId 추출
    public Long getUserId(String token){
        return parseToken(token)
                .get("userId",Long.class);
    }

    //JWT 만료 여부
    public boolean isExpired(String token){
        try{
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        }catch(JwtException e){
            return true;
        }
    }
}
