package com.idle.fmd.domain.user.entity;


import com.idle.fmd.domain.board.entity.BookmarkEntity;
import com.idle.fmd.domain.board.entity.LikeBoardEntity;
import com.idle.fmd.domain.board.entity.ReportEntity;
import com.idle.fmd.domain.comment.entity.CommentEntity;
import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import com.idle.fmd.domain.board.entity.BoardEntity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 유저 테이블 엔티티
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name= "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    // UserEntity 가 삭제되면 연동된 계정 정보도 삭제되도록 설정
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private LolAccountEntity lolAccount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BoardEntity> boards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<BookmarkEntity> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<LikeBoardEntity> likeBoards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ReportEntity> reports = new ArrayList<>();

    // CustomUserDetails -> UserEntity 변환 정적 팩토리 메소드
    public static UserEntity fromCustomUserDetails(CustomUserDetails userDetails) {
        new UserEntity();
        return UserEntity.builder()
                .accountId(userDetails.getUsername())
                .password(userDetails.getPassword())
                .email(userDetails.getEmail())
                .nickname(userDetails.getNickname())
                .profileImage(userDetails.getProfileImage())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // UserEntity 업데이트 메소드
    public void updateUser(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    // UserEntity 비밀번호 변경 메소드
    public void changePassword(String password) {
        this.password = password;
    }

    // User 프로필 업데이트 메소드
    public void updateProfileImage(String imageUrl) {
        this.profileImage = imageUrl;
    }

}
