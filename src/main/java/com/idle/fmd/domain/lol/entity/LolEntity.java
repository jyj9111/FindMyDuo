package com.idle.fmd.domain.lol.entity;

import com.idle.fmd.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Data;


// 롤 테이블 엔티티
@Entity
@Data
@Table(name = "lol")
public class LolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 구해듀오 자체 회원의 유저 아이디 ( 인덱스 )
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 롤 닉네임
    private String nickname;

    // 티어
    private String tier;

    // 주 라인
    private String mainPosition;

    // 주 챔피언 3개
    private String likeChamp;
}
