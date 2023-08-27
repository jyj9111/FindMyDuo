package com.idle.fmd.domain.lol.job.processor;

import com.idle.fmd.domain.lol.dto.LolMatchDto;
import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import com.idle.fmd.domain.lol.entity.LolMatchEntity;
import com.idle.fmd.domain.lol.repo.LolAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LolMatchItemProcessor implements ItemProcessor<List<LolMatchDto>, List<LolMatchEntity>> {

    private final LolAccountRepository lolAccountRepository;
    private List<LolMatchEntity> lolMatchEntities = new ArrayList<>();
    @Override
    public List<LolMatchEntity> process(List<LolMatchDto> items) throws Exception {
        // dto 로 반환된 item 들을 순환하면서 연관관계를 맺어주고 entity 로 변환해서 넘겨줌
        for(LolMatchDto item : items) {
            LolAccountEntity lolAccountEntity = lolAccountRepository.findByPuuid(item.getPuuid()).get();
            // dto -> entity
            LolMatchEntity lolMatchEntity = item.toEntity();
            // 연관관계 맺어주기
            lolMatchEntity.addAccountMatch(lolAccountEntity);
            // 리스트에 추가
            lolMatchEntities.add(lolMatchEntity);
        }

        return lolMatchEntities;
    }
}
