package com.idle.fmd.domain.lol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.idle.fmd.domain.lol.entity.LolMatchEntity;
import lombok.Data;

@Data
public class LolMatchDto {
    private String puuid;// 계정 puuid
    private String matchId; // 게임 매치 id
    private String gameMode; // 게임모드
    private Long gameCreation; // 게임 시작 스탬프
    private Long gameDuration; // 게임 시간 (초 -> 분 변환 필요)
    private String champion; // 챔피언 이름
    private Long championId; // 챔피언 아이디
    private Long kills; // 킬
    private Long deaths; // 데스
    private Long assists; // 어시스트
    private String teamPosition; // 팀 포지션
    private Boolean win; // 승리 여부

    // Dto 를 Entity 로 변환하는 메서드
    public LolMatchEntity toEntity() {
        return LolMatchEntity.builder()
                .matchId(this.matchId)
                .gameMode(this.gameMode)
                .gameCreation(this.gameCreation)
                .gameTime(this.gameDuration)
                .champion(this.champion)
                .championId(this.championId)
                .kills(this.kills)
                .deaths(this.deaths)
                .assists(this.assists)
                .teamPosition(this.teamPosition)
                .win(this.win)
                .build();
    }
}
