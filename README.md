# 구해듀오

# 1. 프로젝트 소개

#### - 목적: 롤 게임 플레이어를 위한 듀오 파트너를 찾는 프로세스 자동화
#### - 필요성: 커뮤니티에서 팀원을 직접 찾는 번거로움 해결, 게임 플레이 경험 향상
#### - 개발 기간: 2023.08.09 ~ 2023.09.14 (37일)
#### - 인원 : Back-end 5명 (팀장-장용진)  
#### - 담당 역할 : 소셜로그인, WebSocket 실시간 채팅, Discord API 연동, ASW 클라우드 환경 아키텍쳐 구현 및 배포, 자유게시판 CRUD 구현 도움
<br>  

# 2. 아키텍처
![서비스 아키텍처](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/2d440bfe-54ab-499d-96dd-3ce89864b379)

<br>

# 3. 핵심 기능

### 1️⃣ 로그인과 회원가입
- 로그인 성공 시 JWT 토큰(Access Token, Refresh Token) 생성 및 발급 (_Spring Security, Jwt, Redis_)
- SNS(구글, 카카오, 네이버) 계정을 포함하여 로그인할 수 있음 (_Oauth2_)
- 이메일 인증번호를 통해 인증 (_Redis_)
<br>![회원가입](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/7c355c47-8dd1-42dc-9eda-c14d0c249223)

### 2️⃣ 마이페이지
- 회원 정보 조회, 수정, 탈퇴 (_CRUD_)
- RIOT API 연동 (_외부 API_)
<br>![마이페이지](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/43425711-9995-45ae-bad1-deafd06f0970)


### 3️⃣ 매칭서비스
- WebSocket을 이용한 매칭서비스
- 선택한 게임모드와 라인에 맞게 사용자의 롤 계정과 비슷한 티어의 사용자를 자동으로 매칭
- 매칭 성공시 상대방의 게임정보와 전적정보가 표시
- 상대방의 정보를 보고 수락/거절 선택
<br>![매칭](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/a7ff9d0d-dcbf-4e74-8525-85dd02317b82)


### 4️⃣ 채팅서비스
- WebSocket 채팅 (_STOMP_)
<br>![채팅](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/b0aeabf1-724d-4dac-a969-adb38829b587)
- 디스코드 음성채널 생성 & 연결 (_외부 API_)
<br>![디스코드](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/0f790ee0-724b-44c9-b0d5-edc08c54afbc)


### 5️⃣ 자유게시판
- 게시물 작성,조회,수정,삭제 (_CRUD_)
- 댓글 작성,조회,삭제
- 게시물 좋아요,북마크,신고
- 제목,작성자,내용으로 검색
- 인기순/최신순 조회
<br>![자유게시판](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/12cf162b-3e71-4b9c-b15e-b00ce65fe46e)


# 4. Stack

### Environment
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=GitHub%20Actions&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20Idea-000000?style=for-the-badge&logo=IntelliJ%20IDEA&logoColor=white)

### Back-end
![java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=Spring%20Security&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=Spring%20Data&logoColor=white) <br/>
![JSON Web Tokens](https://img.shields.io/badge/JSON%20Web%20Tokens-000000?style=for-the-badge&logo=JSON%20Web%20Tokens&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)

### DB/Server
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white)
![Amazon EC2](https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=Amazon%20EC2&logoColor=white)
![Amazon RDS](https://img.shields.io/badge/Amazon%20RDS-527FFF?style=for-the-badge&logo=Amazon%20RDS&logoColor=white)
![Amazon S3](https://img.shields.io/badge/Amazon%20S3-569A31?style=for-the-badge&logo=Amazon%20S3&logoColor=white)

### Front-end
![html](https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![css](https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![javascript](https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![jquery](https://img.shields.io/badge/jquery-0769AD?style=for-the-badge&logo=jquery&logoColor=white)
![vue.js](https://img.shields.io/badge/vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![bootstrap](https://img.shields.io/badge/bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)


### 협업 도구
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=Discord&logoColor=white)
<br><br>

# 5. 담당 구현 기능 상세 설명
- ### AWS 클라우드 아키텍쳐 구현 및 배포
  - EC2 내부에 DB를 생성하지 않고 RDS를 사용해서 외부에 DB 생성 및 연동
  - 회원 프로필 이미지, 매칭에 사용되는 롤 이미지, 자유게시판의 게시글 이미지등의 파일 관리를 위해 S3의 버킷을 생성하여 연동
  - 비용 절감을 위해 하나의 인스턴스에 운영, 개발 두개의 서버를 올리기로 결정, 독립 환경 구성이 가능한 Docker로 이미지 변환 후 컨테이너를 띄워 실행되게 구현<br>

   _개선할 점_
   - 안정적인 서비스 제공을 위해 리버스 프록시 기능을 수행하는 Nginx를 사용해 WAS의 보안 향상과 무중단 배포 도입이 필요. 
  
  
- ### WebSocket 실시간 채팅 구현 (STOMP)
  - pub/sub 방법을 사용해 채팅 구현에 적합한 STOMP 방식을 사용해 구현
  - 웹소켓 지원이 안되는 브라우저를 위해 Spring에서 제공하는 SockJS 기능 추가

   _개선할 점_
  - 욕설 등의 비매너 사용자의 신고내용 확인을 위해 채팅내역 Redis와 같은 NoSQL를 사용해 채팅 내역 저장 필요
  - 서버의 트래픽 증가를 대비해 외부 Kafka, RabbitMQ, ActiveMQ(JMS) 등의 외부 Messaging Queue를 사용해 비동기식 처리 필요

- ### 소셜로그인
  - Spring Security에서 제공하는 OAuth2 기능을 이용해 Naver, Kakao, Google 소셜로그인을 구현
  - 노출이 되면 안되는 민감 정보 Client Id, Client Secret등은 환경변수로 관리
  - 중복가입 방지를 위해 제공받은 정보를 토대로 첫 소셜로그인시 자동회원가입 진행, 이후 정상적으로 jwt 토큰 발급 

- ### 그 외
  - Refresh 토큰 하이 재킹 발생 가능성이 있어 인메모리 방식의 Redis를 사용하여 보안 강화
  - 가능한 MVC 패턴에 맞게 개발이 진행될 수 있도록 팀원들과의 코드리뷰 진행
