package com.idle.fmd.domain.lol.entity;

import com.idle.fmd.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "lol_account")
public class LolAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 구해듀오 자체 회원의 유저 아이디 (인덱스)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 암호화된 소환사 계정 id
    @Column(unique = true)
    private String accountId;

    // 암호화된 소환사 id (id와 이름이 겹쳐서 변경)
    @Column(unique = true)
    private String summonerId;

    // 암호화된 소환사 puuid
    @Column(unique = true)
    private String puuid;

    @Setter
    // 아이콘 id
    private long profileIconId;

    @Setter
    // 계정 날짜 (업데이트 날짜)
    private long revisionDate;

    @Setter
    // 소환사 이름(닉네임)
    private String name;

    @Setter
    // 소환사 레벨
    private long summonerLevel;

    // Lol 과 User 의 연관관게 편의 메소드
    public void addLolAccountUser(UserEntity user) {
        this.user = user;
    }
}
