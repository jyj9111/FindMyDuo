package com.idle.fmd.domain.comment.entity;

import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "comment")
public class CommentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity board;
}
