package com.idle.fmd.domain.board.repo;

import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    // 제목으로 검색
    Page<BoardEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // 유저로 검색
    Page<BoardEntity> findByUser(UserEntity user, Pageable pageable);

    // 내용으로 검색
    Page<BoardEntity> findByContentContainingIgnoreCase(String content, Pageable pageable);

    // 조회수 기능
    @Modifying
    @Query("update BoardEntity p set p.view = :view where p.id = :id")
    @Transactional
    int updateViewCount(@Param("view") Integer view, @Param("id") Long id);

}

