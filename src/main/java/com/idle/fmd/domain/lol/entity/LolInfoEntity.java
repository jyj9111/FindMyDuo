package com.idle.fmd.domain.lol.entity;

import com.idle.fmd.domain.lol.dto.LolInfoDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;


// 롤 정보 테이블 엔티티
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@DynamicUpdate // 업데이트 된 데이터만 추적해서 수정해주는 어노테이션
@Table(name = "lol_info")
public class LolInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 계정 정보와 연관관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lol_account_id")
    private LolAccountEntity lolAccount;

    @Setter
    // 모스트 1 챔피언 id
    private Long mostOneChamp;

    @Setter
    // 모스트 2 챔피언
    private Long mostTwoChamp;

    @Setter
    // 모스트 3 챔피언
    private Long mostThreeChamp;

    @Setter
    // 솔랭 티어
    private String soloTier;

    @Setter
    // 솔랭 티어 등급
    private String soloRank;

    @Setter
    // 솔랭 승리 횟수
    private Long soloWins;

    @Setter
    // 솔랭 패배 횟수
    private Long soloLosses;

    @Setter
    // 자랭 티어
    private String flexTier;

    @Setter
    // 자랭 티어 등급
    private String flexRank;

    @Setter
    // 자랭 승리 횟수
    private Long flexWins;

    @Setter
    // 자랭 패배 횟수
    private Long flexLosses;

    // LolAccount 와 LolInfo 의 연관관게 편의 메서드
    public void addAccountInfo(LolAccountEntity lolAccount) {
        this.lolAccount = lolAccount;
    }

    // 업데이트 메서드
    public void updateFromDto(LolInfoDto dto) {
        this.mostOneChamp = dto.getMostOneChamp();
        this.mostTwoChamp = dto.getMostTwoChamp();
        this.mostThreeChamp = dto.getMostThreeChamp();
        this.soloTier = dto.getSoloTier();
        this.soloRank = dto.getSoloRank();
        this.soloWins = dto.getSoloWins();
        this.soloLosses = dto.getSoloLosses();
        this.flexTier = dto.getFlexTier();
        this.flexRank = dto.getFlexRank();
        this.flexWins = dto.getFlexWins();
        this.flexLosses = dto.getFlexLosses();
    }

}
