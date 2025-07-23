package org.example.fanzip.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProcessor {
    static private final long TOKEN_VALID_MILISECOND=1000L*60*5;



    private final Key key;

    public JwtProcessor(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

//    @Value("${jwt.secret}")
//    private String secretKey;

//    private String secretKey="SuperSecretKeyForJWTSigning123!@#";
//    private Key key=Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//    private Key key=Keys.secretKeyFor(SignatureAlgorithm.HS256);
    //JWT 생성
    public String generateToken(String socialType, String socialId){
//        System.out.println("key:"+key);

        return Jwts.builder()
//                .setSubject(socialType+":"+socialId)
                .claim("socialType",socialType)
                .claim("socialId",socialId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+TOKEN_VALID_MILISECOND))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    //JWT Subject(username) 추출
    public String getUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
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
