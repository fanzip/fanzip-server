package org.example.fanzip.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProcessor jwtProcessor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("=========Jwt Authentication Filter 진입 ==========");
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
//            토큰 없으면 인증하지 않고 다음 필터로 넘김
            filterChain.doFilter(request,response);
            return;
        }

        try{
            String token = header.substring(7);
            if(!jwtProcessor.validateToken(token)){
//                토큰은 있지만 유효하지 않으면 응답 종료(401)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

//            유효한 토큰일 경우 SecurityContext에 인증 객체 등록
            Long userId = jwtProcessor.getUserIdFromToken(token);
            CustomUserPrincipal principal = new CustomUserPrincipal(userId);
            UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(principal,null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(auth); //✅인증 성공
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request,response);//다음 필터로 넘김
    }
}
