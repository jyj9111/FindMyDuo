package com.idle.fmd.domain.lol.repo;

import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LolAccountRepository extends JpaRepository<LolAccountEntity, Integer> {
    Optional<LolAccountEntity> findBySummonerId(String summonerId);

    Optional<LolAccountEntity> findByPuuid(String puuid);

    boolean existsByAccountId(String accountId);
}
