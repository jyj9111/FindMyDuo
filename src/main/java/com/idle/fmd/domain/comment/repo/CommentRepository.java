package com.idle.fmd.domain.comment.repo;


import com.idle.fmd.domain.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
List<CommentEntity> findAllByBoardId(Long id);
}
