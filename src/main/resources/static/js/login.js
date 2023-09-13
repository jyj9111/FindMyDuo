new Vue({
    el: '#login-app',
    data: {
        accountId : '',
        password : ''
    },
    created() {

    },
    methods: {
        login: async function () {
            await axios.post('/users/login', {
                accountId: this.accountId,
                password: this.password
            })
                .then(response => {
                    const token = response.data.token
                    const profileImage = response.data.profileImage
                    if (token == null) {
                        alert(response.data)
                        // 로그인에 실패하면 login 페이지로 다시 이동
                        location.href = "/login";
                    } else {
                        localStorage.setItem("token", token);
                        localStorage.setItem("nickname", response.data.nickname);
                        if (profileImage != null) {
                            localStorage.setItem("profileImage", profileImage);
                        } else {
                            localStorage.setItem("profileImage", "/static/css/images/profile.png")
                        }
                        location.href = "/main";
                    }
                })
                .catch(error => {
                    const message = error.message;
                    Swal.fire({
                        icon: 'error',
                        title: '로그인 실패',
                        text: '입력된 정보가 잘못되었습니다.',
                    });
                    console.log('login error=' + message);
                })
        }
    }
});
