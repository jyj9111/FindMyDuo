package com.idle.fmd.domain.matching;

import com.google.gson.Gson;
import com.idle.fmd.domain.lol.dto.LolMatchDto;
import com.idle.fmd.domain.lol.entity.LolMatchEntity;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.service.CustomUserDetailsManager;
import com.idle.fmd.global.auth.jwt.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;

import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MatchingHandler extends TextWebSocketHandler{
    private final CustomUserDetailsManager manager;
    private final JwtTokenUtils jwtTokenUtils;
    private final TierReader tierReader;
    private final Gson gson = new Gson();
    private final List<WebSocketSession> sessions = new ArrayList<>();

    // 새로운 웹 소켓이 연결될 때 마다 실행되는 메서드 ( 매칭 대기열에 새로운 유저가 들어왔을 때 실행 )
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 내 정보를 session 의 속성에 등록하고 매칭 대기열에 추가
        registerClientInfo(session);
        connectUser(session);
    }

    // 토큰에서 accountID 를 추출해내서 반환하는 메서드
    private String tokenToAccountId(String token){
        String accountId = null;
        if(jwtTokenUtils.validate(token)){
            Claims claims = jwtTokenUtils.parseClaims(token);
            accountId = claims.getSubject().toString();
        }

        return accountId;
    }

    // 내 정보를 session 의 속성에 등록하고 매칭 대기열에 추가하는 메서드
    private void registerClientInfo(WebSocketSession session){
        // URL 에 포함된 쿼리 파라미터 부분을 가져온다
        String requestParameters = session.getUri().getQuery();
        // 쿼리 파라미터를 [변수=값] 형태로 배열에 저장
        String[] paramArray = requestParameters.split("&");

        // 배열에 저장된 각 문자열에서 값만 추출해서 다시 저장 ( 덮어쓰기 )
        for(int i = 0; i < 4; i++){
            String param = paramArray[i];
            paramArray[i] = param.substring(param.indexOf("=") + 1);
        }

        // parameter 1 : JWT, parameter 2 : 게임 모드, parameter 3 : 자신의 포지션, parameter 4 : 듀오의 포지션
        String token = paramArray[0];
        String mode = paramArray[1];
        String myLine = paramArray[2];
        String duoLine = paramArray[3];

        // 토큰에서 유저의 아이디를 뽑아내서 유저의 아이디를 이용해 해당 유저의 엔티티를 가져온다.
        String accountId = tokenToAccountId(token);
        UserEntity userEntity = manager.loadUserEntityByAccountId(accountId);
        log.info("{} 님이 매칭 대기열에 입장하셨습니다.", userEntity.getNickname());
        log.info("sessionId: {}", session.getId());

        // 세션의 속성을 담을 수 있는 객체를 가져온 뒤 속성을 추가
        // 구해듀오 닉네임, 롤 닉네임, 모드, 내 포지션, 듀오 포지션, 티어, 모스트 1/2/3 챔피언 저장
        Map<String, Object> attributes = session.getAttributes();
        attributes.put("nickname", userEntity.getNickname());
        attributes.put("mode", mode);
        attributes.put("myLine", myLine);
        attributes.put("duoLine", duoLine);

        if(userEntity.getLolAccount() != null) {
            attributes.put("lolNickname", userEntity.getLolAccount().getName());

            // 모드에 따라 티어 속성을 다르게 설정 ( 솔랭 또는 자유랭 티어 )
            if (mode.equals("solo")) {
                attributes.put("tier", userEntity.getLolAccount().getLolInfo().getSoloTier());
                attributes.put("rank", userEntity.getLolAccount().getLolInfo().getSoloRank());
                attributes.put("totalWins", userEntity.getLolAccount().getLolInfo().getSoloWins());
                attributes.put("totalLoses", userEntity.getLolAccount().getLolInfo().getSoloLosses());
            }

            if (mode.equals("flex")) {
                attributes.put("tier", userEntity.getLolAccount().getLolInfo().getFlexTier());
                attributes.put("rank", userEntity.getLolAccount().getLolInfo().getFlexRank());
                attributes.put("totalWins", userEntity.getLolAccount().getLolInfo().getFlexWins());
                attributes.put("totalLoses", userEntity.getLolAccount().getLolInfo().getFlexLosses());
            }

            // 모스트 1/2/3 챔피언 저장
            attributes.put("mostOne", userEntity.getLolAccount().getLolInfo().getMostOneChamp());
            attributes.put("mostTwo", userEntity.getLolAccount().getLolInfo().getMostTwoChamp());
            attributes.put("mostThree", userEntity.getLolAccount().getLolInfo().getMostThreeChamp());
        }
        // 롤 계정이 연동되어 있지 않을 때 속성 설정
        else{
            attributes.put("lolNickname","");
            attributes.put("tier", "UNRANKED");
            attributes.put("rank", "");
            attributes.put("totalWins", 0);
            attributes.put("totalLoses", 0);
            attributes.put("mostOne", 0);
            attributes.put("mostTwo", 0);
            attributes.put("mostThree", 0);
        }

        // 매칭 대기열에 추가
        sessions.add(session);
    }

    public void connectUser(WebSocketSession session) throws Exception{
        for(WebSocketSession connected: sessions){
            if(
                    !connected.getAttributes().containsKey("destination") &&
                    connected.getAttributes().get("mode").equals(session.getAttributes().get("mode")) &&
                    connected.getAttributes().get("duoLine").equals(session.getAttributes().get("myLine")) &&
                    connected.getAttributes().get("myLine").equals(session.getAttributes().get("duoLine"))
            ){
                boolean tierInRange = false;
                String myTier = session.getAttributes().get("tier").toString();
                String duoTier = connected.getAttributes().get("tier").toString();
                String mode = session.getAttributes().get("mode").toString();

                if(mode.equals("solo")) tierInRange = tierReader.soloTierInRange(myTier, duoTier);
                if(mode.equals("flex")) tierInRange = tierReader.flexTierInRange(myTier, duoTier);

                if(tierInRange) {
                    // 나의 정보를 상대방에게 전달
                    sendUserInfo(session, connected);
                    // 상대방의 정보를 나에게 전달
                    sendUserInfo(connected, session);
                }
            }
        }
    }
    public void sendUserInfo(WebSocketSession mySession, WebSocketSession duoSession) throws Exception{
        UserEntity myEntity = manager.loadUserEntityByNickname(mySession.getAttributes().get("nickname").toString());
        List<LolMatchDto> myMatchList = new ArrayList<>();

        if(myEntity.getLolAccount() != null){
            List<LolMatchEntity> lolMatchEntities = myEntity.getLolAccount().getLolMatch();
            log.info(lolMatchEntities.toString());
            for(LolMatchEntity match: lolMatchEntities){
                myMatchList.add(match.entityToDto());
            }
        }

        MatchingResponseDto myInfo = new MatchingResponseDto(
                mySession.getAttributes().get("nickname").toString(),
                mySession.getAttributes().get("lolNickname").toString(),
                mySession.getAttributes().get("mode").toString(),
                mySession.getAttributes().get("myLine").toString(),
                mySession.getAttributes().get("tier").toString(),
                mySession.getAttributes().get("rank").toString(),
                Long.parseLong(mySession.getAttributes().get("mostOne").toString()),
                Long.parseLong(mySession.getAttributes().get("mostTwo").toString()),
                Long.parseLong(mySession.getAttributes().get("mostThree").toString()),
                Long.parseLong(mySession.getAttributes().get("totalWins").toString()),
                Long.parseLong(mySession.getAttributes().get("totalLoses").toString()),
                myMatchList
        );

        duoSession.getAttributes().put("destination", mySession.getId());

        String json = gson.toJson(myInfo);
        TextMessage textMessage = new TextMessage(json);
        duoSession.sendMessage(textMessage);
    }
}
