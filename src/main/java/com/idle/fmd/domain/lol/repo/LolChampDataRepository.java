package com.idle.fmd.domain.lol.repo;

import com.idle.fmd.domain.lol.entity.LolChampDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LolChampDataRepository extends JpaRepository<LolChampDataEntity, Long> {
    LolChampDataEntity findByChampCode(Long champCode);
}
