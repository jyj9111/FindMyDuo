package com.idle.fmd.domain.board.entity;

import com.idle.fmd.domain.board.dto.req.BoardCreateDto;
import com.idle.fmd.domain.comment.entity.CommentEntity;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    @Column(nullable = true)
    private Integer liked;

    @Column(nullable = true)
    private Integer bookmarked;

    private Integer reported;

    @Column(nullable = false)
    private Integer view;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<FileEntity> files = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<BookmarkEntity> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<LikeBoardEntity> likeBoards = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<ReportEntity> reports = new ArrayList<>();

    public static BoardEntity createBoard(BoardCreateDto dto, UserEntity user) {
        return BoardEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .files(new ArrayList<>())
                .user(user)
                .liked(0)
                .bookmarked(0)
                .reported(0)
                .view(0)
                .build();
    }

    // 게시판 이미지 추가
    public void changeImageBoard(List<FileEntity> files) {
        this.files = files;
    }

    // 게시판 제목, 내용 수정
    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 좋아요 + 1
    public void increaseLikeCount() {
        this.liked += 1;
    }

    public void decreaseLikeCount() {
        this.liked -= 1;
    }

    // 즐겨찾기 + 1
    public void increaseBookmarkCount() {
        this.bookmarked += 1;
    }

    public void decreaseBookmarkCount() {
        this.bookmarked -= 1;
    }

    // 신고 + 1
    public void increaseReportCount() {
        this.reported += 1;
    }

    public void decreaseReportCount() {
        this.reported -= 1;
    }

}
