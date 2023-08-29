package com.idle.fmd.domain.lol.dto;


import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import lombok.Data;

@Data
public class LolAccountDto {
    private String accountId; // 암호화된 소환사 계정 id
    private String summonerId; // 암호화된 소환사 id (Api 에서는 id로 되어있는데 id 컬럼이랑 겹쳐서 이름 변경)
    private Long profileIconId; // 아이콘 id
    private Long revisionDate; // 계정 날짜 (업데이트 날짜)
    private String name; // 소환사 이름(닉네임)
    private String puuid; // 암호화된 소환사 puuid
    private Long summonerLevel; // 소환사 레벨

    // Dto 를 Entity 로 변환하는 메서드
    public LolAccountEntity toEntity() {
        return LolAccountEntity.builder()
                .accountId(this.accountId)
                .summonerId(this.summonerId)
                .profileIconId(this.profileIconId)
                .revisionDate(this.revisionDate)
                .name(this.name)
                .puuid(this.puuid)
                .summonerLevel(this.summonerLevel)
                .build();
    }

}
