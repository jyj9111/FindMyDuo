package com.idle.fmd.domain.lol.service;

import com.idle.fmd.domain.lol.dto.LolAccountDto;
import com.idle.fmd.domain.lol.dto.LolInfoDto;
import com.idle.fmd.domain.lol.dto.LolMatchDto;
import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import com.idle.fmd.domain.lol.entity.LolInfoEntity;
import com.idle.fmd.domain.lol.entity.LolMatchEntity;
import com.idle.fmd.domain.lol.repo.LolAccountRepository;
import com.idle.fmd.domain.lol.repo.LolInfoRepository;
import com.idle.fmd.domain.lol.repo.LolMatchRepository;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.exception.BusinessException;
import com.idle.fmd.global.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LolApiService {

    private final LolAccountRepository lolAccountRepository;
    private final UserRepository userRepository;
    private final LolInfoRepository lolInfoRepository;
    private final LolMatchRepository lolMatchRepository;


    @Value("${riot-api.api-key}")
    private String myKey;
    private final String riotUrl = "https://kr.api.riotgames.com";

    // URL Encoding
    public String UrlEncode(String url) {
        String enUrl = "";
        try {
            enUrl = URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enUrl;
    }

    // url 을 받아 응답을 요청하고 받응 응답을 파싱해서 JSONObject 로 반환
    public Object executeHttpGet(String url) {
        JSONParser jsonParser = new JSONParser();

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            // get 메서드와 URL 설정
            HttpGet httpGet = new HttpGet(url);

            // header 설정
            httpGet.addHeader("User-Agent", "Mozilla/5.0");
            httpGet.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
            httpGet.addHeader("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8");
            httpGet.addHeader("Origin", "https://developer.riotgames.com");
            httpGet.addHeader("X-Riot-Token", myKey);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                // 성공적으로 응답이 반환됐을 때만 파싱한 데이터 리턴
                if (response.getStatusLine().getStatusCode() == 200) {
                    ResponseHandler<String> handler = new BasicResponseHandler();
                    Object parsedObject = jsonParser.parse(handler.handleResponse(response));
//                    log.info("parsed JSON: " + parsedObject);
                    return parsedObject;
                } else {
                    log.error("응답 에러 " + response.getStatusLine().getStatusCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 회원이 소환사 닉네임과 함께 처음 연동 버튼을 눌렀을 때 계정 정보 저장
    public LolAccountDto save(String accountId, String summonerName) {
        // 토큰에 있는 유저 정보가 없는 정보일 때 예외 발생
        if (!userRepository.existsByAccountId(accountId)) {
            throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
        }

        // 토큰에 있는 유저 정보를 가져옴
        UserEntity user = userRepository.findByAccountId(accountId).get();

        // 롤 계정 정보를 가져오는 메서드 호출해서 dto 에 저장
        LolAccountDto lolAccountDto = getSummoner(summonerName);


        // DB에 이미 등록된 아이디일 경우 예외 발생
        String dtoAccountId = lolAccountDto.getAccountId();
        if (lolAccountRepository.existsByAccountId(dtoAccountId)) {
            throw new BusinessException(BusinessExceptionCode.DUPLICATED_USER_ERROR);
        };

        // 이미 연동되어 있는 롤 계정이 있다면 연동되어 있던 기존 롤 계정을 지워줌
        if (user.getLolAccount() != null) {
            LolAccountEntity deleteAccount = user.getLolAccount();
            lolAccountRepository.delete(deleteAccount);
        }

        // 새로운 롤 계정 정보 생성 해서 저장
        LolAccountEntity lolAccountEntity = lolAccountDto.toEntity();
        // userEntity 애도 연관관계 맺어줌
        lolAccountEntity.addLolAccountUser(user);
        lolAccountRepository.save(lolAccountEntity);

        // 롤 정보 Dto를 받아온다.
        String summonerId = lolAccountDto.getSummonerId();
        LolInfoDto lolInfoDto = getLolInfo(summonerId);
        LolInfoEntity lolInfoEntity = lolInfoDto.toEntity();

        // LolAccountEntity 와 LolInfoEntity 에 서로 연결
        lolAccountEntity.setLolInfo(lolInfoEntity);
        lolInfoEntity.addAccountInfo(lolAccountEntity);

        // DB에 저장
        lolInfoRepository.save(lolInfoEntity);

        // 매치 정보를 읽어오기
        String puuid = lolAccountDto.getPuuid();
        Long lolAccountId = lolAccountEntity.getId();
        List<String> matchList = getUserLolMatchId(puuid);

        // 모든 매치 ID를 읽어온다.
        try {
            for (String matchId : matchList) {
                LolMatchDto lolMatchDto = getLolMatchInfo(puuid, matchId);
                // 약간의 딜레이 주기
                Thread.sleep(1000); // 1초 딜레이
                // 게임모드가 솔랭이나 자랭이 아니면 다음 매치 id로 넘어감
                if (lolMatchDto.getGameMode() == null ) continue;

                // 각 모드가 10개씩까지만 저장되도록 설정
                if (lolMatchRepository.countByLolAccount_IdAndGameMode(lolAccountId, "SOLO") == 10 &&
                        lolMatchDto.getGameMode().equals("SOLO")) { continue; }

                if (lolMatchRepository.countByLolAccount_IdAndGameMode(lolAccountId, "FLEX") == 10 &&
                        lolMatchDto.getGameMode().equals("FLEX")) { continue; }

                log.info(matchId + "- 전적 정보 추가");

                // dto -> entity
                LolMatchEntity lolMatchEntity = lolMatchDto.toEntity();
                // 연관관계 맺어주기
                lolMatchEntity.addAccountMatch(lolAccountEntity);

                // 일단 반환된 entity 들을 저장
                lolMatchRepository.save(lolMatchEntity);
            }
        } catch (InterruptedException e) {
            // thread 예외
            e.printStackTrace();
        }

        return lolAccountDto;
    }

    // 소환사 계정 정보를 가져오는 메서드
    public LolAccountDto getSummoner(String summonerName) {
        // 닉네임 Encoding
        summonerName = UrlEncode(summonerName);
        String reqeustUrl = riotUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName;
        log.info(reqeustUrl);

        JSONObject jsonObject = (JSONObject) executeHttpGet(reqeustUrl);

        if (jsonObject != null) {
            // SummonerDto 에 데이터를 매핑하여 반환
            LolAccountDto dto = new LolAccountDto();
            dto.setAccountId((String) jsonObject.get("accountId"));
            dto.setProfileIconId((Long) jsonObject.get("profileIconId"));
            dto.setRevisionDate((Long) jsonObject.get("revisionDate"));
            dto.setName((String) jsonObject.get("name"));
            dto.setPuuid((String) jsonObject.get("puuid"));
            dto.setSummonerId((String) jsonObject.get("id"));
            dto.setSummonerLevel((Long) jsonObject.get("summonerLevel"));

            return dto;
        }

        // 해당되는 정보가 없을 경우 null 반환
        return null;
    }

    // 각종 데이터를 가져와서 LolDto 로 합치는 메서드
    public LolInfoDto getLolInfo(String summonerId) {
        LolInfoDto dto = new LolInfoDto();
        dto.setSummonerId(summonerId);

        // 티어 정보를 가져오는 API URL 호출
        String tierRequestUrl = riotUrl + "/lol/league/v4/entries/by-summoner/" + summonerId;
        JSONArray tierEntries = (JSONArray) executeHttpGet(tierRequestUrl);
        log.info(tierRequestUrl);

        // 티어정보를 dto에 저장
        if (tierEntries != null) {
            for (Object entryObject : tierEntries) {
                JSONObject entry = (JSONObject) entryObject;
                String queueType = (String) entry.get("queueType");

                if ("RANKED_SOLO_5x5".equals(queueType)) {
                    // LolDto 에 데이터를 매핑하여 반환
                    dto.setSoloTier((String) entry.get("tier"));
                    dto.setSoloRank((String) entry.get("rank"));
                    dto.setSoloWins((Long) entry.get("wins"));
                    dto.setSoloLosses((Long) entry.get("losses"));
                } else if ("RANKED_FLEX_SR".equals(queueType)) {
                    // LolDto 에 데이터를 매핑하여 반환
                    dto.setFlexTier((String) entry.get("tier"));
                    dto.setFlexRank((String) entry.get("rank"));
                    dto.setFlexWins((Long) entry.get("wins"));
                    dto.setFlexLosses((Long) entry.get("losses"));
                }
            }
        }

        // 랭크 티어가 없는 경우 UNRANKED 로 설정
        if (dto.getSoloTier() == null) {
            dto.setSoloTier("UNRANKED");
            dto.setSoloRank("");
        }
        if (dto.getFlexTier() == null) {
            dto.setFlexTier("UNRANKED");
            dto.setFlexRank("");
        }

        // 모스트 챔프 3개를 가져오는 API URL 호출
        String champRequestUrl = riotUrl + "/lol/champion-mastery/v4/champion-masteries/by-summoner/" + summonerId;
        JSONArray mostChampion = (JSONArray) executeHttpGet(champRequestUrl);
        log.info(champRequestUrl);

        // 모스트 챔프 3개를 Dto에 저장
        if (mostChampion != null) {
            for (int i = 0; i < 3 && i < mostChampion.size(); i++) {
                JSONObject champ = (JSONObject) mostChampion.get(i);
                if (i == 0) {
                    dto.setMostOneChamp((Long) champ.get("championId"));
                } else if (i == 1) {
                    dto.setMostTwoChamp((Long) champ.get("championId"));
                } else if (i == 2) {
                    dto.setMostThreeChamp((Long) champ.get("championId"));
                }
            }
        }
        return dto;
    }

    // 소환사의 매치 기록 ID를 가져오는 메서드
    public List<String> getUserLolMatchId(String puuid) {
        List<String> matchIdList = new ArrayList<>();

        // 매치 Id 정보를 가져오는 API URL 호출
        String matchIdRequestUrl = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid + "/ids/?type=ranked&start=0&count=30";
        JSONArray matchIdEntries = (JSONArray) executeHttpGet(matchIdRequestUrl);
        log.info(matchIdRequestUrl);

        // 매치 ID 정보를 List 에 저장
        if (matchIdEntries != null) {
            for (Object entryObject : matchIdEntries) {
                String matchId = (String) entryObject;
                matchIdList.add(matchId);
            }
            return matchIdList;
        }

        return null;
    }

    // 매치 기록 ID로 전적 정보를 가져오는 메서드
    public LolMatchDto getLolMatchInfo(String puuid, String matchId) {
        LolMatchDto dto = new LolMatchDto();
        dto.setPuuid(puuid);
        dto.setMatchId(matchId);

        // 전적 정보를 가져오는 API URL 호출
        String matchRequestUrl = "https://asia.api.riotgames.com/lol/match/v5/matches/" + matchId;
//        log.info(matchRequestUrl);
        JSONObject matchResponse = (JSONObject) executeHttpGet(matchRequestUrl);

        // 게임 정보 (모드, 시간) 를 dto 에 저장
        JSONObject matchInfo = (JSONObject) matchResponse.get("info");

        dto.setGameDuration((Long) matchInfo.get("gameDuration"));
        dto.setGameCreation((Long) matchInfo.get("gameCreation"));


        // QueueId 가 420 이면 솔로 랭크, QueueId 가 440 이면 자유 랭크
        Long queueId = (Long) matchInfo.get("queueId");
        if (queueId == 420) {
            dto.setGameMode("SOLO");
        } else if (queueId == 440) {
            dto.setGameMode("FLEX");
        }

        JSONArray matchDetails = (JSONArray) matchInfo.get("participants");

        // 주어진 소환사의 puuid 가 같다면 전적 정보를 dto 에 저장
        for (Object entryObject : matchDetails) {
            JSONObject entry = (JSONObject) entryObject;

            if (entry.get("puuid").equals(puuid)) {
                dto.setChampion((String) entry.get("championName"));
                dto.setChampionId((Long) entry.get("championId"));
                dto.setKills((Long) entry.get("kills"));
                dto.setDeaths((Long) entry.get("deaths"));
                dto.setAssists((Long) entry.get("assists"));
                dto.setTeamPosition((String) entry.get("teamPosition"));
                dto.setWin((Boolean) entry.get("win"));

                return dto;
            }
        }
        return null;
    }
}
