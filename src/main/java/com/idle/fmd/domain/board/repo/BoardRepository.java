package com.idle.fmd.domain.board.repo;

import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    // 제목으로 검색
    Page<BoardEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // 유저로 검색
    Page<BoardEntity> findByUser(UserEntity user, Pageable pageable);

    // 내용으로 검색
    Page<BoardEntity> findByContentContainingIgnoreCase(String content, Pageable pageable);

}

