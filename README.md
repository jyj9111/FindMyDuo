# 구해듀오

# 1.프로젝트 소개

### 목적: 롤 게임 플레이어를 위한 듀오 파트너를 찾는 프로세스 자동화
### 필요성: 커뮤니티에서 팀원을 직접 찾는 번거로움 해결, 게임 플레이 경험 향상

## 팀원

| <img src="https://avatars.githubusercontent.com/u/130991633?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/89755903?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/83864280?s=32&v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/112999436?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/131418584?v=4" width="130" height="130"> |
|:-----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|
|                              [장용진](https://github.com/jyj9111)                               |                             [이민철](https://github.com/GrO0vy)                              |                             [이효주](https://github.com/Leehyoju97)                              |                              [김하늘](https://github.com/gureumi74)                              |                          [김구하](https://github.com/KoohaKim)                           |

### 배포주소: http://ec2-3-39-135-200.ap-northeast-2.compute.amazonaws.com:8081/main
     
설치 방법
``` bash
$ git clone https://github.com/Likelion-backend-IDLE/FindMyDuo.git
$ cd FindMyDuo
```

# 2.핵심 기능

### 1️⃣ 로그인과 회원가입
- 로그인 성공 시 JWT 토큰(Access Token, Refresh Token) 생성 및 발급
- SNS(구글, 카카오, 네이버) 계정을 포함하여 로그인할 수 있음
- 이메일 인증번호를 통해 인증
<br>![회원가입](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/7c355c47-8dd1-42dc-9eda-c14d0c249223)

### 2️⃣ 마이페이지
- 회원 정보 조회, 수정, 탈퇴
- riot api 연동
<br>![마이페이지](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/43425711-9995-45ae-bad1-deafd06f0970)


### 3️⃣ 매칭서비스
- websocket을 이용한 매칭서비스
- 선택한 게임모드와 라인에 맞게 사용자의 롤 계정과 비슷한 티어의 사용자를 자동으로 매칭
- 매칭 성공시 상대방의 게임정보와 전적정보가 표시
- 상대방의 정보를 보고 수락/거절 선택
<br>![매칭](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/a7ff9d0d-dcbf-4e74-8525-85dd02317b82)


### 4️⃣ 채팅서비스
- websocket 채팅
<br>![채팅](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/b0aeabf1-724d-4dac-a969-adb38829b587)
- 디스코드
<br>![디스코드](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/0f790ee0-724b-44c9-b0d5-edc08c54afbc)


### 5️⃣ 자유게시판
- 게시물 작성,조회,수정,삭제
- 댓글 작성,조회,삭제
- 게시물 좋아요,북마크,신고
- 제목,작성자,내용으로 검색
- 인기순/최신순 조회
<br>![자유게시판](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/12cf162b-3e71-4b9c-b15e-b00ce65fe46e)


# 3.개발환경 및 사용기술

### Environment
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=GitHub%20Actions&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20Idea-000000?style=for-the-badge&logo=IntelliJ%20IDEA&logoColor=white)

### Back-end
![java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=Spring%20Security&logoColor=white) <br/>
![JSON Web Tokens](https://img.shields.io/badge/JSON%20Web%20Tokens-000000?style=for-the-badge&logo=JSON%20Web%20Tokens&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)

### Front-end
![html](https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![css](https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![javascript](https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![jquery](https://img.shields.io/badge/jquery-0769AD?style=for-the-badge&logo=jquery&logoColor=white)
![vue.js](https://img.shields.io/badge/vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![bootstrap](https://img.shields.io/badge/bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)

### DB/Server
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white)
![Amazon EC2](https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=Amazon%20EC2&logoColor=white)
![Amazon RDS](https://img.shields.io/badge/Amazon%20RDS-527FFF?style=for-the-badge&logo=Amazon%20RDS&logoColor=white)

### 협업 도구
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=Discord&logoColor=white)


### 4.ERD
![image](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/6e86980d-471f-49bb-b244-5064a4590c18)

### 5. 서비스 아키텍처
![서비스 아키텍처](https://github.com/Likelion-backend-IDLE/FindMyDuo/assets/83864280/2d440bfe-54ab-499d-96dd-3ce89864b379)

