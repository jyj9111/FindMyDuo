package com.idle.fmd.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserViewController {
    @GetMapping("/main")
    public String homePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/mypage")
    public String myPage() {
        return "mypage";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }
}
