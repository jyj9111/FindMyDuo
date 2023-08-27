package com.idle.fmd.domain.board.repo;

import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.entity.LikeBoardEntity;
import com.idle.fmd.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeBoardRepository extends JpaRepository<LikeBoardEntity, Long> {

    Optional<LikeBoardEntity> findByBoardAndUser(BoardEntity board, UserEntity user);

    List<LikeBoardEntity> findAllByBoardId(Long boardId);
}
