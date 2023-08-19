package com.idle.fmd.domain.file.entity;

import com.idle.fmd.domain.board.entity.BoardEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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

    // File과 Board의 연관관계 편의메소드
    private void addFileBoard(BoardEntity board) {
        this.board = board;
        board.getFiles().add(this);
    }

    public static FileEntity ofEntity(BoardEntity board, String imageUrl) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setImageUrl(imageUrl);
        fileEntity.addFileBoard(board);

        return fileEntity;
    }
}
