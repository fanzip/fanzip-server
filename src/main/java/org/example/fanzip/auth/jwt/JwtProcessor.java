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
    static private final long TOKEN_VALID_MILISECOND=1000L*60*30;

    private final Key key;

    public JwtProcessor(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    //JWT 생성
    public String generateToken(Long userId) {
        JwtBuilder builder= Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+TOKEN_VALID_MILISECOND))
                .signWith(key, SignatureAlgorithm.HS256);

        if(userId!=null){
            builder.claim("userId",userId);
        }
        return builder.compact();
    }


    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    //JWT Subject(username) 추출
    public String getUserId(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId").toString();
    }
    //JWT 검증(유효 기간 검증)-해석 불가인 경우 예외 발생
    public boolean validateToken(String token){
        Jws<Claims> claims=Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return true;
    }
}
