package com.idle.fmd.domain.matching;

import lombok.Data;

@Data
public class MatchingSuccessDto {
    private String roomId;
    private String roomName;
    private String url;

    public MatchingSuccessDto(String roomId, String roomName, String url) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.url = url;
    }
}
