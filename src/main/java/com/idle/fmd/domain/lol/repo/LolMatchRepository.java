package com.idle.fmd.domain.lol.repo;

import com.idle.fmd.domain.lol.entity.LolMatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LolMatchRepository extends JpaRepository<LolMatchEntity, Integer> {
    Boolean existsByMatchId(String matchId);

    // puuid 와 게임 모드에 해당하는 모든 LolMatchEntity 리스트 반환
    List<LolMatchEntity> findByLolAccountPuuidAndGameModeOrderByGameCreationAsc(String puuid, String gameMode);

    // 특정 유저의 매치 id가 있는지 판별
    boolean existsByLolAccountAccountIdAndMatchId(String accountId, String matchId);

    // 특정 유저의 특정 게임 모드의 데이터의 수 반환
    long countByLolAccount_IdAndGameMode(Long lolAccountId, String gameMode);
}
