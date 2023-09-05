new Vue({
    el: '#navbar-app',
    data: {
        loggedIn: false // 초기에는 로그인하지 않은 상태
    },
    methods: {
        checkLoginStatus() {
            // 로컬 스토리지에서 토큰을 가져온다.
            const token = localStorage.getItem('token');

            // 토큰이 존재하면 로그인 상태로 간주한다.
            this.loggedIn = token != null;
        },
        logout() {
            // 로그아웃 요청을 서버로 전송
            axios.post('/users/logout')
                .then(() => {
                    // 로그아웃 성공 시 로컬 스토리지의 토큰 삭제
                    localStorage.removeItem('token');
                    alert("로그아웃 되었습니다.")
                    // 로그인 상태 업데이트
                    this.loggedIn = false;
                    location.href = '/main';
                })
                .catch(error => {
                    console.error('로그아웃 실패: ', error);
                });
        },
    },
    mounted: function (){
        this.checkLoginStatus(); // 페이지가 로드될 때 로그인 상태 확인
    }
});