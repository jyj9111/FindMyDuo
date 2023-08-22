package com.idle.fmd.domain.comment.entity;

import com.idle.fmd.domain.comment.dto.CommentRequestDto;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
//@SQLDelete(sql = "UPDATE board SET deleted = true WHERE id = ?")
//@Where(clause = "deleted = false")
@Entity
@Table(name = "comment")
public class CommentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long boardId;

    private String content;

    private String delete_at;

//    private boolean deleted_at;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;



}
