package com.idle.fmd.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "file")
@Entity
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    private String imageUrl;

    public static FileEntity createFile(BoardEntity board, String imageUrl) {
        return FileEntity.builder()
                .board(board)
                .imageUrl(imageUrl)
                .build();
    }
}
