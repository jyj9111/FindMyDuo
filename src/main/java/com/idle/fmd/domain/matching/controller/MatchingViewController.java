package com.idle.fmd.domain.matching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MatchingViewController {
    @GetMapping("/matching")
    public String homePage() {
        return "matching/matching";
    }

    @GetMapping("/chat")
    public String chatPage() { return "matching/chat"; }
}
