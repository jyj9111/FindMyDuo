package com.idle.fmd.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE file SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
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

    private boolean deleted = Boolean.FALSE;

    public static FileEntity createFile(BoardEntity board, String imageUrl) {
        return FileEntity.builder()
                .board(board)
                .imageUrl(imageUrl)
                .build();
    }
}
