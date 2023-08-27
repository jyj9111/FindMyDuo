package com.idle.fmd.domain.lol.job.writer;

import com.idle.fmd.domain.lol.entity.LolInfoEntity;
import com.idle.fmd.domain.lol.repo.LolInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LolInfoItemWriter implements ItemWriter<LolInfoEntity> {
    private final LolInfoRepository lolInfoRepository;

    @Override
    public void write(Chunk<? extends LolInfoEntity> items) throws Exception {
        // 처리된 Entity 를 repo 에 저장
        for (LolInfoEntity entity : items) {
            lolInfoRepository.save(entity);
        }
    }
}
