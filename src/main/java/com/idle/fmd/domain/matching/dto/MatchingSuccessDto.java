package com.idle.fmd.domain.matching.dto;

import lombok.Data;

@Data
public class MatchingSuccessDto {
    private String roomId;
    private String roomName;
    private String url;
    private String discordUrl;

    public MatchingSuccessDto(String roomId, String roomName, String url, String discordUrl) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.url = url;
        this.discordUrl = discordUrl;
    }
}
