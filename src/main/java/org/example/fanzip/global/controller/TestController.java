package org.example.fanzip.global.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @RequestMapping("/redis-test")
    @ResponseBody
    public String redisTest(){
        redisTemplate.opsForValue().set("test-key","Hello Redis!");

        String value = redisTemplate.opsForValue().get("test-key");

        return "Redis 연동 성공! 저장된 값: " + value;
    }
}
