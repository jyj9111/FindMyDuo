package com.idle.fmd.domain.board.entity;

import com.idle.fmd.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "like_board")
public class LikeBoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // true = 좋아요, false = 좋아요 취소
    @Column(nullable = false)
    private boolean status;

    public LikeBoardEntity(BoardEntity board, UserEntity user) {
        this.board = board;
        this.user = user;
        this.status = true;
    }
}
