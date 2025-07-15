package org.example.fanzip.controller;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // 이렇게 하면 /WEB-INF/views/index.jsp 로 포워딩됨
    }
}