package com.idle.fmd.domain.lol.job.writer;

import com.idle.fmd.domain.lol.entity.LolMatchEntity;
import com.idle.fmd.domain.lol.repo.LolMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class LolMatchItemWriter implements ItemWriter<List<LolMatchEntity>> {
    private final LolMatchRepository lolMatchRepository;
    @Override
    public void write(Chunk<? extends List<LolMatchEntity>> matchEntitiesList) throws Exception {
        // writer 에는 청크 단위로 처리한 데이터들이 모두 들어온다.
        // 계정들의 matchEntity 들의 리스트를 순환하면서
        for(List<LolMatchEntity> matchEntities : matchEntitiesList) {
            // 각 matchEntity 를 하나씩 순환
            for(LolMatchEntity matchEntity : matchEntities) {
                String gameMode = matchEntity.getGameMode();

                // 일단 반환된 entity 들을 저장
                lolMatchRepository.save(matchEntity);

                // 유저의 puuid 의, 게임 모드를 GameCreation (게임 시작 스탬프) 를 기준으로 오래된 순으로 정렬된 리스트를 가져온다.
                List<LolMatchEntity> rankedData =
                        lolMatchRepository.findByLolAccountPuuidAndGameModeOrderByGameCreationAsc(
                                matchEntity.getLolAccount().getPuuid(), gameMode);

                // 각각 솔랭과 자랭 list 의 size 가 10보다 크면 오래된 데이터를 그만큼 삭제
                if (rankedData.size() > 10) {
                    for(int i = 0; i < rankedData.size() - 10; i++) {
                        lolMatchRepository.delete(rankedData.get(i));
                    }
                }
            }
        }
    }
}
