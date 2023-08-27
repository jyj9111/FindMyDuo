package com.idle.fmd.domain.lol.controller;

import com.idle.fmd.domain.lol.repo.LolAccountRepository;
import com.idle.fmd.domain.lol.service.LolApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lol")
public class LolApiController {
    private final LolAccountRepository lolAccountRepository;
    private final LolApiService lolApiService;

    // 닉네임 정보를 입력 받아 롤 계정 정보 DB 저장
    @PostMapping("/save")
    public ResponseEntity<String> saveUserLolAccount(
            Authentication authentication,
            @RequestParam String lolNickname) {
        String accountId = authentication.getName();
        lolApiService.save(accountId, lolNickname);
        return ResponseEntity.ok("유저 정보 저장 완료");
    }
}
