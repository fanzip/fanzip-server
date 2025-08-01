package org.example.fanzip.security;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {

    public static void addHttpOnlyCookie(HttpServletResponse response, String name, String value, int maxAge){
        log.info("===========Cookie Util============");
        Cookie cookie=new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);// TODO: Prod 환경에서 true로 변경
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void removeCookie(HttpServletResponse response, String name){
        addHttpOnlyCookie(response,name,"",0);
    }
}
