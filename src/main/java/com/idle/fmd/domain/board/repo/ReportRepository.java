package com.idle.fmd.domain.board.repo;

import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.entity.ReportEntity;
import com.idle.fmd.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    Optional<ReportEntity> findByBoardAndUser(BoardEntity board, UserEntity user);

    List<ReportEntity> findAllByBoardId(Long boardId);
}
