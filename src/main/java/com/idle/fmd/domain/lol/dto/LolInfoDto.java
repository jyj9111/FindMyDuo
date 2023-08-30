package com.idle.fmd.domain.lol.dto;

import com.idle.fmd.domain.lol.entity.LolInfoEntity;
import lombok.Data;

@Data
public class LolInfoDto {
    private String summonerId; // 암호화된 소환사 id
    private long mostOneChamp;  // 모스트 1 챔피언 id
    private long mostTwoChamp; // 모스트 2 챔피언
    private long mostThreeChamp; // 모스트 3 챔피언
    private String soloTier; // 솔랭 티어
    private String soloRank; // 솔랭 티어 등급
    private long soloWins; // 솔랭 승리 횟수
    private long soloLosses; // 솔랭 패배 횟수
    private String flexTier; // 자랭 티어
    private String flexRank; // 자랭 티어 등급
    private long flexWins; // 자랭 승리 횟수
    private long flexLosses; // 자랭 패배 횟수

    // Dto 를 Entity 로 변환하는 메서드
    public LolInfoEntity toEntity() {
        return LolInfoEntity.builder()
                .mostOneChamp(this.mostOneChamp)
                .mostTwoChamp(this.mostTwoChamp)
                .mostThreeChamp(this.mostThreeChamp)
                .soloTier(this.soloTier)
                .soloRank(this.soloRank)
                .soloWins(this.soloWins)
                .soloLosses(this.soloLosses)
                .flexTier(this.flexTier)
                .flexRank(this.flexRank)
                .flexWins(this.flexWins)
                .flexLosses(this.flexLosses)
                .build();
    }

}
