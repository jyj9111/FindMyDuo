import {isValidateToken} from "./keep-access-token.js";

let token = localStorage.getItem('token');
let webSocket;
let timeoutId;

new Vue({
    el: '#matching-app',
    data: {
        isMatching: false, // 매칭 됐는지 여부
        matchingButtonLabel: '매칭 시작',
        mode: '',
        myLine: '',
        duoLine: '',
        nickname:'',
        lolNickname: '',
        tier:'',
        rank: '',
        mostOne:'',
        mostTwo: '',
        mostThree:'',
        totalWins: '',
        totalLoses: '',
        discordUrl: '',
        matches: []
    },
    async created() {
        if (!token) {
            // 토큰이 없는 경우 로그인 페이지로 이동
            alert('로그인 후 이용해주세요.')
            location.href = '/login';
            return;
        }
    },
    methods: {
        async startMatching() {
            token = await isValidateToken();
            if(this.myLine == this.duoLine){
                alert("같은 역할군 간의 매칭은 불가능합니다.")
                return
            }
            const host = window.location.hostname;
            const port = window.location.port;
            const wsUrl = 'ws://'+host+':'+port+'/ws/matching?Authorization='+token+'&mode='+this.mode+'&myLine='+this.myLine+'&duoLine='+this.duoLine;
            webSocket = await new WebSocket(wsUrl);

            webSocket.onmessage = (msg) => {
                console.log(msg)
                try {
                    const data = JSON.parse(msg.data)
                    const chatMessage = document.createElement("div")
                    const message = document.createElement("p")
                    const matches = document.createElement("p")
                    if (data.roomId == undefined) {
                        message.innerText = data.username + ": " + data.message
                        message.innerText = "---------- 상대방 정보 ----------\n" +
                            "닉네임: " + data.nickname + "\n" +
                            "롤 닉네임: " + data.lolNickname + "\n" +
                            "티어: " + data.tier + data.rank + "\n" +
                            "모드: " + data.mode + "\n" +
                            "모스트1: " + data.mostOne + "\n" +
                            "모스트2: " + data.mostTwo + "\n" +
                            "모스트3: " + data.mostThree + "\n" +
                            "승리: " + data.totalWins + "\n" +
                            "패배: " + data.totalLoses + "\n"
                        ;

                        matches.innerText = "--------------- 전적 -------------------\n"

                        for(let i = 0; i < data.matchList.length; i++){
                            matches.innerText += `${data.matchList[i].gameMode}  ${data.matchList[i].champion}  ${data.matchList[i].teamPosition} ${data.matchList[i].kills} ${data.matchList[i].deaths} ${data.matchList[i].assists} ${data.matchList[i].win} \n`
                        }
                        chatMessage.appendChild(message)
                        chatMessage.appendChild(matches)
                        document.getElementById("div-matching").appendChild(chatMessage)
                        timeoutId = setTimeout(function(){
                            webSocket.send("reject")
                        }, 2000000)
                    } else {
                        localStorage.setItem('roomId', data.roomId);
                        localStorage.setItem('discordUrl', data.discordUrl)
                        const rName = data.roomName;
                        const other = rName.replace(localStorage.getItem('nickname'), "").replace("-","");
                        console.log('other: '+ other);
                        localStorage.setItem('other', other);
                        window.open('/chat/room/enter','_blank', 'scrollbars=yes, resizable=yes, location=no, width=800,height=800');
                    }
                } catch (e) {
                    const data = msg.data
                    const chatMessage = document.createElement("div");
                    const message = document.createElement("p");
                    message.innerText = data;

                    chatMessage.appendChild(message)
                    document.getElementById("div-matching").appendChild(chatMessage)
                }
            }
        },
        async matchingAccept(){
            clearTimeout(timeoutId);
            webSocket.send("accept")
        },
        async matchingReject(){
            clearTimeout(timeoutId);
            webSocket.send("reject")
        }
    }
});