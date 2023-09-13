package com.idle.fmd.domain.lol.job.writer;

import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import com.idle.fmd.domain.lol.repo.LolAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LolAccountItemWriter implements ItemWriter<LolAccountEntity> {
    private final LolAccountRepository lolAccountRepository;

    @Autowired
    public LolAccountItemWriter(LolAccountRepository lolAccountRepository) {
        this.lolAccountRepository = lolAccountRepository;
    }

    @Override
    public void write(Chunk<? extends LolAccountEntity> items) throws Exception {
        // 처리된 Entity 를 repository 에 저장
        for(LolAccountEntity entity : items) {
            // 업데이트가 필요한 경우 업데이트된 정보를 저장
            if (entity != null) {
                lolAccountRepository.save(entity);
            }
        }
    }
}
