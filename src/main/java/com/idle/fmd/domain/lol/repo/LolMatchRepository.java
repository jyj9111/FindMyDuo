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
}
