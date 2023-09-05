package com.idle.fmd.domain.matching;

import com.google.gson.Gson;
import com.idle.fmd.domain.lol.dto.LolMatchDto;
import com.idle.fmd.domain.lol.entity.LolMatchEntity;
import com.idle.fmd.domain.user.entity.UserEntity;
import com.idle.fmd.domain.user.service.CustomUserDetailsManager;
import com.idle.fmd.global.auth.jwt.JwtTokenUtils;
import io.jsonwebtoken.Claims;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
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
public class MatchingHandler extends TextWebSocketHandler {
    private final CustomUserDetailsManager manager;
    private final JwtTokenUtils jwtTokenUtils;
    private final TierReader tierReader;
    private final MatchingService matchingService;
    private final Gson gson = new Gson();
    private final List<WebSocketSession> sessions = new ArrayList<>();

    // 새로운 웹 소켓이 연결될 때 마다 실행되는 메서드 ( 매칭 대기열에 새로운 유저가 들어왔을 때 실행 )
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 내 정보를 session 의 속성에 등록하고 매칭 대기열에 추가
        registerClientInfo(session);
        // 조건에 맞는 상대와 매칭 시도
        connectUser(session);
    }

    // 메세지를 받으면 ( 수락 or 거절 ) 실행되는 메서드
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        session.getAttributes().put("answer", message.getPayload());

        WebSocketSession destination = null;
        for (WebSocketSession connected : sessions) {
            if (session.getAttributes().get("destination").equals(connected.getId().toString())) {
                destination = connected;
                break;
            }
        }

        String myAnswer = session.getAttributes().get("answer").toString().toString();
        if (destination != null) {
            String destinationAnswer = String.valueOf(destination.getAttributes().get("answer"));
            log.info("my: {}, des: {}", myAnswer, destinationAnswer);

            if (myAnswer.equals("accept")) {
                if (destinationAnswer.equals("null")) return;
                else if (destinationAnswer.equals("accept")) {
                    String roomName = String.format("%s.%s", session.getId(),destination.getId());
                    matchingService.openChatRoom(roomName, session, destination);
                    return;
                }
            }
        }

        if (myAnswer.equals("reject")) {
            TextMessage textMessage = new TextMessage("stop");
            session.sendMessage(textMessage);
            if(destination != null && destination.getAttributes().containsKey("answer")){
                destination.getAttributes().remove("destination");
                destination.getAttributes().remove("answer");
                textMessage = new TextMessage("continue");
                destination.sendMessage(textMessage);
            }
            sessions.remove(session);
            session.close();
        } else {
            session.getAttributes().remove("destination");
            destination.getAttributes().remove("answer");
            TextMessage textMessage = new TextMessage("continue");
            session.sendMessage(textMessage);
        }
    }

    // 세션 종료 시 연결된 웹 소켓을 관리하는 sessions 리스트에서 해당 세션 제거
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if(sessions.contains(session)) sessions.remove(session);
        log.info("웹 소켓 연결해제");
    }

    // 토큰에서 accountID 를 추출해내서 반환하는 메서드
    private String tokenToAccountId(String token) {
        String accountId = null;
        if (jwtTokenUtils.validate(token)) {
            Claims claims = jwtTokenUtils.parseClaims(token);
            accountId = claims.getSubject().toString();
        }

        return accountId;
    }

    // 내 정보를 session 의 속성에 등록하고 매칭 대기열에 추가하는 메서드
    private void registerClientInfo(WebSocketSession session) {
        // URL 에 포함된 쿼리 파라미터 부분을 가져온다
        String requestParameters = session.getUri().getQuery();
        // 쿼리 파라미터를 [변수=값] 형태로 배열에 저장
        String[] paramArray = requestParameters.split("&");

        // 배열에 저장된 각 문자열에서 값만 추출해서 다시 저장 ( 덮어쓰기 )
        for (int i = 0; i < 4; i++) {
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

        // 세션의 속성을 담을 수 있는 객체를 가져온 뒤 속성을 추가
        // 구해듀오 닉네임, 롤 닉네임, 모드, 내 포지션, 듀오 포지션, 티어, 모스트 1/2/3 챔피언 저장
        Map<String, Object> attributes = session.getAttributes();
        attributes.put("nickname", userEntity.getNickname());
        attributes.put("mode", mode);
        attributes.put("myLine", myLine);
        attributes.put("duoLine", duoLine);

        if (userEntity.getLolAccount() != null) {
            attributes.put("lolNickname", userEntity.getLolAccount().getName());

            // 모드에 따라 티어 속성을 다르게 설정 ( 솔랭 또는 자유랭 티어 )
            if (mode.equals("solo")) {
                attributes.put("tier", userEntity.getLolAccount().getLolInfo().getSoloTier());
                if(tierReader.soloTierToNumber(attributes.get("tier").toString()) == -1){
                    throw new RuntimeException("마스터 이상의 티어는 듀오를 할 수 없습니다.");
                }
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
        else {
            attributes.put("lolNickname", "");
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

        log.info("{} 님이 매칭 대기열에 입장하셨습니다.", userEntity.getNickname());
        log.info("sessionId: {}", session.getId());
    }

    // 조건에 맞는 유저를 찾고 서로의 정보를 상대방에게 전달해주는 메서드
    public void connectUser(WebSocketSession session) throws Exception {
        // 연결된 웹 소켓 세션 중 목적지가 없고 ( 매칭된 상태가 아니고 ), 모드/라인/티어 조건이 만족하는 상대방을 찾는다.
        for (WebSocketSession connected : sessions) {
            if (
                    !connected.getAttributes().containsKey("destination") &&
                            connected.getAttributes().get("mode").equals(session.getAttributes().get("mode")) &&
                            connected.getAttributes().get("duoLine").equals(session.getAttributes().get("myLine")) &&
                            connected.getAttributes().get("myLine").equals(session.getAttributes().get("duoLine"))
            ) {
                boolean tierInRange = false;
                String myTier = session.getAttributes().get("tier").toString();
                String duoTier = connected.getAttributes().get("tier").toString();
                String mode = session.getAttributes().get("mode").toString();

                // 솔로랭크 모드이면 솔로랭크티어, 자유랭크 모드이면 자유랭크 티어를 찾아서 티어조건이 맞는지 확인
                if (mode.equals("solo")) tierInRange = tierReader.soloTierInRange(myTier, duoTier);
                if (mode.equals("flex")) tierInRange = tierReader.flexTierInRange(myTier, duoTier);

                // 티어조건이 맞을 때 실행
                if (tierInRange) {
                    // 나의 정보를 상대방에게 전달
                    sendUserInfo(session, connected);
                    // 상대방의 정보를 나에게 전달
                    sendUserInfo(connected, session);
                }
            }
        }
    }

    // 서로의 정보를 전달해주는 메서드
    private void sendUserInfo(WebSocketSession mySession, WebSocketSession duoSession) throws Exception {
        // 웹 소켓 세션의 닉네임을 이용해서 유저 엔티티를 찾는다.
        UserEntity myEntity = manager.loadUserEntityByNickname(mySession.getAttributes().get("nickname").toString());
        List<LolMatchDto> myMatchList = new ArrayList<>();

        // 유저의 롤 전적기록을 찾아서 DTO 형태로 리스트에 저장
        if (myEntity.getLolAccount() != null) {
            List<LolMatchEntity> lolMatchEntities = myEntity.getLolAccount().getLolMatch();
            for (LolMatchEntity match : lolMatchEntities) {
                myMatchList.add(match.entityToDto());
            }
        }

        // 매칭 성공 시 응답 DTO 를 통해서 상대방에게 전달할 데이터를 준비
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

        // 해당 유저의 세션의 목적지를 설정한다.
        mySession.getAttributes().put("destination", duoSession.getId());

        // 매칭된 상대방에게 DTO 를 JSON 형태로 변환 후 전달한다.
        String json = gson.toJson(myInfo);
        TextMessage textMessage = new TextMessage(json);
        duoSession.sendMessage(textMessage);
    }
}
