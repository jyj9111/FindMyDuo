package com.idle.fmd.domain.lol.job;

import com.idle.fmd.domain.lol.dto.LolAccountResponseDto;
import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import com.idle.fmd.domain.lol.repo.LolAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class LolItemProcessor implements ItemProcessor<LolAccountResponseDto, LolAccountEntity> {
    private final LolAccountRepository lolAccountRepository;

    @Autowired
    public LolItemProcessor(LolAccountRepository lolAccountRepository) {
        this.lolAccountRepository = lolAccountRepository;
    }

    @Override
    public LolAccountEntity process(LolAccountResponseDto lolAccountDto) throws Exception {
        // DB 에서 이미 저장된 롤 계정 정보를 가져오기
        Optional<LolAccountEntity> existingEntity = lolAccountRepository.findBySummonerId(lolAccountDto.getSummonerId());

        // DB에 정보가 없는 경우 그냥 엔티티로 변환해서 넘겨줌
        if (existingEntity.isEmpty()) {
            return lolAccountDto.toEntity();
        }

        // 비교하기 위한 Entity get
        LolAccountEntity entity = existingEntity.get();

        // 최종 업데이트 날짜가 DB에 저장되어 있는 날짜와 다르면 변할 수 있는 정보 업데이트
        if(entity.getRevisionDate() != lolAccountDto.getRevisionDate()) {
            // 변할 수 있는 정보 업데이트 (소환사 닉네임, 업데이트 날짜, 소환사 레벨, 소환사 프로필 아이콘)
            entity.setName(lolAccountDto.getName());
            entity.setProfileIconId(lolAccountDto.getProfileIconId());
            entity.setRevisionDate(lolAccountDto.getRevisionDate());
            entity.setSummonerLevel(lolAccountDto.getSummonerLevel());

            return entity;
        }

        // 업데이트가 필요하지 않은 경우
        return null;
    }

}
