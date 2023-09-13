package com.idle.fmd.domain.lol.entity;

import com.idle.fmd.domain.lol.dto.LolMatchDto;
import jakarta.persistence.*;
import lombok.*;

// 롤 전적 정보를 담는 테이블
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "lol_match")
public class LolMatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 계정 정보와 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lol_account_id")
    private LolAccountEntity lolAccount;

    // puuid 로 찾아야 함
    // 게임 매치 id
    private String matchId; // 게임 매치 id
    private String gameMode; // 게임모드
    private Long gameCreation; // 게임 시작 스탬프
    private Long gameTime; // 게임 시간 (초 -> 분 변환 필요)
    private String champion; // 챔피언 이름
    private Long championId; // 챔피언 아이디
    private Long kills; // 킬
    private Long deaths; // 데스
    private Long assists; // 어시스트
    private String teamPosition; // 팀 포지션
    private Boolean win; // 승리 여부

    // LolAccount 와 LolMatch 의 연관관게 편의 메서드
    public void addAccountMatch(LolAccountEntity lolAccount) {
        this.lolAccount = lolAccount;
    }


    public LolMatchDto entityToDto(){
        LolMatchDto lolMatchDto = new LolMatchDto();
        lolMatchDto.setMatchId(this.matchId);
        lolMatchDto.setGameMode(this.gameMode);
        lolMatchDto.setGameCreation(this.gameCreation);
        lolMatchDto.setGameDuration(this.gameTime);
        lolMatchDto.setChampion(this.champion);
        lolMatchDto.setChampionId(this.championId);
        lolMatchDto.setKills(this.kills);
        lolMatchDto.setDeaths(this.deaths);
        lolMatchDto.setAssists(this.assists);
        lolMatchDto.setTeamPosition(this.teamPosition);
        lolMatchDto.setWin(this.getWin());

        return lolMatchDto;
    }
}
