package com.idle.fmd.domain.lol.job.reader;

import com.idle.fmd.domain.lol.dto.LolInfoDto;
import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import com.idle.fmd.domain.lol.repo.LolAccountRepository;
import com.idle.fmd.domain.lol.service.LolApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LolInfoItemReader implements ItemReader<LolInfoDto> {
    private final LolApiService lolApiService;
    private final LolAccountRepository lolAccountRepository;

    private Iterator<LolAccountEntity> lolAccountIterator;
    @Override
    public LolInfoDto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        log.info("티어 정보 리더 시작");
        // 데이터를 아직 읽기 전이면 Iterator 가 null 이 된다.
        if(lolAccountIterator == null) {
            // LolAccountEntity 를 읽어온다.
            List<LolAccountEntity> lolEntities = lolAccountRepository.findAll();
            // Iterator 에 Entity 들을 저장
            lolAccountIterator = lolEntities.iterator();
        }

        // 다음 LolInfoEntity 가 있는지 확인하고 있으면 SummonerId (암호화된 소환사 ID) 를 추출해서 API 호출
        if (lolAccountIterator.hasNext()) {
            String summonerId = lolAccountIterator.next().getSummonerId();
            log.info("소환사의 암호화 된 계정 : " + summonerId);
            Thread.sleep(500);
            return lolApiService.getLolInfo(summonerId);
        }

        // 데이터가 더 이상 없으면 null 반환
        return null;
    }
}
