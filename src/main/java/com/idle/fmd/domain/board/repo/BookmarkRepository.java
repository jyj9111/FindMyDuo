package com.idle.fmd.domain.board.repo;

import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.entity.BookmarkEntity;
import com.idle.fmd.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {

    Optional<BookmarkEntity> findByBoardAndUser(BoardEntity board, UserEntity user);

    Page<BookmarkEntity> findAllByUser(UserEntity user, Pageable pageable);

    List<BookmarkEntity> findAllByBoardId(Long boardId);
}
