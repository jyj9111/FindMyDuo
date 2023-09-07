const token = localStorage.getItem('token');

new Vue({
    el: '#matching-app',
    data: {
        isMatching: false, // 매칭 됐는지 여부
        matchingButtonLabel: '매칭 시작'
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
        async toggleMatching() {
            // console.log('버튼 눌림');
            this.isMatching = !this.isMatching;
            this.matchingButtonLabel = this.isMatching ? '매칭 취소' : '매칭 시작';
        }
    }
});