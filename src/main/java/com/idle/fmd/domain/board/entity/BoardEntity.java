package com.idle.fmd.domain.board.entity;

import com.idle.fmd.domain.board.dto.BoardCreateDto;
import com.idle.fmd.domain.file.entity.FileEntity;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE board SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Entity
@Table(name = "board")
public class BoardEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String title;

    private String content;

    private boolean deleted = Boolean.FALSE;


    @OneToMany(mappedBy = "board")
    private List<FileEntity> files = new ArrayList<>();

    // Board와 User의 연관관계 편의 메소드
    public void addBoardUser(UserEntity user) {
        this.user = user;
        user.getBoards().add(this);
    }

    public static BoardEntity createBoard(BoardCreateDto dto, UserEntity user) {
        return BoardEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .build();
    }

    public void createImageBoard(List<FileEntity> files) {
        this.files = files;
    }

    // 게시판 수정
    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
