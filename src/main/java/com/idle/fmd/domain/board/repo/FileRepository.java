package com.idle.fmd.domain.board.repo;

import com.idle.fmd.domain.board.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
