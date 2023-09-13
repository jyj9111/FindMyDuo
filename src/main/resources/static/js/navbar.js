import {isValidateToken} from "./keep-access-token.js";
let token = localStorage.getItem('token');

new Vue({
    el: '#navbar-app',
    data: {
        loggedIn: false, // 초기에는 로그인하지 않은 상태
        nickname: '',
        navbarProfileImage: ''
    },
    methods: {
        async checkLoginStatus() {
            console.log(token);
            // 토큰이 존재하면 로그인 상태로 간주한다.
            this.loggedIn = token != null;
            if(this.loggedIn) {
                this.nickname = localStorage.getItem('nickname')
                this.navbarProfileImage = localStorage.getItem('profileImage')
            }
        },
        async logout() {
            token = await isValidateToken()
            // 로그아웃 요청을 서버로 전송
            await axios.post('/users/logout')
                .then(() => {
                    // 로그아웃 성공 시 로컬 스토리지의 토큰 삭제
                    localStorage.clear();
                    Swal.fire({
                        icon: 'success',
                        title: '로그아웃 되었습니다.'
                    });
                    // 로그인 상태 업데이트
                    this.loggedIn = false;
                    setTimeout(() => {
                        location.href = '/main';
                    }, 2000);
                })
                .catch(error => {
                    console.error('로그아웃 실패: ', error);
                });
        },
    },
    mounted: async function (){
        await this.checkLoginStatus(); // 페이지가 로드될 때 로그인 상태 확인
    }
});