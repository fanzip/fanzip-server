package org.example.fanzip.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.fanzip.global.config.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class JwtProcessor {

    static private final long ACCESS_TOKEN_VALID_MILISECOND=30*60*1000L;//30분
    static private final long
            REFRESH_TOKEN_VALID_MILISECOND=7*24*60*60*1000L;//7일

    private final Key key;

    public JwtProcessor(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String role, String subject, Long validityMillis) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+validityMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateAccessToken(Long userId, String role) {
        return generateToken(userId, role, "ACCESS_TOKEN", ACCESS_TOKEN_VALID_MILISECOND);
    }
    public String generateRefreshToken(Long userId, String role) {
        return generateToken(userId, role,"REFRESH_TOKEN", REFRESH_TOKEN_VALID_MILISECOND);
    }

    //JWT 검증(유효 기간 검증)-해석 불가인 경우 예외 발생
    public boolean validateToken(String token){
        try{
            parseToken(token);
            return true;
        }catch(JwtException | IllegalArgumentException e){
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
    public Long getUserIdFromToken(String token){
        return parseToken(token)
                .get("userId",Long.class);
    }

    public String getRoleFromToken(String token){
        return parseToken(token)
                .get("role",String.class);
    }

    public int getRefreshTokenExpiryInSeconds(){
        return (int)(REFRESH_TOKEN_VALID_MILISECOND/1000);
    }
}