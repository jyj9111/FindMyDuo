package com.idle.fmd.domain.lol.job.processor;

import com.idle.fmd.domain.lol.dto.LolInfoDto;
import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import com.idle.fmd.domain.lol.entity.LolInfoEntity;
import com.idle.fmd.domain.lol.repo.LolAccountRepository;
import com.idle.fmd.domain.lol.repo.LolInfoRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LolInfoItemProcessor implements ItemProcessor<LolInfoDto, LolInfoEntity> {
    private final LolAccountRepository lolAccountRepository;
    private final LolInfoRepository lolInfoRepository;

    // 영속성 관리를 위한 Entity 매니저
    private final EntityManager entityManager;

    @Override
    public LolInfoEntity process(LolInfoDto item) throws Exception {
        // LolAccountEntity 를 dto 에 있는 summonerId (소환사 계정 정보 id) 로 조회
       LolAccountEntity lolAccountEntity = lolAccountRepository.findBySummonerId(item.getSummonerId()).get();

       // 롤 계정 정보에서 연관된 롤 게임 정보를 가져온다.
       LolInfoEntity lolInfoEntity = lolAccountEntity.getLolInfo();

       // 게임 정보가 없으면 새로운 엔티티를 생성해서 넣어준다.
       if(lolInfoEntity == null) {
           lolInfoEntity = item.toEntity();
           // LolAccountEntity 와 LolInfoEntity 에 서로 연결
           lolAccountEntity.setLolInfo(lolInfoEntity);
           lolInfoEntity.addAccountInfo(lolAccountEntity);
       } else {
           lolInfoEntity.updateFromDto(item);
       }

       // Entity 를 영속성 컨텍스트에 저장하고 분리
        entityManager.persist(lolAccountEntity);
        entityManager.persist(lolInfoEntity);

        return lolInfoEntity;
    }
}
