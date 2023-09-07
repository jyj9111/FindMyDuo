package com.idle.fmd.domain.matching;

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

    public Optional<String> createChannel(String channelName, int limit) {
        Optional<String> url = Optional.empty();
        try {
            url = Optional.ofNullable(discordConfig.createVoiceChannel(channelName, limit));
        } catch (NullPointerException error) {
            error.printStackTrace();
        }
        return url;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteChannel() {
        discordConfig.deleteVoiceChannel();
    }
}
