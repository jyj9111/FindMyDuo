package com.idle.fmd.domain.user.entity;


import com.idle.fmd.domain.lol.entity.LolEntity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// 유저 테이블 엔티티
@Entity
@Data
@Table(name= "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 규해듀오 회원의 아이디
    @Column(unique = true, nullable = false)
    private String accountId;

    // 이메일
    @Column(unique = true, nullable = false)
    private String email;

    // 구해듀오 회원의 닉네임
    @Column(unique = true, nullable = false)
    private String nickname;

    // 규해듀오 회원의 패스워드
    @Column(nullable = false)
    private String password;

    // 구해듀오 가입 날짜
    private LocalDateTime createdAt;

    // 구해듀오 회원의 프로필 이미지 정보
    private String profileImage;

    // 구해듀오 회원의 롤 계정정보
    @OneToOne
    @JoinColumn(name = "lol_account")
    private LolEntity lolAccount;

}
