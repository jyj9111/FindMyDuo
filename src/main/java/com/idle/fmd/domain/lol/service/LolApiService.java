package com.idle.fmd.domain.lol.service;

import com.idle.fmd.domain.lol.dto.LolAccountResponseDto;
import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import com.idle.fmd.domain.lol.repo.LolAccountRepository;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.repo.UserRepository;
import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class LolApiService {

    private final LolAccountRepository lolAccountRepository;
    private final UserRepository userRepository;


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
    public LolAccountResponseDto save(String accountId, String summonerName) {
        // 토큰에 있는 유저 정보가 없는 정보일 때 예외 발생
        if(!userRepository.existsByAccountId(accountId)) {
            throw new BusinessException(BusinessExceptionCode.NOT_EXIST_USER_ERROR);
        }

        // 토큰에 있는 유저 정보를 가져옴
        UserEntity user = userRepository.findByAccountId(accountId).get();

        // 롤 계정 정보를 가져오는 메서드 호출해서 dto 에 저장
        LolAccountResponseDto dto = getSummoner(summonerName);

        // 이미 연동되어 있는 롤 계정이 있다면 연동되어 있던 기존 롤 계정을 지워줌
        if(user.getLolAccount() != null) {
            LolAccountEntity deleteAccount = user.getLolAccount();
            lolAccountRepository.delete(deleteAccount);
        }

        // 새로운 롤 계정 정보 생성 해서 저장
        LolAccountEntity lolAccountEntity = dto.toEntity();
        // userEntity 애도 연관관계 맺어줌
        lolAccountEntity.addLolAccountUser(user);
        lolAccountRepository.save(lolAccountEntity);
        return dto;
    }

    // 소환사 계정 정보를 가져오는 메서드
    public LolAccountResponseDto getSummoner(String summonerName) {
        // 닉네임 Encoding
        summonerName = UrlEncode(summonerName);
        String reqeustUrl = riotUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName;
        log.info(reqeustUrl);

        JSONObject jsonObject = (JSONObject) executeHttpGet(reqeustUrl);

        if(jsonObject != null) {
            // SummonerDto 에 데이터를 매핑하여 반환
            LolAccountResponseDto dto = new LolAccountResponseDto();
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
}
