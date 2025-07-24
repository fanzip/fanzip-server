package org.example.fanzip.auth.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtProcessor jwtProcessor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("jwtInterceptor preHandle");

        String header=request.getHeader("Authorization");

        if(header==null || !header.startsWith("Bearer ")){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없습니다.");
            return false;
        }

        String token=header.substring(7);
        System.out.println("token:"+token);
        try{
            Jws<Claims> claims= jwtProcessor.parseToken(token);

            System.out.println("claims:"+claims);
            String userId=claims.getBody().get("userId", String.class);
            request.setAttribute("userId", userId);
            return true;
        }catch (JwtException e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            return false;
        }
    }
}
