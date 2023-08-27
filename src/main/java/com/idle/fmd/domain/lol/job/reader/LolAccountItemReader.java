package com.idle.fmd.domain.lol.job.reader;

import com.idle.fmd.domain.lol.dto.LolAccountDto;
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
public class LolAccountItemReader implements ItemReader<LolAccountDto> {
    private final LolApiService lolApiService;
    private final LolAccountRepository lolAccountRepository;

    // 첫 Job 실행시 엔티티에 있는 모든 정보를 가져온다.
    private Iterator<String> accountIterator;

    // 나중에 예외 처리 추가할 것
    @Override
    public LolAccountDto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // 데이터를 아직 읽기 전이면 Iterator 가 null 이다.
        if(accountIterator == null) {
            // LolApiEntity의 닉네임들을 읽어온다.
            List<LolAccountEntity> lolEntities = lolAccountRepository.findAll();

            // 닉네임만 추출해서 Iterator로 변환
            accountIterator = lolEntities.stream()
                    .map(LolAccountEntity::getName)
                    .iterator();
        }

        // 다음 닉네임이 있는지 확인하고, 있으면 API 호출, 및 계정 정보 리턴
        if (accountIterator.hasNext()) {
            String name = accountIterator.next();
            log.info(name + "의 정보를 긁어옵니다.");
            return lolApiService.getSummoner(name);
        }

        // 데이터가 더 이상 없을 때 null 반환
        return null;
    }
}
