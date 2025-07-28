package org.example.fanzip.auth.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtProcessor jwtProcessor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("Jwt Interceptor is running");

        String header=request.getHeader("Authorization");

        // 1. Authorization 헤더가 없거나 형식이 틀렸을 경우
        if(header==null || !header.startsWith("Bearer ")){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없습니다.");//401 에러
            return false;
        }

        String token=header.substring(7);
        log.info("token:{}",token);


        //2. 유효성 검증
        if(!jwtProcessor.validateToken(token)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");//401 에러
            return false;
        }

        //3. 토큰 만료 여부 체크
        if(jwtProcessor.isExpired(token)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료 되었습니다");//401 에러
            return false;
        }

        Long userId=jwtProcessor.getUserId(token);
        request.setAttribute("userId", userId);

        return true;
    }
}
