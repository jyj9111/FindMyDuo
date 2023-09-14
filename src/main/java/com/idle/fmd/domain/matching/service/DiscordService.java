package com.idle.fmd.domain.matching.service;

import com.idle.fmd.global.config.etc.DiscordConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordService {
    private final DiscordConfig discordConfig;

    // 디스코드 채널 생성 메서드
    public Optional<String> createChannel(String channelName, int limit) {
        Optional<String> url = Optional.empty();
        try {
            url = Optional.ofNullable(discordConfig.createVoiceChannel(channelName, limit));
        } catch (NullPointerException error) {
            error.printStackTrace();
        }
        return url;
    }

    // 매일 자정마다 비어있는 음성채널 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteChannel() {
        discordConfig.deleteVoiceChannel();
    }
}
