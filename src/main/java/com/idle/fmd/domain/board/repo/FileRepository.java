package com.idle.fmd.domain.board.repo;

import com.idle.fmd.domain.board.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findAllByBoardId(Long boardId);
}
