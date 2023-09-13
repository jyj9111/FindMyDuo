
//alert(document.title);
// websocket & stomp initialize

var sock = new SockJS("/ws-stomp");
var ws = Stomp.over(sock);
var reconnect = 0;
// vue.js
var vm = new Vue({
    el: '#div-chatting',
    data: {
        roomId: '',
        roomname: '',
        sender: '',
        other:'',
        discordUrl: '',
        message: '',
        messages: []
    },
    async created() {
        this.roomId = localStorage.getItem('roomId');
        this.sender = localStorage.getItem('nickname');
        this.discordUrl = localStorage.getItem('discordUrl');
        this.other = localStorage.getItem('other');
        document.title = `${this.other}` + "와의 채팅방 - Find My Duo";
        this.findRoom();
    },
    methods: {
        findRoom: function() {
            axios.get('/chat/room/'+this.roomId)
                .then(response => {this.roomname = response.data.name;});
        },
        sendMessage: function() {
            ws.send("/app/chat/message", {}, JSON.stringify({type:'TALK', roomId:this.roomId, sender:this.sender, message:this.message}));
            this.message = '';
        },
        recvMessage: function(recv) {
            this.messages.push({"type":recv.type,"sender":recv.type=='ENTER'?'[알림]':recv.sender,"message":recv.message})
            this.$nextTick(function () {
                this.scrollToBottom();
            })
        },
        connectDiscord: function (){
            window.open(this.discordUrl);
        },
        // 메시지를 추가할 때마다 스크롤을 아래로 이동시키는 함수
        scrollToBottom() {
            const chatContainer = this.$refs.chatContainer;
            chatContainer.scrollTop = chatContainer.scrollHeight;
        },
    }
});

// A : /app/
// A : /topic/1
// B : /tipic/1

function connect() {
    // pub/sub event
    ws.connect({}, function(frame) {
        ws.subscribe("/topic/chat/room/"+vm.$data.roomId, function(message) {
            var recv = JSON.parse(message.body);
            vm.recvMessage(recv);
        });
        ws.send("/app/chat/message", {}, JSON.stringify({type:'ENTER', roomId:vm.$data.roomId, sender:vm.$data.sender}));
    }, function(error) {
        if(reconnect++ <= 5) {
            setTimeout(function() {
                console.log("connection reconnect");
                sock = new SockJS("/ws-stomp");
                ws = Stomp.over(sock);
                connect();
            },10*1000);
        }
    });
}
connect();