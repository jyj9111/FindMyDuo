new Vue({
    el: '#login-app',
    data: {
        accountId : '',
        password : ''
    },
    created() {

    },
    methods: {
        login: function () {
            axios.post('/users/login', {
                accountId: this.accountId,
                password: this.password
            }).then(response => {
                const token = response.data.token
                if (token == null) {
                    alert(response.data)
                    // 로그인에 실패하면 login 페이지로 다시 이동
                    location.href = "/login";
                } else {
                    localStorage.setItem("token", token);
                    localStorage.setItem("nickname", response.data.nickname);
                    alert("로그인에 성공했습니다.")
                    location.href = "/main";
                }
            }).catch(error => {
                const message=error.message;
                alert(message);
                console.log('login error='+message);
            })
        }
    }
});
