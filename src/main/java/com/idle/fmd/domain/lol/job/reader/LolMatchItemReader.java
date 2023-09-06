package com.idle.fmd.domain.lol.job.reader;

import com.idle.fmd.domain.lol.dto.LolMatchDto;
import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import com.idle.fmd.domain.lol.repo.LolAccountRepository;
import com.idle.fmd.domain.lol.repo.LolMatchRepository;
import com.idle.fmd.domain.lol.service.LolApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LolMatchItemReader implements ItemReader<List<LolMatchDto>> {
    private final LolApiService lolApiService;
    private final LolAccountRepository lolAccountRepository;
    private final LolMatchRepository lolMatchRepository;

    private Iterator<LolAccountEntity> lolAccountIterator;
    private Iterator<String> matchIdIterator;
    private String puuid;

    private static final int REQUEST_DELAY_MS = 1000; // 1000 = 1초

    @Override
    public List<LolMatchDto> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        // 처음 리더가 호출될 때 DB에 있는 계정 목록을 읽어온다.
        if(lolAccountIterator == null) {
            // 모든 LolAccountEntity 를 읽어온다.
            List<LolAccountEntity> lolEntities = lolAccountRepository.findAll();
            // Iterator 에 Entity 들을 저장
            lolAccountIterator = lolEntities.iterator();
        }

        List<LolMatchDto> dtoList = new ArrayList<>();

        // 다음 계정 정보가 있는 경우
        while (lolAccountIterator.hasNext()) {
            LolAccountEntity lolAccountEntity = lolAccountIterator.next();
            puuid = lolAccountEntity.getPuuid();
            String accountId = lolAccountEntity.getAccountId();
            List<String> matchIdList = lolApiService.getUserLolMatchId(puuid);
            matchIdIterator = matchIdList.iterator();

            // 다음 매치 ID가 있는 경우
            while (matchIdIterator.hasNext()) {
                String matchId = matchIdIterator.next();

                // 해당 유저의 매치 ID가 DB에 있는 지 판별하고 만약 있다면 그 이후 데이터는 처리하지 않음
                if(!lolMatchRepository.existsByLolAccountAccountIdAndMatchId(accountId, matchId)) {
                    LolMatchDto dto = lolApiService.getLolMatchInfo(puuid, matchId);
                    // 초당 요청 범위를 넘지 않도록 약간의 딜레이
                    Thread.sleep(REQUEST_DELAY_MS);
                    // 게임모드가 솔랭이나 자랭이 아니면 다음 매치 id로 넘어감
                    if(dto.getGameMode() == null) {
                        continue;
                    }
                    log.info(matchId + "- 전적 정보 추가");
                    dtoList.add(dto);
                } else {
                    // DB에 있는 매치 id면 그 이후 데이터는 이미 처리한 데이터니까 다음 계정으로 넘어감
                    // -> 매치 id 응답이 최신순으로 반환돼서 오기 때문에
                    break;
                }
            }
        }

        // dtoList 가 비지 않았다면 dtoList 를 반환하고 비었다면 null 반환
        return dtoList.isEmpty() ? null : dtoList;
    }
}
