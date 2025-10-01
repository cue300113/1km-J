package com.sky.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    @RequestMapping(value = {
            "/",
            "/login",
            "/register",
            "/enter-room",
            "/chat-room",
            "/reset-password",
            "/about"
    })
    public String index() {
        return "forward:/index.html";
    }
}