package com.idle.fmd.domain.lol.repo;

import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LolAccountRepository extends JpaRepository<LolAccountEntity, Integer> {
}
