import {isValidateToken} from "./keep-access-token.js";

let token = localStorage.getItem('token');
let webSocket;
let time = 120;
let timeoutId;
let intervalId;

new Vue({
    el: '#matching-app',
    data: {
        mode: 'SOLO',
        myLine: 'TOP',
        duoLine: 'TOP',
        nickname:'',
        profileImg:'',
        lolNickname: '',
        tier:'',
        tierImg:'',
        mostOne:'',
        mostTwo: '',
        mostThree:'',
        totalWins: '',
        totalLoses: '',
        totalRate: '',
        discordUrl: '',
        matches: []
    },
    async created() {
        if (!token) {
            // 토큰이 없는 경우 로그인 페이지로 이동
            // alert('로그인 후 이용해주세요.')
            Swal.fire({
                icon: 'error',
                title: '로그인 후 이용해 주세요',
                confirmButtonText: '확인'
            }).then((result) => {
                if (result.isConfirmed) {
                    location.href = '/login';
                }
            });
            return;
        }
    },
    methods: {
        async startMatching() {
            token = await isValidateToken();
            if(this.myLine == this.duoLine){
                Swal.fire({
                    icon: 'error',
                    title: '같은 역할군 같의 매칭은 불가능 합니다.',
                    timer: 1500
                });
                return
            }
            const host = window.location.hostname;
            const port = window.location.port;
            const wsUrl = 'ws://'+host+':'+port+'/ws/matching?Authorization='+token+'&mode='+this.mode+'&myLine='+this.myLine+'&duoLine='+this.duoLine;
            webSocket = await new WebSocket(wsUrl);

            document.getElementById("div-loading").style.display = ""
            document.getElementById("btn-matching-start").style.display = "none"
            document.getElementById("btn-matching-stop").style.display = ""

            webSocket.onmessage = (msg) => {
                console.log(msg)
                try {
                    const data = JSON.parse(msg.data)
                    if (data.roomId == undefined) {
                        this.nickname = data.nickname
                        this.profileImg = data.profileImg
                        this.lolNickname = data.lolNickname
                        this.tier = data.tier + data.rank
                        this.tierImg = data.tierImg
                        this.mode = data.mode
                        this.mostOne = data.mostOne
                        this.mostTwo = data.mostTwo
                        this.mostThree = data.mostThree
                        this.totalWins = data.totalWins
                        this.totalLoses = data.totalLoses
                        this.totalRate =
                            parseFloat(data.totalWins) + parseFloat(data.totalLoses) == 0  ? "0%" : Math.round(parseFloat(data.totalWins) / (parseFloat(data.totalWins) + parseFloat(data.totalLoses)) * 100) + "%"

                        for(let i = 0; i < data.matchList.length; i++){
                            this.matches.push(data.matchList[i])
                        }

                        document.getElementById("btn-matching-stop").style.display = "none"
                        document.getElementById("div-loading").style.display = "none"
                        document.getElementById("div-info").style.display =""
                        document.getElementById("div-answer").style.display = ""
                        timeoutId = setTimeout(function(){
                            webSocket.send("reject")
                        }, 120000)

                        document.getElementById("time").innerText = time
                        intervalId = setInterval(  function(){
                            document.getElementById("time").innerText = --time;
                        }, 1000);
                    } else {
                        localStorage.setItem('roomId', data.roomId);
                        localStorage.setItem('discordUrl', data.discordUrl)
                        const rName = data.roomName;
                        const other = rName.replace(localStorage.getItem('nickname'), "").replace("-","");
                        console.log('other: '+ other);
                        localStorage.setItem('other', other);
                        clearInterval(intervalId);
                        window.open('/chat/room/enter','_blank', 'scrollbars=yes, resizable=yes, location=no, width=800,height=800');
                        setTimeout(function () {
                            location.href = "/matching"
                        }, 5000)
                    }
                } catch (e) {
                    const data = msg.data
                    if(data == 'stop'){
                        location.href = "/matching"
                    }
                    else if(data == 'continue'){
                        clearTimeout(timeoutId);
                        clearInterval(intervalId);
                        time = 120;
                        document.getElementById("btn-matching-stop").style.display = ""
                        document.getElementById("div-info").style.display = "none"
                        document.getElementById("div-loading").style.display = ""
                    }
                }
            }
        },
        async stopMatching(){
            await clearTimeout(timeoutId);
            await clearInterval(intervalId);
            time = 120;
            webSocket.close();
            document.getElementById("div-loading").style.display = "none"
            document.getElementById("btn-matching-start").style.display = ""
            document.getElementById("btn-matching-stop").style.display = "none"
        },
        async matchingAccept(){
            await clearTimeout(timeoutId);
            webSocket.send("accept")
        },
        async matchingReject(){
            await clearTimeout(timeoutId);
            await clearInterval(intervalId);
            time = 120;
            webSocket.send("reject")
        }
    }
});